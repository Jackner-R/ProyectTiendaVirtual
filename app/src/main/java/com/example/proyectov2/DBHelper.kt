    package com.example.proyectov2

    import android.content.ContentValues
    import android.content.Context
    import android.database.sqlite.SQLiteDatabase
    import android.database.sqlite.SQLiteOpenHelper

    class DBHelper(context: Context) :
        SQLiteOpenHelper(context, "tienda.db", null, 4) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("""
        CREATE TABLE productos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT,
            precio REAL,
            descripcion TEXT,
            imagen BLOB,
            stock INTEGER DEFAULT 0
        )
    """)

            db.execSQL(
                "CREATE TABLE carrito (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "producto_id INTEGER," +
                        "nombre TEXT," +
                        "precio REAL," +
                        "cantidad INTEGER," +
                        "imagen BLOB)"
            )

        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS productos")
            db.execSQL("DROP TABLE IF EXISTS carrito")
            onCreate(db)
        }

        fun insertarProducto(nombre: String, precio: Double, descripcion: String, imagen: ByteArray, stock: Int) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("nombre", nombre)
                put("precio", precio)
                put("descripcion", descripcion)
                put("imagen", imagen)
                put("stock", stock)
            }
            db.insert("productos", null, values)
        }



        fun obtenerProductos(): List<Producto> {
            val productos = mutableListOf<Producto>()
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM productos", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val nombre = cursor.getString(1)
                    val precio = cursor.getDouble(2)
                    val descripcion = cursor.getString(3)
                    val imagen = cursor.getBlob(4)
                    val stock = cursor.getInt(5)

                    productos.add(Producto(id, nombre, descripcion, precio, imagen, stock))


                } while (cursor.moveToNext())
            }
            cursor.close()
            return productos
        }

        fun obtenerNombreProducto(idProducto: Int): String {
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT nombre FROM productos WHERE id = ?", arrayOf(idProducto.toString()))
            var nombre = "Producto"
            if (cursor.moveToFirst()) {
                nombre = cursor.getString(0)
            }
            cursor.close()
            return nombre
        }




        fun agregarAlCarrito(producto: Producto, cantidad: Int = 1) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("producto_id", producto.id)
                put("nombre", producto.nombre)
                put("precio", producto.precio)
                put("cantidad", cantidad)
                put("imagen", producto.imagen)
            }
            db.insert("carrito", null, values)
            db.close()
        }


        fun eliminarDelCarrito(id: Int) {
            val db = writableDatabase
            db.delete("carrito", "id = ?", arrayOf(id.toString()))
            db.close()
        }


        fun obtenerCarrito(): List<ItemCarrito> {
            val carrito = mutableListOf<ItemCarrito>()
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM carrito", null)

            if (cursor.moveToFirst()) {
                do {
                    val item = ItemCarrito(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                        cursor.getBlob(cursor.getColumnIndexOrThrow("imagen"))
                    )
                    carrito.add(item)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
            return carrito
        }



        fun obtenerProductosCarrito(): List<ProductoCarrito> {
            val lista = mutableListOf<ProductoCarrito>()
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT producto_id, cantidad FROM carrito", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val cantidad = cursor.getInt(1)
                    lista.add(ProductoCarrito(id, cantidad))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return lista
        }

        enum class ResultadoStock {
            OK,
            SIN_STOCK
        }

        fun reducirStockProducto(idProducto: Int, cantidadComprada: Int): ResultadoStock {
            val db = writableDatabase
            val cursor = db.rawQuery("SELECT stock FROM productos WHERE id = ?", arrayOf(idProducto.toString()))
            if (!cursor.moveToFirst()) {
                cursor.close()
                db.close()
                return ResultadoStock.SIN_STOCK
            }
            val stockActual = cursor.getInt(0)
            cursor.close()
            val nuevoStock = stockActual - cantidadComprada
            if (nuevoStock < 0) {
                db.close()
                return ResultadoStock.SIN_STOCK
            }

            val contentValues = ContentValues()
            contentValues.put("stock", nuevoStock)
            val rows = db.update("productos", contentValues, "id=?", arrayOf(idProducto.toString()))
            db.close()
            return if (rows > 0) ResultadoStock.OK else ResultadoStock.SIN_STOCK
        }



        fun limpiarCarrito(): Boolean {
            val db = writableDatabase
            val rows = db.delete("carrito", null, null)
            return rows >= 0
        }






    }
