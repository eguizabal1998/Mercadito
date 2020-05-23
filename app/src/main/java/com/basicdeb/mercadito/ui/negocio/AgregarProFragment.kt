package com.basicdeb.mercadito.ui.negocio

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.basicdeb.mercadito.R
import com.basicdeb.mercadito.data.FiredbRepoImpl
import com.basicdeb.mercadito.databinding.AgregarFragmentBinding
import com.basicdeb.mercadito.domain.FiredbUseCaseImpl
import com.basicdeb.mercadito.ui.perfil.PerfilFragment
import com.basicdeb.mercadito.viewmodel.negocio.AgregarViewModel
import com.basicdeb.mercadito.viewmodel.perfil.DBFactory
import com.basicdeb.mercadito.vo.Resource
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class AgregarProFragment : Fragment() {

    private lateinit var viewModel: AgregarViewModel
    private lateinit var binding: AgregarFragmentBinding
    private lateinit var factory: DBFactory

    private val PICK_IMAGE = 1000

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        factory = DBFactory(FiredbUseCaseImpl(FiredbRepoImpl()))
        binding = DataBindingUtil.inflate(inflater,R.layout.agregar_fragment,container,false)
        viewModel = ViewModelProvider(this,factory).get(AgregarViewModel::class.java)

        binding.viewModel = viewModel

        observers()

        binding.btnAgregarCargar.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(
                        activity!!.baseContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    pickImageFromGallery()
                }
            }
            else{
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        }

        binding.btnAgregarGuardar.setOnClickListener {
            val bitmap = (binding.imgAgregarProd.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
            val data = baos.toByteArray()

            viewModel.image = data

            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return@setOnClickListener
            val defaultValue = sharedPref.getString("departamento","vacio")

            viewModel.departamento = defaultValue.toString()

            viewModel.checkCampos()
        }

        return binding.root
    }

    private fun observers() {
        viewModel.eventoGuardar.observe(this.viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading -> {
                    binding.cirLoginButtonAgregar.visibility = View.VISIBLE
                    binding.btnAgregarGuardar.isEnabled = false
                }
                is Resource.Success -> {
                    binding.cirLoginButtonAgregar.visibility = View.GONE
                    binding.btnAgregarGuardar.isEnabled = true
                    Snackbar.make(this.view!!, "Guardado", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Failure -> {
                    binding.cirLoginButtonAgregar.visibility = View.GONE
                    binding.btnAgregarGuardar.isEnabled = true
                    Snackbar.make(this.view!!, it.exception.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("imagen",data?.data.toString())
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            val imageUri = data?.data
            val myOptions = RequestOptions().override(400,400)
            Glide.with(this).load(imageUri)
                .apply(myOptions)
                .into(binding.imgAgregarProd)

            binding.imgAgregarProd.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this.context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
