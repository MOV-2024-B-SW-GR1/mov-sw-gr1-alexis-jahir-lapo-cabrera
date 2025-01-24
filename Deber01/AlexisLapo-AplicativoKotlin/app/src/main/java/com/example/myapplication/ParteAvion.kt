package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityParteAvionBinding

class ParteAvion : AppCompatActivity() {
    private lateinit var binding: ActivityParteAvionBinding
    private var pId: Int = 0
    private var aId: Int = 0
    private var msg: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParteAvionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val i = intent
        msg = i.getStringExtra("msg").toString()
        pId = i.getIntExtra("pId", 0)
        aId = i.getIntExtra("aId", 0)

        when (msg) {
            "add" -> {
                binding.btnSave.text = "Guardar"
                binding.btnSave.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.GONE
                cargarAvionesEnSpinner()
            }
            "edit_partes" -> {
                binding.btnSave.text = "Actualizar"
                binding.btnSave.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.GONE
                cargarDatosParte()
            }
            "delete_partes" -> {
                binding.btnSave.visibility = View.GONE
                binding.btnDelete.text = "Eliminar"
                binding.btnDelete.visibility = View.VISIBLE
                cargarDatosParte()
                bloquearCampos() // Bloquear campos en modo eliminar
            }
        }

        binding.btnSave.setOnClickListener {
            when (msg) {
                "add" -> insertData()
                "edit_partes" -> updateData()
            }
        }

        binding.btnDelete.setOnClickListener {
            if (msg == "delete_partes") {
                deleteData()
            }
        }
    }

    private fun bloquearCampos() {
        binding.etNombreParte.isEnabled = false
        binding.etPrecioRepuesto.isEnabled = false
        binding.etHorasUso.isEnabled = false
        binding.spinnerAviones.isEnabled = false
    }

    private fun cargarAvionesEnSpinner() {
        // Tomamos los aviones de DataStore
        val aviones = DataStore.aviones
        val nombres = mutableListOf<String>()
        val ids = mutableListOf<Int>()

        for (avion in aviones) {
            val id = avion["id"] as Int
            val nombre = avion["nombre"] as String
            nombres.add(nombre)
            ids.add(id)
        }

        if (nombres.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerAviones.adapter = adapter

            binding.spinnerAviones.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    aId = ids[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    aId = 0
                }
            }
        } else {
            Toast.makeText(this, "No hay aviones disponibles", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun cargarDatosParte() {
        val parte = DataStore.partes.firstOrNull { it["id"] == pId }
        if (parte != null) {
            aId = parte["avion_id"] as Int
            binding.etNombreParte.setText(parte["nombre"] as String)
            binding.etPrecioRepuesto.setText((parte["precio"] as Double).toString())
            binding.etHorasUso.setText((parte["horas"] as Int).toString())
        }
        // Llenar spinner aviones
        val aviones = DataStore.aviones
        val nombres = mutableListOf<String>()
        val ids = mutableListOf<Int>()

        for (avion in aviones) {
            val id = avion["id"] as Int
            val nombre = avion["nombre"] as String
            nombres.add(nombre)
            ids.add(id)
        }

        if (nombres.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerAviones.adapter = adapter
            // Seleccionar el avión correspondiente
            val pos = ids.indexOf(aId)
            if (pos != -1) {
                binding.spinnerAviones.setSelection(pos)
            }
        }
    }

    private fun deleteData() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Parte")
            .setMessage("¿Estás seguro de que deseas eliminar esta parte?")
            .setPositiveButton("Sí") { _, _ ->
                val ok = DataStore.deleteParte(pId)
                if (ok) {
                    Toast.makeText(this, "Parte del avión eliminada exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al eliminar parte del avión", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun insertData() {
        val nombreParte = binding.etNombreParte.text.toString()
        val precioRepuesto = binding.etPrecioRepuesto.text.toString().toDoubleOrNull() ?: 0.0
        val horasUso = binding.etHorasUso.text.toString().toIntOrNull() ?: 0

        if (aId == 0) {
            Toast.makeText(this, "Debe seleccionar un avión", Toast.LENGTH_SHORT).show()
            return
        }

        val ok = DataStore.insertParte(aId, nombreParte, precioRepuesto, horasUso)
        if (ok) {
            Toast.makeText(this, "Parte del avión insertada exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al insertar parte del avión", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateData() {
        val nombreParte = binding.etNombreParte.text.toString()
        val precioRepuesto = binding.etPrecioRepuesto.text.toString().toDoubleOrNull() ?: 0.0
        val horasUso = binding.etHorasUso.text.toString().toIntOrNull() ?: 0

        val ok = DataStore.updateParte(pId, nombreParte, precioRepuesto, horasUso)
        if (ok) {
            Toast.makeText(this, "Parte del avión actualizada exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar parte del avión", Toast.LENGTH_SHORT).show()
        }
    }
}
