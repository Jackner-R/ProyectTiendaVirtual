package com.example.proyectov2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class CarritoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var dbHelper: DBHelper
    private lateinit var carrito: MutableList<ItemCarrito>
    private lateinit var adapter: CarritoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        recyclerView = findViewById(R.id.recyclerViewCarrito)
        tvTotal = findViewById(R.id.tvTotal)
        dbHelper = DBHelper(this)

        carrito = dbHelper.obtenerCarrito().toMutableList()

        adapter = CarritoAdapter(carrito) { item ->
            dbHelper.eliminarDelCarrito(item.id)
            carrito.remove(item)
            adapter.notifyDataSetChanged()
            actualizarTotal()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        actualizarTotal()

        val btnIrAPago = findViewById<Button>(R.id.btnIrAPago)
        btnIrAPago.setOnClickListener {
            val total = carrito.sumOf { it.precio * it.cantidad }
            val intent = Intent(this, PagoActivity::class.java)
            intent.putExtra("total_pago", total)
            startActivity(intent)
        }


    }

    private fun actualizarTotal() {
        val total = carrito.sumOf { it.precio * it.cantidad }
        tvTotal.text = "Total: S/. %.2f".format(total)
    }
}
