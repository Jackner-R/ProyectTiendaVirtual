package com.example.proyectov2

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductoAdapter(
    private val context: Context,
    private val productos: List<Producto>
) : RecyclerView.Adapter<ProductoAdapter.ViewHolder>(){



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombre)
        val precio: TextView = view.findViewById(R.id.tvPrecio)
        val imagen: ImageView = view.findViewById(R.id.ivProducto)
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]

        holder.nombre.text = producto.nombre
        holder.precio.text = "S/. %.2f".format(producto.precio)
        holder.imagen.setImageBitmap(
            BitmapFactory.decodeByteArray(producto.imagen, 0, producto.imagen.size)
        )

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalleProductoActivity::class.java)
            intent.putExtra("producto", producto)
            context.startActivity(intent)
        }
    }



    override fun getItemCount() = productos.size
}
