package com.org.wiichat.pojo

data class MessageObject(
    var id: Long,
    var message: String,
    var baseImage: String?,
    var timestamp: Long = System.currentTimeMillis()
)