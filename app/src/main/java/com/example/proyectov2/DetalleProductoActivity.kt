package com.example.proyectov2


import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class DetalleProductoActivity : AppCompatActivity() {

    private lateinit var imagen: ImageView
    private lateinit var nombre: TextView
    private lateinit var precio: TextView
    private lateinit var descripcion: TextView
    private lateinit var cantidad: EditText
    private lateinit var btnAgregarCarrito: Button

    private lateinit var producto: Producto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_producto)

        imagen = findViewById(R.id.ivDetalleImagen)
        nombre = findViewById(R.id.tvDetalleNombre)
        precio = findViewById(R.id.tvDetallePrecio)
        descripcion = findViewById(R.id.tvDetalleDescripcion)
        cantidad = findViewById(R.id.etCantidad)
        btnAgregarCarrito = findViewById(R.id.btnAgregarCarrito)

        // Recibir el producto por intent
        producto = intent.getSerializableExtra("producto") as Producto

        // Mostrar datos
        val bitmap = BitmapFactory.decodeByteArray(producto.imagen, 0, producto.imagen.size)
        imagen.setImageBitmap(bitmap)
        nombre.text = producto.nombre
        precio.text = "S/. %.2f".format(producto.precio)
        descripcion.text = producto.descripcion

        // Agregar al carrito
        btnAgregarCarrito.setOnClickListener {
            val cantidadTexto = cantidad.text.toString()
            if (cantidadTexto.isBlank() || cantidadTexto.toIntOrNull() == null || cantidadTexto.toInt() < 1) {
                Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
            } else {
                val cantidadNum = cantidadTexto.toInt()
                val dbHelper = DBHelper(this)
                dbHelper.agregarAlCarrito(producto, cantidadNum)
                Toast.makeText(this, "Agregado al carrito", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
