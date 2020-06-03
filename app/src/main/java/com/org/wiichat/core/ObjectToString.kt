package com.org.wiichat.core

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.org.wiichat.pojo.MessageObject

class ObjectToString {
    companion object {
        private val gson = Gson()
        fun getJsonFromObject(o: Any): String {
            return gson.toJson(o)
        }

        fun getMessageObject(json: String): Any {
            val type = object :
                TypeToken<ArrayList<MessageObject?>?>() {}.type
            return gson.fromJson<MessageObject>(json, type)
        }
    }
}