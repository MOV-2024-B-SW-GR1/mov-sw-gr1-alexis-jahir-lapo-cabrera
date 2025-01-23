package com.example.myapplication

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityParteAvionBinding
import android.util.Log

class ParteAvion : AppCompatActivity() {
    private lateinit var binding: ActivityParteAvionBinding
    private lateinit var db: SQLiteDatabase
    private var pId: Int = 0
    private var aId: Int = 0
    private var msg: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParteAvionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ConnectionClass.getConnection(this)
        val i = intent
        msg = i.getStringExtra("msg").toString()
        pId = i.getIntExtra("pId", 0)
        aId = i.getIntExtra("aId", 0)

        when (msg) {
            "add" -> {
                binding.btnSave.text = "Guardar"
                binding.btnSave.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.GONE
                cargarAviones()
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
                bloquearCampos() // Bloquear los campos en modo "delete"
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

    private fun cargarAviones() {
        val cursor = db.rawQuery("SELECT id, nombre FROM avion", null)
        val aviones = mutableListOf<String>()
        val avionIds = mutableListOf<Int>()

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(ConnectionClass.COL_AVION_ID))
                val nombre = it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_AVION_NOMBRE))
                aviones.add(nombre)
                avionIds.add(id)
            }
        }

        if (aviones.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, aviones)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerAviones.adapter = adapter

            binding.spinnerAviones.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    aId = avionIds[position]
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
        val cursor = db.rawQuery("SELECT * FROM parte WHERE id = ?", arrayOf(pId.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                aId = it.getInt(it.getColumnIndexOrThrow(ConnectionClass.COL_PARTE_AVION_ID))
                binding.etNombreParte.setText(it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_PARTE_NOMBRE)))
                binding.etPrecioRepuesto.setText(it.getDouble(it.getColumnIndexOrThrow(ConnectionClass.COL_PARTE_PRECIO)).toString())
                binding.etHorasUso.setText(it.getInt(it.getColumnIndexOrThrow(ConnectionClass.COL_PARTE_HORAS)).toString())

                val avionCursor = db.rawQuery("SELECT id, nombre FROM avion", null)
                avionCursor.use { avionIt ->
                    val aviones = mutableListOf<String>()
                    val avionIds = mutableListOf<Int>()
                    while (avionIt.moveToNext()) {
                        val id = avionIt.getInt(avionIt.getColumnIndexOrThrow(ConnectionClass.COL_AVION_ID))
                        val nombre = avionIt.getString(avionIt.getColumnIndexOrThrow(ConnectionClass.COL_AVION_NOMBRE))
                        aviones.add(nombre)
                        avionIds.add(id)
                    }

                    if (aviones.isNotEmpty()) {
                        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, aviones)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerAviones.adapter = adapter

                        val avionPosition = avionIds.indexOf(aId)
                        if (avionPosition != -1) {
                            binding.spinnerAviones.setSelection(avionPosition)
                        }
                    }
                }
            }
        }
    }

    private fun deleteData() {
        // Mostrar un cuadro de diálogo de confirmación
        AlertDialog.Builder(this)
            .setTitle("Eliminar Parte")
            .setMessage("¿Estás seguro de que deseas eliminar esta parte?")
            .setPositiveButton("Sí") { _, _ ->
                val selection = "${ConnectionClass.COL_PARTE_ID} = ?"
                val selectionArgs = arrayOf(pId.toString())

                val count = db.delete(ConnectionClass.TABLE_PARTE, selection, selectionArgs)
                if (count > 0) {
                    Toast.makeText(this, "Parte del avión eliminada exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al eliminar parte del avión", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun bloquearCampos() {
        binding.etNombreParte.isEnabled = false
        binding.etPrecioRepuesto.isEnabled = false
        binding.etHorasUso.isEnabled = false
        binding.spinnerAviones.isEnabled = false
    }

    private fun insertData() {
        val nombreParte = binding.etNombreParte.text.toString()
        val precioRepuesto = binding.etPrecioRepuesto.text.toString().toDoubleOrNull() ?: 0.0
        val horasUso = binding.etHorasUso.text.toString().toIntOrNull() ?: 0

        if (aId == 0) {
            Toast.makeText(this, "Debe seleccionar un avión", Toast.LENGTH_SHORT).show()
            return
        }

        val values = ContentValues().apply {
            put(ConnectionClass.COL_PARTE_AVION_ID, aId)
            put(ConnectionClass.COL_PARTE_NOMBRE, nombreParte)
            put(ConnectionClass.COL_PARTE_PRECIO, precioRepuesto)
            put(ConnectionClass.COL_PARTE_HORAS, horasUso)
        }

        val newRowId = db.insert(ConnectionClass.TABLE_PARTE, null, values)
        if (newRowId > 0) {
            Toast.makeText(this, "Parte del avión insertado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al insertar parte del avión", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateData() {
        val nombreParte = binding.etNombreParte.text.toString()
        val precioRepuesto = binding.etPrecioRepuesto.text.toString().toDoubleOrNull() ?: 0.0
        val horasUso = binding.etHorasUso.text.toString().toIntOrNull() ?: 0

        val values = ContentValues().apply {
            put(ConnectionClass.COL_PARTE_NOMBRE, nombreParte)
            put(ConnectionClass.COL_PARTE_PRECIO, precioRepuesto)
            put(ConnectionClass.COL_PARTE_HORAS, horasUso)
        }

        val selection = "${ConnectionClass.COL_PARTE_ID} = ?"
        val selectionArgs = arrayOf(pId.toString())

        val count = db.update(ConnectionClass.TABLE_PARTE, values, selection, selectionArgs)
        if (count > 0) {
            Toast.makeText(this, "Parte del avión actualizado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar parte del avión", Toast.LENGTH_SHORT).show()
        }
    }


}