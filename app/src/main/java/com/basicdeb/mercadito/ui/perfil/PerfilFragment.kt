package com.basicdeb.mercadito.ui.perfil

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.basicdeb.mercadito.GlideImageLoader

import com.basicdeb.mercadito.R
import com.basicdeb.mercadito.data.FiredbRepoImpl
import com.basicdeb.mercadito.databinding.PerfilFragmentBinding
import com.basicdeb.mercadito.domain.FiredbUseCaseImpl
import com.basicdeb.mercadito.viewmodel.perfil.DBFactory
import com.basicdeb.mercadito.viewmodel.perfil.PerfilViewModel
import com.basicdeb.mercadito.vo.Resource
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.manager.SupportRequestManagerFragment
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.PicassoProvider
import kotlinx.android.synthetic.main.perfil_fragment.view.*
import lv.chi.photopicker.ChiliPhotoPicker
import lv.chi.photopicker.PhotoPickerFragment
import java.io.ByteArrayOutputStream

class PerfilFragment : Fragment(), PhotoPickerFragment.Callback  {

    private lateinit var viewModel: PerfilViewModel
    private lateinit var binding: PerfilFragmentBinding
    private lateinit var factory: DBFactory

    private val PICK_IMAGE = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        factory = DBFactory(FiredbUseCaseImpl(FiredbRepoImpl()))
        binding = DataBindingUtil.inflate(inflater,R.layout.perfil_fragment,container,false)
        viewModel = ViewModelProvider(this,factory).get(PerfilViewModel::class.java)

        binding.viewModel = viewModel

        observers()

        listeners()



        return binding.root
    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        Log.i("imagen","listener")
        Log.i("imagen",photos.toString())
    }

    private fun listeners() {
        binding.btnPerfilCargar.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(activity!!.baseContext, Manifest.permission.READ_EXTERNAL_STORAGE) ==
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

//            val galeryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//            startActivityForResult(galeryIntent,PICK_IMAGE)
        }

        binding.btnPerfilGuardar.setOnClickListener {
            val bitmap = (binding.imageViewPerfil.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
            val data = baos.toByteArray()

            viewModel.image = data
            viewModel.departamento = binding.spinnerDepartamento.selectedItem.toString()

            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return@setOnClickListener
            with (sharedPref.edit()) {
                putString("departamento", binding.spinnerDepartamento.selectedItem.toString())
                commit()
            }
            viewModel.checkNombre()
        }


    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
//        PhotoPickerFragment.newInstance(
//            allowCamera = true,
//            theme = R.style.ChiliPhotoPicker_Dark
//        ).show(fragmentManager!!,"picker")
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    //handle requested permission result
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("imagen",data?.data.toString())
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            val imageUri = data?.data
            val myOptions = RequestOptions().override(1920,700)

            Glide.with(this).load(imageUri)
                .apply(myOptions)
                .into(binding.imageViewPerfil)

            binding.imageViewPerfil.scaleType =ImageView.ScaleType.FIT_XY
            //binding.imageViewPerfil.setImageURI(imageUri)
        }
    }

    private fun observers() {
        viewModel.eventoGuardar.observe(this.viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading -> {
                    binding.progressBarPerfil.visibility = View.VISIBLE
                    binding.btnPerfilGuardar.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBarPerfil.visibility = View.GONE
                    binding.btnPerfilGuardar.isEnabled = true
                    Snackbar.make(this.view!!, "Guardado", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Failure -> {
                    binding.progressBarPerfil.visibility = View.GONE
                    binding.btnPerfilGuardar.isEnabled = true
                    Snackbar.make(this.view!!, it.exception.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        })

        viewModel.eventoGetPerfil.observe(this.viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading -> {
                    binding.progressBarPerfil.visibility = View.VISIBLE
                    binding.btnPerfilGuardar.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBarPerfil.visibility = View.GONE
                    binding.btnPerfilGuardar.isEnabled = true
                    viewModel.nombre.set(it.data?.nombre)
                    viewModel.descripcion.set(it.data?.descripcion)
                    viewModel.numero.set(it.data?.numero.toString())
                    viewModel.facebook.set(it.data?.facebook)
                    binding.tvPerfilDepartamento.text = it.data?.departamento
                }
                is Resource.Failure -> {
                    binding.progressBarPerfil.visibility = View.GONE
                    binding.btnPerfilGuardar.isEnabled = true
                    Snackbar.make(this.view!!, it.exception.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        })

        viewModel.eventoPortada.observe(this.viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading -> {
                }
                is Resource.Success -> {

                    val myOptions = RequestOptions().override(1920,700)

                    Glide.with(this).load(it.data)
                        .apply(myOptions)
                        .into(binding.imageViewPerfil)

                    binding.imageViewPerfil.scaleType =ImageView.ScaleType.FIT_XY

//                    Glide.with(this).load(it.data)
//                        .fitCenter()
//                        .apply(RequestOptions.bitmapTransform(RoundedCorners(60)))
//                        .into(binding.imageViewPerfil)
                    //binding.imageViewPerfil.setImageURI(it.data.toUri())
                }
                is Resource.Failure -> {
                    Snackbar.make(this.view!!, it.exception.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        viewModel.departamento = sharedPref.getString("departamento","Santa Ana").toString()
        viewModel.getPerfil()
    }



}
