package com.example.proyectov2

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class PagoActivity : AppCompatActivity() {

    private lateinit var tvMontoTotal: TextView
    private lateinit var btnConfirmarPago: Button
    private lateinit var rgTipoPago: RadioGroup
    private lateinit var layoutTarjeta: LinearLayout
    private lateinit var layoutYapePlin: LinearLayout

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago)

        crearCanalNotificacion()

        tvMontoTotal = findViewById(R.id.tvMontoTotal)
        btnConfirmarPago = findViewById(R.id.btnConfirmarPago)
        rgTipoPago = findViewById(R.id.rgTipoPago)
        layoutTarjeta = findViewById(R.id.layoutTarjeta)
        layoutYapePlin = findViewById(R.id.layoutYapePlin)

        dbHelper = DBHelper(this)

        val total = intent.getDoubleExtra("total_pago", 0.0)
        tvMontoTotal.text = "Total a pagar: S/. %.2f".format(total)

        rgTipoPago.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbTarjeta -> {
                    layoutTarjeta.visibility = LinearLayout.VISIBLE
                    layoutYapePlin.visibility = LinearLayout.GONE
                }
                R.id.rbYapePlin -> {
                    layoutTarjeta.visibility = LinearLayout.GONE
                    layoutYapePlin.visibility = LinearLayout.VISIBLE
                }
                else -> {
                    layoutTarjeta.visibility = LinearLayout.GONE
                    layoutYapePlin.visibility = LinearLayout.GONE
                }
            }
        }

        btnConfirmarPago.setOnClickListener {
            if (validarCamposPago()) {
                procesarCompra()
            }
        }
    }

    private fun crearCanalNotificacion() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val nombre = "CanalStock"
            val descripcion = "Notificaciones de stock insuficiente"
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = android.app.NotificationChannel("canal_stock_id", nombre, importancia)
            canal.description = descripcion

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(canal)
        }
    }

    private fun validarCamposPago(): Boolean {
        val checkedId = rgTipoPago.checkedRadioButtonId
        if (checkedId == -1) {
            Toast.makeText(this, "Seleccione un método de pago", Toast.LENGTH_SHORT).show()
            return false
        }
        if (checkedId == R.id.rbTarjeta) {
            val numero = findViewById<EditText>(R.id.etNumeroTarjeta).text.toString().trim()
            val cvv = findViewById<EditText>(R.id.etCVV).text.toString().trim()
            val venc = findViewById<EditText>(R.id.etVencimiento).text.toString().trim()
            if (numero.length != 16) {
                Toast.makeText(this, "Ingrese un número de tarjeta válido", Toast.LENGTH_SHORT).show()
                return false
            }
            if (cvv.length != 3) {
                Toast.makeText(this, "Ingrese un CVV válido", Toast.LENGTH_SHORT).show()
                return false
            }
            if (venc.length != 5) {
                Toast.makeText(this, "Ingrese la fecha de vencimiento válida (MM/AA)", Toast.LENGTH_SHORT).show()
                return false
            }
        } else if (checkedId == R.id.rbYapePlin) {
            val celular = findViewById<EditText>(R.id.etNumeroCelular).text.toString().trim()
            val codigo = findViewById<EditText>(R.id.etCodigoAprobacion).text.toString().trim()
            if (celular.length != 9) {
                Toast.makeText(this, "Ingrese un número de celular válido", Toast.LENGTH_SHORT).show()
                return false
            }
            if (codigo.length != 6) {
                Toast.makeText(this, "Ingrese un código de aprobación válido", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun mostrarNotificacionStockBajo(context: Context, nombreProducto: String) {
        val builder = NotificationCompat.Builder(context, "canal_stock_id")
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("Stock insuficiente")
            .setContentText("El producto $nombreProducto se quedó sin stock.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1001, builder.build())
    }

    private fun procesarCompra() {
        val carritoProductos = dbHelper.obtenerProductosCarrito()

        if (carritoProductos.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        var error = false
        for (producto in carritoProductos) {
            val resultado = dbHelper.reducirStockProducto(producto.id, producto.cantidad)
            if (resultado != DBHelper.ResultadoStock.OK) {
                error = true
                val nombreProducto = dbHelper.obtenerNombreProducto(producto.id)
                mostrarNotificacionStockBajo(this, nombreProducto)
                break
            }
        }

        if (error) {
            Toast.makeText(this, "Error al comprar . Stock insuficiente.", Toast.LENGTH_LONG).show()
            return
        }

        val limpio = dbHelper.limpiarCarrito()
        if (!limpio) {
            Toast.makeText(this, "Error al limpiar carrito", Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(this, "Pago confirmado. ¡Gracias por su compra!", Toast.LENGTH_LONG).show()

        val intent = Intent(this, ClienteActivity::class.java)
        startActivity(intent)

        finish()
    }

}
