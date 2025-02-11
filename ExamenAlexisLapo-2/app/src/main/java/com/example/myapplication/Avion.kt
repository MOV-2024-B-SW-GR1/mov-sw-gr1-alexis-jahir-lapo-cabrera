package com.example.myapplication

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityAvionBinding
import java.text.SimpleDateFormat
import java.util.Locale
import android.widget.ArrayAdapter

class Avion : AppCompatActivity() {
    private lateinit var binding: ActivityAvionBinding
    private var db: SQLiteDatabase? = null
    private var msg = ""
    private var aId = 0

    private data class Ubicacion(
        val nombre: String,
        val latitud: Double,
        val longitud: Double
    )

    private val ubicaciones = listOf(
        Ubicacion(
            "Aeropuerto Internacional Mariscal Sucre (Quito/Tababela)",
            -0.1892,
            -78.3577
        ),
        Ubicacion(
            "Aeropuerto de Latacunga",
            -0.9333,
            -78.6200
        ),
        Ubicacion(
            "Aeropuerto de Ambato",
            -1.2500,
            -78.6167
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ConnectionClass.getConnection(this)
        msg = intent.getStringExtra("msg").toString()
        aId = intent.getIntExtra("aId", 0)

        when (msg) {
            "add" -> {
                // Modo añadir: solo mostrar botón guardar
                binding.btnSave.text = "Guardar"
                binding.btnSave.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.GONE
                habilitarCampos(true)
            }
            "edit" -> {
                // Modo editar: solo mostrar botón actualizar
                binding.btnSave.text = "Actualizar"
                binding.btnSave.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.GONE
                habilitarCampos(true)
                cargarDatosAvion()
            }
            "delete" -> {
                // Modo eliminar: solo mostrar botón eliminar
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

        setupUbicacionSpinner()
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
        
        // Seleccionar la ubicación correcta en el spinner
        val ubicacion = intent.getStringExtra("ubicacion") ?: ""
        val position = ubicaciones.indexOfFirst { it.nombre == ubicacion }
        if (position != -1) {
            binding.spinnerUbicacion.setSelection(position)
        }
    }

    private fun setupUbicacionSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ubicaciones.map { it.nombre }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUbicacion.adapter = adapter
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

        // Validar formato de fecha
        try {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(binding.etFechaFabricacion.text.toString().trim())
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Formato de fecha inválido. Use dd/mm/yyyy",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun insertData() {
        if (!validarCampos()) return

        try {
            val ubicacionSeleccionada = ubicaciones[binding.spinnerUbicacion.selectedItemPosition]
            
            val values = android.content.ContentValues().apply {
                put(ConnectionClass.COL_AVION_NOMBRE, binding.etNombre.text.toString().trim())
                put(
                    ConnectionClass.COL_AVION_FECHA,
                    binding.etFechaFabricacion.text.toString().trim()
                )
                put(
                    ConnectionClass.COL_AVION_OPERATIVO,
                    if (binding.cbEstadoOperativo.isChecked) 1 else 0
                )
                put(
                    ConnectionClass.COL_AVION_CAPACIDAD,
                    binding.etCapacidadPasajeros.text.toString().toInt()
                )
                put(ConnectionClass.COL_AVION_LATITUD, ubicacionSeleccionada.latitud)
                put(ConnectionClass.COL_AVION_LONGITUD, ubicacionSeleccionada.longitud)
                put(ConnectionClass.COL_AVION_UBICACION, ubicacionSeleccionada.nombre)
            }

            val result = db?.insert(ConnectionClass.TABLE_AVION, null, values)
            if (result != -1L) {
                Toast.makeText(this, "Avión registrado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al registrar avión", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateData() {
        if (!validarCampos()) return

        try {
            val ubicacionSeleccionada = ubicaciones[binding.spinnerUbicacion.selectedItemPosition]
            
            val values = android.content.ContentValues().apply {
                put(ConnectionClass.COL_AVION_NOMBRE, binding.etNombre.text.toString().trim())
                put(
                    ConnectionClass.COL_AVION_FECHA,
                    binding.etFechaFabricacion.text.toString().trim()
                )
                put(
                    ConnectionClass.COL_AVION_OPERATIVO,
                    if (binding.cbEstadoOperativo.isChecked) 1 else 0
                )
                put(
                    ConnectionClass.COL_AVION_CAPACIDAD,
                    binding.etCapacidadPasajeros.text.toString().toInt()
                )
                put(ConnectionClass.COL_AVION_LATITUD, ubicacionSeleccionada.latitud)
                put(ConnectionClass.COL_AVION_LONGITUD, ubicacionSeleccionada.longitud)
                put(ConnectionClass.COL_AVION_UBICACION, ubicacionSeleccionada.nombre)
            }

            val selection = "${ConnectionClass.COL_AVION_ID} = ?"
            val selectionArgs = arrayOf(aId.toString())

            val count = db?.update(ConnectionClass.TABLE_AVION, values, selection, selectionArgs)
            if (count != null && count > 0) {
                Toast.makeText(this, "Avión actualizado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar avión", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
        try {
            val selection = "${ConnectionClass.COL_AVION_ID} = ?"
            val selectionArgs = arrayOf(aId.toString())

            val count = db?.delete(ConnectionClass.TABLE_AVION, selection, selectionArgs)
            if (count != null && count > 0) {
                Toast.makeText(this, "Avión eliminado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al eliminar avión", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db?.close()
    }
}