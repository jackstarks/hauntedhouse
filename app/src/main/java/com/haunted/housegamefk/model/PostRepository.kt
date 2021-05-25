package com.haunted.housegamefk.model

import java.util.*

class PostRepository {
    val mainData: MutableList<Int>
        get() {
            val users: MutableList<Int> = ArrayList()

            return users
        }

    companion object {
        private val TAG = PostRepository::class.java.simpleName
    }
}