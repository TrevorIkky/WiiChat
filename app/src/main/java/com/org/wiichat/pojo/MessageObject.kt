package com.org.wiichat.pojo

import java.io.Serializable

class MessageObject(
    var id: Long,
    var message: String,
    var baseImage: String? = null,
    var timestamp: Long = System.currentTimeMillis()
) : Serializable