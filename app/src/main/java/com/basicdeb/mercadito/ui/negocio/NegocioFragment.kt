package com.basicdeb.mercadito.ui.negocio

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.basicdeb.mercadito.R
import com.basicdeb.mercadito.data.FiredbRepoImpl
import com.basicdeb.mercadito.databinding.NegocioFragmentBinding
import com.basicdeb.mercadito.domain.FiredbUseCaseImpl
import com.basicdeb.mercadito.objects.Producto
import com.basicdeb.mercadito.viewmodel.negocio.NegocioViewModel
import com.basicdeb.mercadito.viewmodel.negocio.ProductosAdapter
import com.basicdeb.mercadito.viewmodel.perfil.DBFactory
import com.basicdeb.mercadito.vo.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.producto.view.*
import kotlin.math.absoluteValue

class NegocioFragment : Fragment(),ProductosAdapter.onItemClickListener {

    private lateinit var viewModel: NegocioViewModel
    private lateinit var binding: NegocioFragmentBinding
    private lateinit var factory: DBFactory
    private lateinit var adapter: ProductosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        factory = DBFactory(FiredbUseCaseImpl(FiredbRepoImpl()))
        binding = DataBindingUtil.inflate(inflater,R.layout.negocio_fragment,container,false)
        viewModel = ViewModelProvider(this,factory).get(NegocioViewModel::class.java)

        binding.viewModel = viewModel

        listeners()

        observers()

        getProductos()

        return binding.root
    }

    fun getProductos(){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = sharedPref.getString("departamento","vacio")
        viewModel.departamento = defaultValue.toString()

        viewModel.getProductos()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = ProductosAdapter( this.context!!, this,this)

        val RecyclerView = binding.rvNegocioLista

        RecyclerView.layoutManager = LinearLayoutManager(this.context)
        RecyclerView.adapter = adapter

        viewModel.getProductos()
    }

    private fun listeners() {
        binding.btnNegocioAgregar.setOnClickListener {
            this.findNavController().navigate(NegocioFragmentDirections.actionNegocioFragmentToAgregarProFragment())
        }
    }

    private fun observers(){
        viewModel.eventoObtenerLista.observe(this.viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading -> {
                    Snackbar.make(this.view!!, "Cargando", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Success -> {
                    viewModel.lista.clear()
                    viewModel.lista.addAll(it.data)
                    adapter.setListData(viewModel.lista)
                    adapter.notifyDataSetChanged()
                }
                is Resource.Failure -> {
                    Snackbar.make(this.view!!, it.exception.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        })

        viewModel.eventoBorrar.observe(this.viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading -> {
                    Snackbar.make(this.view!!, "Cargando", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Success -> {
                    viewModel.getProductos()
                }
                is Resource.Failure -> {
                    Snackbar.make(this.view!!, it.exception.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onItemClick(item: Producto, position: Int, view: View) {

    }

    override fun onEditClick(item: Producto, position: Int) {
        this.findNavController().navigate(NegocioFragmentDirections.actionNegocioFragmentToModificarFragment(item.nombre,
            item.precio.toString(),item.descripcion,item.unidad,item.NegocioId,item.imagen,item.disponible,item.id))
    }

    override fun onDeleteClick(item: Producto, position: Int) {
        viewModel.deleteProducto(item.id)
    }

    override fun onChecked(item: Producto, ischecked:Boolean) {
        viewModel.changeDispo(item.id,ischecked)
    }
}
