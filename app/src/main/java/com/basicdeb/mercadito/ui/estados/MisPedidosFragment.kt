package com.basicdeb.mercadito.ui.estados

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.basicdeb.mercadito.R
import com.basicdeb.mercadito.data.FiredbRepoImpl
import com.basicdeb.mercadito.databinding.MisPedidosFragmentBinding
import com.basicdeb.mercadito.domain.FiredbUseCaseImpl
import com.basicdeb.mercadito.objects.Ordenes
import com.basicdeb.mercadito.ui.menu.ViewPagerAdapter
import com.basicdeb.mercadito.viewmodel.estados.MisPedidosViewModel
import com.basicdeb.mercadito.viewmodel.perfil.DBFactory
import com.basicdeb.mercadito.vo.Resource
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar


class MisPedidosFragment : Fragment(),ordenesAdapter.onItemClickListener {

    private lateinit var mAdView:AdView

    private lateinit var viewModel: MisPedidosViewModel
    private lateinit var binding: MisPedidosFragmentBinding
    private lateinit var adapter: ordenesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = DBFactory(FiredbUseCaseImpl(FiredbRepoImpl()))
        binding = DataBindingUtil.inflate(inflater,R.layout.mis_pedidos_fragment,container,false)
        viewModel = ViewModelProvider(this,factory).get(MisPedidosViewModel::class.java)

        binding.viewModel = viewModel

        MobileAds.initialize(this.context, "ca-app-pub-7939671701124128~4654516290")

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        observers()

        return binding.root
    }

    private fun observers() {
        viewModel.Ordenes.observe(this.viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading -> {
                    Snackbar.make(this.view!!,"Cargando",Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    viewModel.listaOrdenes.clear()
                    viewModel.listaOrdenes.addAll(it.data)
                    adapter.setListData(viewModel.listaOrdenes)
                    adapter.notifyDataSetChanged()
                    //binding.textView4.text = it.data.toString()
                    //Snackbar.make(this.view!!,it.data.toString(),Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Failure -> {
                    Snackbar.make(this.view!!, it.exception.message!!,Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = ordenesAdapter( this.context!!, this,this)

        val RecyclerView = binding.rvMisPedidos

        RecyclerView.layoutManager = LinearLayoutManager(this.context)
        RecyclerView.adapter = adapter


    }

    override fun onItemClick(item: Ordenes, position: Int) {

    }
}
