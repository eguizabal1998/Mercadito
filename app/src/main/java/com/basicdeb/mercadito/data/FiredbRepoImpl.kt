package com.basicdeb.mercadito.data

import android.util.Log
import com.basicdeb.mercadito.objects.*
import com.basicdeb.mercadito.vo.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File

class FiredbRepoImpl: FiredbRepo {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firebaseFireStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val firebaseStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance("gs://mercadito-51eb6.appspot.com")
    }

    private val firebaseStorage2: FirebaseStorage by lazy {
        FirebaseStorage.getInstance("gs://mercadito-prod")
    }

    private val firedatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private val Uid = firebaseAuth.uid.toString()

    override suspend fun guardar(Perfil: Perfil,imagen:ByteArray,departamento:String): Resource<Boolean> {

        val ImagesRef = firebaseStorage.reference.child("$Uid/portada.jpg")

        val data = ImagesRef.putBytes(imagen).await()
        Log.i("imagen",data.storage.path)

        val negocio = hashMapOf(
            "nombre" to Perfil.nombre,
            "descripcion" to Perfil.descripcion,
            "numero" to Perfil.numero,
            "facebook" to Perfil.facebook,
            "portada" to "$Uid/portada_1920x700.jpg",
            "departamento" to listOf(departamento)
        )

        //firebaseFireStore.collection("Negocios-$departamento").document(Uid).collection("data").document("perfil").set(negocio).await()
        firebaseFireStore.collection("Negocios").document(Uid).set(negocio).await()

        return Resource.Success(true)
    }

    override suspend fun getPerfil(departamento:String): Resource<Perfil?> {

        return try {
            val cache = firebaseFireStore.collection("Negocios").document(Uid).get(Source.CACHE).await()
            val perfil = Perfil(cache.data?.get("nombre").toString(),cache.data?.get("descripcion").toString(),
                cache.data?.get("numero").toString().toInt(),cache.data?.get("facebook").toString(),cache.data?.get("departamento").toString())
            Log.i("perfil","base en cache")
            Resource.Success(perfil)
        }catch (e:Exception){
            val data = firebaseFireStore.collection("Negocios").document(Uid).get().await()
            val perfil = Perfil(data.data?.get("nombre").toString(),data.data?.get("descripcion").toString(),
                data.data?.get("numero").toString().toInt(),data.data?.get("facebook").toString(),data.data?.get("departamento").toString())

            Log.i("perfil","base en linea")
            Resource.Success(perfil)
        }

    }

    override suspend fun getPortada(): Resource<String> {
        val ImagesRef = firebaseStorage.reference.child("$Uid/portada_1920x700.jpg")
        val localFile = File.createTempFile("portada_1920x700", "jpg")

        val imagen = ImagesRef.getFile(localFile).await()

        Log.i("imagen",localFile.path)

        return Resource.Success(localFile.path)
    }

    override suspend fun guardarConfig(horarios: Horarios,departamento: String): Resource<Boolean> {
        firebaseFireStore.collection("Negocios-$departamento").document(Uid).collection("data").document("horarios").set(horarios).await()
        return Resource.Success(true)
    }

    override suspend fun getConfig(departamento: String): Resource<Horarios> {

        try {
            val data = firebaseFireStore.collection("Negocios-$departamento").document(Uid).collection("data").document("horarios").get(Source.CACHE).await()

            val horarios = Horarios(data.getBoolean("lunes")!!,data.getBoolean("martes")!!,data.getBoolean("miercoles")!!,data.getBoolean("jueves")!!,
                data.getBoolean("viernes")!!,data.getBoolean("sabado")!!,data.getBoolean("domingo")!!,
                data.get("horaA").toString().toInt(),data.get("minutoA").toString().toInt(),data.get("horaC").toString().toInt(),data.get("minutoC").toString().toInt())

            Log.i("horarios","base en cache")

            return Resource.Success(horarios)

        }catch (e:Exception){
            val data = firebaseFireStore.collection("Negocios-$departamento").document(Uid).collection("data").document("horarios").get().await()

            val horarios = Horarios(data.getBoolean("lunes")!!,data.getBoolean("martes")!!,data.getBoolean("miercoles")!!,data.getBoolean("jueves")!!,
                data.getBoolean("viernes")!!,data.getBoolean("sabado")!!,data.getBoolean("domingo")!!,
                data.get("horaA").toString().toInt(),data.get("minutoA").toString().toInt(),data.get("horaC").toString().toInt(),data.get("minutoC").toString().toInt())

            Log.i("horarios","base en linea")

            return Resource.Success(horarios)
        }

    }

    override suspend fun saveProductos(producto: Producto,imagen:ByteArray,departamento: String): Resource<Producto> {

        val producto1 = hashMapOf(
            "nombre" to producto.nombre,
            "descripcion" to producto.descripcion,
            "precio" to producto.precio,
            "unidad" to producto.unidad,
            "negocioId" to Uid,
            "imagen" to producto.imagen,
            "disponible" to producto.disponible
        )

        val data = firebaseFireStore.collection("Productos").add(producto1).await()

        val id = data.id
        val ImagesRef = firebaseStorage2.reference.child("$id.jpg")

        val dataimg = ImagesRef.putBytes(imagen).await()
        Log.i("imagen",dataimg.storage.path)

        return Resource.Success(producto)
    }

    override suspend fun getProductos(departamento:String): Resource<MutableList<Producto>> {
        val data = firebaseFireStore.collection("Productos").whereEqualTo("negocioId",Uid).get().await()

        val lista = mutableListOf<Producto>()
        for (documents in data){
            val Uidn = documents.id
            val ImagesRef = firebaseStorage2.reference.child(Uidn + "_400x400.jpg")
            val localFile = File.createTempFile(Uidn + "_400x400-$Uidn", "jpg")

            val imagen = ImagesRef.getFile(localFile).await()

            lista.add(Producto(
                documents.data.get("nombre").toString(),
                documents.data.get("descripcion").toString(),
                documents.data.get("precio").toString().toFloat(),
                documents.data.get("unidad").toString(),
                documents.data.get("negocioId").toString(),
                localFile.path,
                documents.data.get("disponible").toString().toBoolean(),
                documents.id
            ))
        }

        return Resource.Success(lista)
    }



    override suspend fun deleteProducto(id:String,departamento:String): Resource<String> {
        val ImagesRef = firebaseStorage2.reference.child(id + "_400x400.jpg")
        ImagesRef.delete()
        firebaseFireStore.collection("Productos").document(id).delete().await()

        return Resource.Success(id)
    }

    override suspend fun modProducto(producto: Producto,imagen:ByteArray,departamento:String): Resource<Producto> {
        val id = producto.id
        val ImagesRef = firebaseStorage2.reference.child("$id.jpg")
        ImagesRef.putBytes(imagen).await()
        firebaseFireStore.collection("Productos").document(id).set(producto).await()

        return Resource.Success(producto)
    }


    override suspend fun changeDispo(id: String,departamento: String,change:Boolean): Resource<Boolean> {
        firebaseFireStore.
        collection("Negocios-$departamento")
            .document(Uid).collection("productos")
            .document(id).
            update("disponible",change).await()

        return Resource.Success(true)
    }

    override fun saveToken(token: String,departamento:String) {
        firebaseFireStore.collection("ListaNegocios-$departamento").document(Uid).update("token",token)
    }

    @ExperimentalCoroutinesApi
    override suspend fun getOrdenes(): Flow<Resource<MutableList<Ordenes>>> = callbackFlow {

        val eventDocumented = firedatabase.getReference("Ordenes/$Uid")

        val eventListener = eventDocumented.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
                this@callbackFlow.close(databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.let { result ->
                    val data = mutableListOf<Ordenes>()
                    result.children.forEach { result2 ->
                        val dataProd = mutableListOf<ordenProd>()
                        result2.child("productos").children.forEach {
                            dataProd.add(it.getValue(ordenProd::class.java)!!)
                        }
                        data.add(Ordenes(result2.child("info").getValue(ordenInfo::class.java)!!, dataProd))
                    }
                    this@callbackFlow.offer(Resource.Success(data))
                    //this@callbackFlow.sendBlocking(Resource.Success(data))
                }
            }
        })

        awaitClose { eventDocumented.removeEventListener(eventListener) }

    }
}

//dataSnapshot.let {result ->
//    val data = mutableListOf<Ordenes>()
//    result.children.forEach {result2 ->
//        val dataProd = mutableListOf<ordenProd>()
//        result2.child("productos").children.forEach {
//            dataProd.add(it.getValue(ordenProd::class.java)!!)
//        }
//        data.add(Ordenes(result2.child("info").getValue(ordenInfo::class.java)!!,dataProd))
//    }
//    this@callbackFlow.offer(Resource.Success(data))
//    //this@callbackFlow.sendBlocking(Resource.Success(data))
//}

//val data = firedatabase.child("Ordenes").child(Uid).addValueEventListener(object : ValueEventListener{
//    override fun onCancelled(p0: DatabaseError) {
//        // Failed to read value
//        Log.w("ordenes", "Failed to read value.", p0.toException())
//    }
//
//    override fun onDataChange(p0: DataSnapshot) {
//        val value = p0.getValue(String::class.java)
//        Log.d("ordenes", "Value is: $value")
//    }
//
//})
//
//return Resource.Success(true)