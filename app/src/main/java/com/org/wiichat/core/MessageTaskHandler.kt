package com.org.wiichat.core

import android.content.Context
import android.util.Log
import com.org.wiichat.core.room.Chat
import com.org.wiichat.core.room.User
import com.org.wiichat.core.room.WiiDatabase
import com.org.wiichat.pojo.MessageObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class MessageTaskHandler(context: Context, wiiDbInstance: WiiDatabase) {
    private var serverSocket: ServerSocket? = null
    private var socket: Socket? = null
    private val TIMEOUT = 2000
    private val PORT = 2828
    private val TAG = "MessageTaskHandler"
    private val ctx = context
    private val dbInstance = wiiDbInstance

    suspend fun createClient(address: String) =
        withContext(Dispatchers.IO) {
            try {
                socket = Socket()
                socket!!.bind(null)
                socket!!.connect(InetSocketAddress(address, PORT), TIMEOUT)
                handleMessage()
            } catch (ex: Exception) {

            }
        }

    suspend fun createServer() = withContext(Dispatchers.IO) {
        try {
            serverSocket = ServerSocket(PORT)
            socket = serverSocket?.accept()
            handleMessage()
        } catch (ex: Exception) {

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
                        val messageObject = (objectFromInputStream as MessageObject)
                        val userObj =
                            dbInstance.userDao().getUserFromId(messageObject.userId!!.toInt())
                        if (userObj.isEmpty()) {
                            val userInsertionResult = async {
                                dbInstance.userDao().addUser(
                                    User(
                                        userId = messageObject.userId!!.toInt(),
                                        deviceName = messageObject.deviceAddress
                                    )
                                )
                            }
                            if (userInsertionResult.await() >= 0) {
                                dbInstance.chatDao().addChatMessage(
                                    Chat(
                                        chatId = messageObject.id.toInt(),
                                        userIdRef = messageObject.userId!!.toInt(),
                                        baseImage = messageObject.baseImage,
                                        message = messageObject.message,
                                        timestamp = messageObject.id
                                    )
                                )
                            } else {
                                Log.d(TAG, "Error inserting user, can not get insertion id")
                            }
                        } else {
                            dbInstance.chatDao().addChatMessage(
                                Chat(
                                    chatId = messageObject.id.toInt(),
                                    userIdRef = messageObject.userId!!.toInt(),
                                    baseImage = messageObject.baseImage,
                                    message = messageObject.message,
                                    timestamp = messageObject.id
                                )
                            )
                        }
                    }
                }
            } catch (ex: Exception) {

            }
            delay(500)
        }
    }
}