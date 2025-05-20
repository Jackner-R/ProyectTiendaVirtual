package com.example.proyectov2


import java.io.Serializable

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagen: ByteArray,
    val stock: Int
) : Serializable