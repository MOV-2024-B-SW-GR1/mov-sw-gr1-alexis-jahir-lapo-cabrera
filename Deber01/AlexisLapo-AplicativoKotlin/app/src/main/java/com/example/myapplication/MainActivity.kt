package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Con DataStore no hay que crear ni abrir una DB
        binding.tvStatus.text = "Estado: Almacenamiento en memoria activo"

        setupSpinner()
        setupButtons()
    }

    private fun setupSpinner() {
        val opciones = arrayOf("Seleccione una opción", "Avión", "Parte de Avión")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipo.adapter = adapter

        binding.spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> { // Ninguna selección
                        deshabilitarBotones()
                    }
                    1 -> { // Avión
                        habilitarBotones()
                    }
                    2 -> { // Parte de Avión
                        if (avionesDisponibles()) {
                            habilitarBotones()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Debe registrar al menos un avión primero",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.spinnerTipo.setSelection(0)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                deshabilitarBotones()
            }
        }
    }

    private fun setupButtons() {
        binding.btnInsert.setOnClickListener {
            when (binding.spinnerTipo.selectedItemPosition) {
                1 -> { // Avión
                    val intent = Intent(this, Avion::class.java).apply {
                        putExtra("msg", "add")
                    }
                    startActivity(intent)
                }
                2 -> { // Parte de Avión
                    if (avionesDisponibles()) {
                        val intent = Intent(this, ParteAvion::class.java).apply {
                            putExtra("msg", "add")
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "Debe registrar al menos un avión primero",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.spinnerTipo.setSelection(0)
                    }
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            when (binding.spinnerTipo.selectedItemPosition) {
                1 -> { // Avión
                    val intent = Intent(this, ListaAvionesActivity::class.java).apply {
                        putExtra("action", "edit")
                    }
                    startActivity(intent)
                }
                2 -> { // Parte de Avión
                    val intent = Intent(this, ListaAvionesActivity::class.java).apply {
                        putExtra("action", "edit_partes") // Editar partes de avión
                    }
                    startActivity(intent)
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            when (binding.spinnerTipo.selectedItemPosition) {
                1 -> { // Avión
                    val intent = Intent(this, ListaAvionesActivity::class.java).apply {
                        putExtra("action", "delete")
                    }
                    startActivity(intent)
                }
                2 -> { // Parte de Avión
                    val intent = Intent(this, ListaAvionesActivity::class.java).apply {
                        putExtra("action", "delete_partes") // Eliminar partes de avión
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun habilitarBotones() {
        binding.btnInsert.isEnabled = true
        binding.btnEdit.isEnabled = true
        binding.btnDelete.isEnabled = true
    }

    private fun deshabilitarBotones() {
        binding.btnInsert.isEnabled = false
        binding.btnEdit.isEnabled = false
        binding.btnDelete.isEnabled = false
    }

    /**
     * Verifica si hay aviones en memoria usando DataStore
     */
    private fun avionesDisponibles(): Boolean {
        return DataStore.aviones.isNotEmpty()
    }

    /**
     * Si la app vuelve a esta pantalla, podrías refrescar el estado del spinner
     * en caso de que se haya borrado el último avión, etc.
     */
    override fun onResume() {
        super.onResume()
        if (binding.spinnerTipo.selectedItemPosition == 2 && !avionesDisponibles()) {
            binding.spinnerTipo.setSelection(0)
        }
    }
}
