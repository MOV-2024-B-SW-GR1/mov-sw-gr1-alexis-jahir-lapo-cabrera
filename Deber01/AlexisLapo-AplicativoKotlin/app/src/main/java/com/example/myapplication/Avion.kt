package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityAvionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class Avion : AppCompatActivity() {
    private lateinit var binding: ActivityAvionBinding
    private var msg = ""
    private var aId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        msg = intent.getStringExtra("msg").toString()
        aId = intent.getIntExtra("aId", 0)

        when (msg) {
            "add" -> {
                binding.btnSave.text = "Guardar"
                binding.btnSave.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.GONE
                habilitarCampos(true)
            }
            "edit" -> {
                binding.btnSave.text = "Actualizar"
                binding.btnSave.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.GONE
                habilitarCampos(true)
                cargarDatosAvion()
            }
            "delete" -> {
                binding.btnSave.visibility = View.GONE
                binding.btnDelete.visibility = View.VISIBLE
                habilitarCampos(false)
                cargarDatosAvion()
            }
        }

        binding.btnSave.setOnClickListener {
            when (msg) {
                "add" -> insertData()
                "edit" -> updateData()
            }
        }

        binding.btnDelete.setOnClickListener {
            confirmarEliminacion()
        }
    }

    private fun habilitarCampos(enabled: Boolean) {
        binding.etNombre.isEnabled = enabled
        binding.etFechaFabricacion.isEnabled = enabled
        binding.cbEstadoOperativo.isEnabled = enabled
        binding.etCapacidadPasajeros.isEnabled = enabled
    }

    private fun cargarDatosAvion() {
        binding.etNombre.setText(intent.getStringExtra("nombre"))
        binding.etFechaFabricacion.setText(intent.getStringExtra("fecha"))
        binding.cbEstadoOperativo.isChecked = intent.getIntExtra("operativo", 0) == 1
        binding.etCapacidadPasajeros.setText(intent.getIntExtra("capacidad", 0).toString())
    }

    private fun validarCampos(): Boolean {
        if (binding.etNombre.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "El nombre es requerido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etFechaFabricacion.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "La fecha de fabricación es requerida", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etCapacidadPasajeros.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "La capacidad de pasajeros es requerida", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validar formato de fecha (opcional)
        try {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(binding.etFechaFabricacion.text.toString().trim())
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Formato de fecha inválido. Use yyyy/MM/dd",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun insertData() {
        if (!validarCampos()) return

        val nombre = binding.etNombre.text.toString().trim()
        val fecha = binding.etFechaFabricacion.text.toString().trim()
        val operativo = binding.cbEstadoOperativo.isChecked
        val capacidad = binding.etCapacidadPasajeros.text.toString().toInt()

        val ok = DataStore.insertAvion(nombre, fecha, operativo, capacidad)
        if (ok) {
            Toast.makeText(this, "Avión registrado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al registrar avión", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateData() {
        if (!validarCampos()) return

        val nombre = binding.etNombre.text.toString().trim()
        val fecha = binding.etFechaFabricacion.text.toString().trim()
        val operativo = binding.cbEstadoOperativo.isChecked
        val capacidad = binding.etCapacidadPasajeros.text.toString().toInt()

        val ok = DataStore.updateAvion(aId, nombre, fecha, operativo, capacidad)
        if (ok) {
            Toast.makeText(this, "Avión actualizado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar avión", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmarEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Está seguro que desea eliminar este avión?")
            .setPositiveButton("Sí") { _, _ ->
                deleteData()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteData() {
        val ok = DataStore.deleteAvion(aId)
        if (ok) {
            Toast.makeText(this, "Avión eliminado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al eliminar avión", Toast.LENGTH_SHORT).show()
        }
    }
}
