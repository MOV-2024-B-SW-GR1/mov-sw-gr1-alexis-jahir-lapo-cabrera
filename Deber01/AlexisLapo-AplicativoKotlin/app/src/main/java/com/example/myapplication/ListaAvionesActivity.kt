package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityListaAvionesBinding

class ListaAvionesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaAvionesBinding
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

        cargarAviones()
    }

    private fun cargarAviones() {
        try {
            val aviones = DataStore.aviones // Lista en memoria

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
                            // Verificamos si tiene partes
                            val numPartes = DataStore.partes.count { it["avion_id"] == aId }
                            if (numPartes > 0) {
                                val intent = Intent(this, ListaPartesActivity::class.java).apply {
                                    putExtra("aId", aId)
                                }
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this,
                                    "No hay partes registradas para este avión",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        "delete_partes" -> { // Eliminar partes de avión
                            val aId = avion["id"] as Int
                            val numPartes = DataStore.partes.count { it["avion_id"] == aId }
                            if (numPartes > 0) {
                                val intent = Intent(this, ListaPartesActivity::class.java).apply {
                                    putExtra("aId", aId)
                                    putExtra("action", "delete_partes")
                                }
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this,
                                    "No hay partes registradas para este avión",
                                    Toast.LENGTH_SHORT
                                ).show()
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
