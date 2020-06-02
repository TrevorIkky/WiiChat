package com.org.wiichat.core.receiver

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import com.org.wiichat.core.services.WiiMessageTransferService
import es.dmoral.toasty.Toasty

class WiiMessageResultReceiver(handler: Handler, context: Context) :
    ResultReceiver(handler) {
    private val ctx = context
    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        when (resultCode) {
            WiiMessageTransferService.MESSAGE_SUCCESS_CODE -> {
                Toasty.error(ctx, "File sent!", Toasty.LENGTH_LONG).show()
            }
            WiiMessageTransferService.MESSAGE_ERROR_CODE -> {
                Toasty.error(ctx, "Error sending file", Toasty.LENGTH_LONG).show()
            }
        }
        super.onReceiveResult(resultCode, resultData)
    }
}