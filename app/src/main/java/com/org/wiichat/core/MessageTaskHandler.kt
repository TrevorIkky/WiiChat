package com.org.wiichat.core

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class MessageTaskHandler {
    private var serverSocket: ServerSocket? = null
    private var socket = Socket()
    private val TIMEOUT = 2000
    private val TAG = "MessageTaskHandler"

    suspend fun sendMessage(messageObject: Any, address: String, port: Int) =
        withContext(Dispatchers.IO) {
            socket.bind(null)
            socket.connect(InetSocketAddress(address, port), TIMEOUT)
            val outputStream = socket.getOutputStream()
            val byteOs = ByteArrayOutputStream()
            try {
                val objectOutputStream = ObjectOutputStream(byteOs)
                objectOutputStream.writeObject(messageObject)
                outputStream.write(byteOs.toByteArray())
            } catch (e: Exception) {
                Log.d(TAG, e!!.message)
            } finally {
                byteOs.flush()
                outputStream.close()
                socket.takeIf { socket.isConnected }.apply {
                    this!!.close()
                }
                return@withContext true
            }
        }

    suspend fun listenForMessage(port: Int) = withContext(Dispatchers.IO) {
        serverSocket = ServerSocket(port)
        //blocking call accept()
        val clientSocket = serverSocket?.accept()
        var objectFromInputStream: Any? = null
        try {
            val inputStream = clientSocket?.getInputStream()
            inputStream?.let { byteArrayInputStream ->
                val objectInputStream = ObjectInputStream(inputStream)
                objectFromInputStream = objectInputStream.readObject()
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        } finally {
            serverSocket?.takeIf { !serverSocket!!.isClosed }.also {
                serverSocket?.close()
                return@withContext objectFromInputStream
            }
        }

    }
}