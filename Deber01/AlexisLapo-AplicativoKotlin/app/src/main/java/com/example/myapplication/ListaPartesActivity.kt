package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityListaPartesBinding

class ListaPartesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaPartesBinding
    private var aId: Int = 0
    private var action: String = "edit_partes"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaPartesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        aId = intent.getIntExtra("aId", 0)
        action = intent.getStringExtra("action") ?: "edit_partes"

        cargarPartes()
    }

    private fun cargarPartes() {
        try {
            // Filtramos las partes en memoria que pertenecen al avión aId
            val partesAvion = DataStore.partes.filter { it["avion_id"] == aId }

            if (partesAvion.isEmpty()) {
                Toast.makeText(
                    this,
                    "No hay partes registradas para este avión",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return
            }

            val adapter = ParteAdapter(
                this,
                partesAvion,
                onItemClick = { parte ->
                    when (action) {
                        "edit_partes" -> {
                            // Abrir ParteAvion en modo edición
                            val intent = Intent(this, ParteAvion::class.java).apply {
                                putExtra("msg", "edit_partes")
                                putExtra("pId", parte["id"] as Int)
                                putExtra("aId", aId)
                            }
                            startActivity(intent)
                        }

                        "delete_partes" -> {
                            // Abrir ParteAvion en modo eliminar
                            val intent = Intent(this, ParteAvion::class.java).apply {
                                putExtra("msg", "delete_partes")
                                putExtra("pId", parte["id"] as Int)
                                putExtra("aId", aId)
                            }
                            startActivity(intent)
                        }
                    }
                }
            )

            binding.listViewPartes.adapter = adapter

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar partes: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
