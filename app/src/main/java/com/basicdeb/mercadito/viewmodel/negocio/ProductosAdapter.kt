package com.basicdeb.mercadito.viewmodel.negocio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.basicdeb.mercadito.R
import com.basicdeb.mercadito.objects.Producto
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.producto.view.*

class ProductosAdapter(private val context: Context, var clickListener: onItemClickListener, var fragment: Fragment): RecyclerView.Adapter<ProductosAdapter.MainViewHolder>(){

    private var dataList = mutableListOf<Producto>()


    fun setListData(data:MutableList<Producto>){
        dataList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(context).inflate( R.layout.producto,parent,false)

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
        val product = dataList[position]
        holder.bindView(product, clickListener)
    }

    inner class MainViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bindView(producto: Producto, action: onItemClickListener){
            itemView.textView_productorow_nombre.text = producto.nombre
            itemView.textView_productorow_precio.text = producto.precio.toString()
            itemView.textView_productorow_descripcion.text = producto.descripcion
            itemView.textView_productorow_unidad.text = producto.unidad
            itemView.Switch_productorow_disponible.isChecked = producto.disponible

            val myOptions = RequestOptions().override(400,400)

            Glide.with(fragment).load(producto.imagen).apply(myOptions).into(itemView.imageView_producto)

            itemView.imageView_producto.scaleType = ImageView.ScaleType.FIT_XY

            itemView.constrain_productorow_info.setOnClickListener {
                action.onItemClick(producto, adapterPosition, it)
            }
            itemView.btn_productorow_editar.setOnClickListener {
                action.onEditClick(producto,adapterPosition)
            }
            itemView.btn_productorow_eliminar.setOnClickListener {
                action.onDeleteClick(producto,adapterPosition)
            }

            itemView.Switch_productorow_disponible.setOnCheckedChangeListener { buttonView, isChecked ->
                action.onChecked(producto,isChecked)
            }
        }

    }

    interface onItemClickListener{
        fun onItemClick(item: Producto, position: Int, view: View)
        fun onEditClick(item: Producto, position: Int)
        fun onDeleteClick(item: Producto, position: Int)
        fun onChecked(item: Producto,ischecked:Boolean)
    }

}