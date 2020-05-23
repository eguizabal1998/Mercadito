package com.basicdeb.mercadito.data

import com.basicdeb.mercadito.objects.User
import com.basicdeb.mercadito.vo.Resource

interface FireAuthRepo {

    suspend fun register(email: String, password: String): Resource<Boolean>

    suspend fun login(email: String, password: String): Resource<User>

    suspend fun recovery(email: String): Resource<Boolean>

    fun currentUser(): Boolean

    fun closeSesion(): Resource<String>

    fun googleRegister(): Resource<String>

    fun facebookRegister(): Resource<String>

}