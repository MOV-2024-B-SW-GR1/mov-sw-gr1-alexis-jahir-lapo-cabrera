package com.example.myapplication

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityListaAvionesBinding

class ListaAvionesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaAvionesBinding
    private var db: SQLiteDatabase? = null
    private var action: String = "edit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaAvionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        action = intent.getStringExtra("action") ?: "edit"

        // Configurar el título según la acción
        binding.tvTitulo.text = when (action) {
            "edit" -> "Editar Avión"
            "delete" -> "Eliminar Avión"
            "edit_partes" -> "Elegir Avión"
            else -> "Lista de Aviones"
        }

        db = ConnectionClass.getConnection(this)
        cargarAviones()
    }

    private fun cargarAviones() {
        try {
            val cursor = db?.rawQuery(
                """        
                SELECT id, nombre, fecha_fabricacion, estado_operativo, capacidad_pasajeros         
                FROM avion        
            """, null
            )

            val aviones = mutableListOf<Map<String, Any>>()

            cursor?.use {
                while (it.moveToNext()) {
                    aviones.add(
                        mapOf(
                            "id" to it.getInt(0),
                            "nombre" to it.getString(1),
                            "fecha" to it.getString(2),
                            "operativo" to it.getInt(3),
                            "capacidad" to it.getInt(4)
                        )
                    )
                }
            }

            if (aviones.isEmpty()) {
                Toast.makeText(this, "No hay aviones registrados", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            val adapter = AvionAdapter(
                this,
                aviones,
                showDeleteButton = action == "delete",
                onItemClick = { avion ->
                    when (action) {
                        "edit" -> { // Editar avión
                            val intent = Intent(this, Avion::class.java).apply {
                                putExtra("msg", "edit")
                                putExtra("aId", avion["id"] as Int)
                                putExtra("nombre", avion["nombre"] as String)
                                putExtra("fecha", avion["fecha"] as String)
                                putExtra("operativo", avion["operativo"] as Int)
                                putExtra("capacidad", avion["capacidad"] as Int)
                            }
                            startActivity(intent)
                            finish()
                        }

                        "edit_partes" -> { // Editar partes de avión
                            val aId = avion["id"] as Int
                            val cursor = db?.rawQuery(
                                "SELECT COUNT(*) FROM parte WHERE avion_id = ?",
                                arrayOf(aId.toString())
                            )
                            cursor?.use {
                                if (it.moveToFirst() && it.getInt(0) > 0) {
                                    // Si hay partes, abrir ListaPartesActivity
                                    val intent =
                                        Intent(this, ListaPartesActivity::class.java).apply {
                                            putExtra("aId", aId)
                                        }
                                    startActivity(intent)
                                } else {
                                    // Si no hay partes, mostrar mensaje
                                    Toast.makeText(
                                        this,
                                        "No hay partes registradas para este avión",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        "delete_partes" -> { // Eliminar partes de avión
                            val aId = avion["id"] as Int
                            val cursor = db?.rawQuery(
                                "SELECT COUNT(*) FROM parte WHERE avion_id = ?",
                                arrayOf(aId.toString())
                            )
                            cursor?.use {
                                if (it.moveToFirst() && it.getInt(0) > 0) {
                                    // Si hay partes, abrir ListaPartesActivity en modo eliminar
                                    val intent =
                                        Intent(this, ListaPartesActivity::class.java).apply {
                                            putExtra("aId", aId)
                                            putExtra(
                                                "action",
                                                "delete_partes"
                                            ) // Asegúrate de pasar la acción correcta
                                        }
                                    startActivity(intent)
                                } else {
                                    // Si no hay partes, mostrar mensaje
                                    Toast.makeText(
                                        this,
                                        "No hay partes registradas para este avión",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                },
                onDeleteClick = { avion ->
                    val intent = Intent(this, Avion::class.java).apply {
                        putExtra("msg", "delete")
                        putExtra("aId", avion["id"] as Int)
                        putExtra("nombre", avion["nombre"] as String)
                        putExtra("fecha", avion["fecha"] as String)
                        putExtra("operativo", avion["operativo"] as Int)
                        putExtra("capacidad", avion["capacidad"] as Int)
                    }
                    startActivity(intent)
                    finish()
                }
            )

            binding.listViewAviones.adapter = adapter

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar aviones: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}