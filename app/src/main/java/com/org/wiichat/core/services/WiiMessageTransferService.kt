package com.org.wiichat.core.services

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.org.wiichat.core.MessageTaskHandler
import com.org.wiichat.core.ObjectToString
import com.org.wiichat.core.receiver.WiiMessageResultReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class WiiMessageTransferService : IntentService(WiiMessageTransferService::class.simpleName) {
    companion object {
        val SEND_FILE_ACTION = "com.org.wiichat.SEND_FILE"
        var TO_ADDRESS: String = "com.org.wiichat.ADDRESS"
        var PORT: String = "com.org.wiichat.PORT"
        val MESSAGE_SUCCESS_CODE = 1
        val MESSAGE_ERROR_CODE = 0
    }

    private val TAG = "WiiFileTransferService"

    override fun onHandleIntent(intent: Intent?) {
        if (intent!!.action == SEND_FILE_ACTION) {

            Log.d(TAG, "Received Intent.")

            val messageObject =
                ObjectToString.getMessageObject(
                    intent.getStringExtra("jsonObject")!!
                )
            val resultReceiverService =
                intent.getParcelableExtra("wiiResultService") as WiiMessageResultReceiver
            val address = intent.getStringExtra(TO_ADDRESS)
            val port = intent.getIntExtra(PORT, -1)

            val context = applicationContext
            val messageTaskHandler =
                MessageTaskHandler(context)
            CoroutineScope(Dispatchers.IO).launch {
                val deferredResult =
                    async { messageTaskHandler.sendMessage(messageObject, address!!, port) }
                if (deferredResult.await() != null)
                    resultReceiverService.send(MESSAGE_SUCCESS_CODE, Bundle())
                else
                    resultReceiverService.send(MESSAGE_ERROR_CODE, Bundle())
            }
        }
    }
}