package com.org.wiichat.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.org.wiichat.core.MessageTaskHandler
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {

    private val TAG = "MessageViewModel"

    fun createClient(hostAddress: String, messageTaskHandler: MessageTaskHandler) {
        viewModelScope.launch {
            messageTaskHandler.createClient(hostAddress)
        }
    }

    fun createServer(messageTaskHandler: MessageTaskHandler) {
        viewModelScope.launch {
            messageTaskHandler.createServer()
        }
    }



    fun sendMessage(o: Any, messageTaskHandler: MessageTaskHandler) {
        viewModelScope.launch {
            messageTaskHandler.sendMessage(o)
        }
    }

    override fun onCleared() {
        Log.d(TAG, "ViewModel cleared.")
        super.onCleared()
    }
}