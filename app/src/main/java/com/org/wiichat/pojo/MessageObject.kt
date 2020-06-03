package com.org.wiichat.pojo

import java.io.Serializable

class MessageObject(
    var id: Long,
    var message: String,
    var deviceAddress : String? = null,
    var baseImage: String? = null,
    var userId: Long? = null
) : Serializable