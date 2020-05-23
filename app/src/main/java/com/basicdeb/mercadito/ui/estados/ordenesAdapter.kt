package com.basicdeb.mercadito.ui.estados

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.basicdeb.mercadito.R
import com.basicdeb.mercadito.objects.Ordenes
import kotlinx.android.synthetic.main.orden_row.view.*

class ordenesAdapter(private val context: Context, var clickListener: onItemClickListener, var fragment: Fragment): RecyclerView.Adapter<ordenesAdapter.MainViewHolder>(){

    private var dataList = mutableListOf<Ordenes>()



    fun setListData(data:MutableList<Ordenes>){
        dataList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(context).inflate( R.layout.orden_row,parent,false)

        return MainViewHolder(view)
    }


    override fun getItemCount(): Int {
        return if(dataList.size > 0){
            dataList.size
        }else{
            0
        }
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val ordenes = dataList[position]
        holder.bindView(ordenes, clickListener)
    }

    inner class MainViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bindView(ordenes: Ordenes, action: onItemClickListener){
            itemView.orden_row_direccion1.text = ordenes.ordenInfo.direccion1.toString()
            itemView.orden_row_total.text = ordenes.ordenInfo.total.toString()

            itemView.setOnClickListener {
                action.onItemClick(ordenes,adapterPosition)
            }
        }


    }

    interface onItemClickListener{
        fun onItemClick(item: Ordenes, position: Int)
    }

}