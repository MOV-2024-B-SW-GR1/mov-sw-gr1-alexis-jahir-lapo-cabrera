package com.example.myapplication

// Modelo de Avión
data class AvionModel(
    val id: Int,
    var nombre: String,
    var fechaFabricacion: String,
    var estadoOperativo: Boolean,
    var capacidadPasajeros: Int
)

// Modelo de Parte
data class ParteModel(
    val id: Int,
    val avionId: Int,
    var nombre: String,
    var precio: Double,
    var horasUso: Int
)

/**
 * Objeto singleton que simula un "repositorio" en memoria.
 * Mantiene listas de aviones y partes.
 */
object MemoryDataSource {

    // Listas en memoria
    private val aviones = mutableListOf<AvionModel>()
    private val partes = mutableListOf<ParteModel>()

    // Contadores para simular IDs autoincrementales
    private var nextAvionId = 1
    private var nextParteId = 1

    // == AVIONES ==

    /**
     * Retorna todos los aviones en memoria
     */
    fun getAllAviones(): List<AvionModel> {
        return aviones
    }

    /**
     * Inserta un nuevo avión
     */
    fun insertAvion(nombre: String, fechaFab: String, estado: Boolean, capacidad: Int): Int {
        val newAvion = AvionModel(
            id = nextAvionId++,
            nombre = nombre,
            fechaFabricacion = fechaFab,
            estadoOperativo = estado,
            capacidadPasajeros = capacidad
        )
        aviones.add(newAvion)
        return newAvion.id
    }

    /**
     * Retorna un avión por ID (o null si no existe)
     */
    fun getAvionById(avionId: Int): AvionModel? {
        return aviones.find { it.id == avionId }
    }

    /**
     * Actualiza un avión existente
     */
    fun updateAvion(avionId: Int, nombre: String, fechaFab: String, estado: Boolean, capacidad: Int): Boolean {
        val avion = getAvionById(avionId) ?: return false
        avion.nombre = nombre
        avion.fechaFabricacion = fechaFab
        avion.estadoOperativo = estado
        avion.capacidadPasajeros = capacidad
        return true
    }

    /**
     * Elimina un avión
     */
    fun deleteAvion(avionId: Int): Boolean {
        // Antes de eliminar el avión, también podemos eliminar sus partes
        partes.removeAll { it.avionId == avionId }
        return aviones.removeIf { it.id == avionId }
    }

    // == PARTES ==

    /**
     * Retorna la lista de partes de un avión en particular
     */
    fun getPartesByAvion(avionId: Int): List<ParteModel> {
        return partes.filter { it.avionId == avionId }
    }

    /**
     * Retorna una parte específica (por ID)
     */
    fun getParteById(parteId: Int): ParteModel? {
        return partes.find { it.id == parteId }
    }

    /**
     * Inserta una nueva parte asociada a un avión
     */
    fun insertParte(avionId: Int, nombre: String, precio: Double, horasUso: Int): Int {
        val newParte = ParteModel(
            id = nextParteId++,
            avionId = avionId,
            nombre = nombre,
            precio = precio,
            horasUso = horasUso
        )
        partes.add(newParte)
        return newParte.id
    }

    /**
     * Actualiza una parte
     */
    fun updateParte(parteId: Int, nombre: String, precio: Double, horasUso: Int): Boolean {
        val parte = getParteById(parteId) ?: return false
        parte.nombre = nombre
        parte.precio = precio
        parte.horasUso = horasUso
        return true
    }

    /**
     * Elimina una parte
     */
    fun deleteParte(parteId: Int): Boolean {
        return partes.removeIf { it.id == parteId }
    }
}
