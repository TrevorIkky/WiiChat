package com.org.wiichat.core

import android.content.Context
import android.util.Log
import com.org.wiichat.core.room.WiiDatabase
import com.org.wiichat.pojo.MessageObject
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class MessageTaskHandler(context: Context, dbInstance: WiiDatabase) {
    private var serverSocket: ServerSocket? = null
    private var socket: Socket? = null
    private val TIMEOUT = 2000
    private val PORT = 2828
    private val TAG = "MessageTaskHandler"
    private val ctx = context

    suspend fun createClient(address: String) =
        withContext(Dispatchers.IO) {
            try {
                socket = Socket()
                socket!!.bind(null)
                socket!!.connect(InetSocketAddress(address, PORT), TIMEOUT)
                handleMessage()
            } catch (ex: Exception) {
                throw  ex
            }
        }

    suspend fun createServer() = withContext(Dispatchers.IO) {
        try {
            serverSocket = ServerSocket(PORT)
            socket = serverSocket?.accept()
            handleMessage()
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun sendMessage(o: Any?) = withContext(Dispatchers.IO) {
        try {
            val byteOs = ByteArrayOutputStream()
            val socketOutputStream = socket!!.getOutputStream()
            val objectOutputStream = ObjectOutputStream(byteOs)
            objectOutputStream.writeObject(o)
            if (byteOs.toByteArray().isNotEmpty()) {
                socketOutputStream.write(byteOs.toByteArray())
            }
        } catch (ex: Exception) {
            throw ex
        } finally {
            //  return@withContext true
        }
    }

    suspend fun handleMessage() = withContext(Dispatchers.IO) {
        val socketInputStream = socket!!.getInputStream()
        var objectFromInputStream: Any? = null
        while (socket != null) {
            try {
                if (socketInputStream.available() != 0) {
                    Log.d(TAG, "Input stream available.")
                    val objectInputStream = ObjectInputStream(socketInputStream)
                    objectFromInputStream = objectInputStream.readObject()
                    objectFromInputStream?.let {
                        Log.d(TAG, (objectFromInputStream as MessageObject).message)
                        launch(Dispatchers.Main) {
                            Toasty.success(
                                ctx,
                                (objectFromInputStream as MessageObject).message,
                                Toasty.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (ex: Exception) {
                throw ex
            }
            delay(500)
        }
    }
}