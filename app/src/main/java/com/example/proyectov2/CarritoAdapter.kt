package com.example.proyectov2

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class CarritoAdapter(
    private val items: List<ItemCarrito>,
    private val onEliminar: (ItemCarrito) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.ivCarritoImagen)
        val nombre: TextView = view.findViewById(R.id.tvCarritoNombre)
        val precio: TextView = view.findViewById(R.id.tvCarritoPrecio)
        val cantidad: TextView = view.findViewById(R.id.tvCarritoCantidad)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_carrito, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nombre.text = item.nombre
        holder.precio.text = "S/. %.2f".format(item.precio)
        holder.cantidad.text = "Cantidad: ${item.cantidad}"
        holder.imagen.setImageBitmap(BitmapFactory.decodeByteArray(item.imagen, 0, item.imagen.size))

        holder.btnEliminar.setOnClickListener {
            onEliminar(item)
        }
    }

    override fun getItemCount() = items.size
}
