package com.example.proyectov2


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAdmin = findViewById<Button>(R.id.btnAdmin)
        val btnCliente1 = findViewById<Button>(R.id.btnCliente)

        btnAdmin.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
        }

        btnCliente1.setOnClickListener {
            startActivity(Intent(this, ClienteActivity::class.java))
        }
    }
}
