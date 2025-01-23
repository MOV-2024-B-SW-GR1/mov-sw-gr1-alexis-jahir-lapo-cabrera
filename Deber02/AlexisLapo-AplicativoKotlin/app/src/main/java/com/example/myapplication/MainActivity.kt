package com.example.myapplication

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var db: SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar la base de datos
        createDatabase()
        setupSpinner()
        setupButtons()
    }

    private fun createDatabase() {
        try {
            db = ConnectionClass.getConnection(this)
            binding.tvStatus.text = "Estado: Base de datos creada exitosamente"
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvStatus.text = "Estado: Error - ${e.message}"
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSpinner() {
        val opciones = arrayOf("Seleccione una opción", "Avión", "Parte de Avión")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipo.adapter = adapter

        binding.spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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
                    val intent = Intent(this, Avion::class.java)
                    intent.putExtra("msg", "add")
                    startActivity(intent)
                }
                2 -> { // Parte de Avión
                    if (avionesDisponibles()) {
                        val intent = Intent(this, ParteAvion::class.java)
                        intent.putExtra("msg", "add")
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
                    val intent = Intent(this, ListaAvionesActivity::class.java)
                    intent.putExtra("action", "edit")
                    startActivity(intent)
                }
                2 -> { // Parte de Avión
                    val intent = Intent(this, ListaAvionesActivity::class.java)
                    intent.putExtra("action", "edit_partes") // Editar partes de avión
                    startActivity(intent)
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            when (binding.spinnerTipo.selectedItemPosition) {
                1 -> { // Avión
                    val intent = Intent(this, ListaAvionesActivity::class.java)
                    intent.putExtra("action", "delete")
                    startActivity(intent)
                }
                2 -> { // Parte de Avión
                    val intent = Intent(this, ListaAvionesActivity::class.java)
                    intent.putExtra("action", "delete_partes") // Eliminar partes de avión
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

    private fun avionesDisponibles(): Boolean {
        return try {
            // Asegurarse de obtener una conexión válida
            db = ConnectionClass.getConnection(this)

            val cursor = db?.rawQuery("SELECT COUNT(*) FROM avion", null)
            cursor?.use {
                if (it.moveToFirst()) {
                    return it.getInt(0) > 0
                }
                false
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Error al verificar aviones: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualizar la conexión a la base de datos
        db = ConnectionClass.getConnection(this)

        // Actualizar el estado del spinner cuando se regrese a la actividad
        if (binding.spinnerTipo.selectedItemPosition == 2 && !avionesDisponibles()) {
            binding.spinnerTipo.setSelection(0)
        }
    }
}