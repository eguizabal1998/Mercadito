package com.basicdeb.mercadito.data

import android.util.Log
import com.basicdeb.mercadito.objects.User
import com.basicdeb.mercadito.vo.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FireAuthRepoImpl: FireAuthRepo {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firebaseFireStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun googleRegister(): Resource<String> {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()

        return Resource.Success("hola soy Google")
    }

    override fun facebookRegister(): Resource<String> {

        return Resource.Success("hola soy Facebook")
    }

    override suspend fun register(email: String, password: String): Resource<Boolean> {
        val user = firebaseAuth.createUserWithEmailAndPassword(email,password).await()

//        val data = hashMapOf(
//            "fase" to 0
//        )
//
//        firebaseFireStore.collection("Uids").document(user.user?.uid.toString()).collection("info").document("estados").set(data).await()

        user.user?.sendEmailVerification()?.await()
        firebaseAuth.signOut()

        return Resource.Success(true)
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        Log.i("login","fireLogin")
        var data = User("")
        val user = firebaseAuth.signInWithEmailAndPassword(email,password).await()
        val userId = user.user?.uid.toString()
        data.uid = userId

        return if (user.user?.isEmailVerified == true){
            Resource.Success(data)
        }else{
            firebaseAuth.signOut()
            Resource.Failure(Exception("Verifica tu correo para activar la cuenta"))
        }


    }

    override suspend fun recovery(email: String): Resource<Boolean> {
        val state = firebaseAuth.sendPasswordResetEmail(email).await()

        return Resource.Success(true)
    }

    override fun currentUser(): Boolean {
        val Uid = firebaseAuth.currentUser?.uid

        return !Uid.isNullOrEmpty()
    }

    override fun closeSesion(): Resource<String> {
        firebaseAuth.signOut()

        return Resource.Success("Completado")
    }

}