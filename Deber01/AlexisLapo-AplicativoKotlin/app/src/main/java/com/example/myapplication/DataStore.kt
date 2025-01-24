package com.example.myapplication

object DataStore {

    // Listas en memoria
    // Cada elemento es un Map<String, Any> para minimizar cambios en tu código
    val aviones = mutableListOf<Map<String, Any>>()
    val partes = mutableListOf<Map<String, Any>>()

    // Contadores para IDs autoincrementales en memoria
    private var avionIdCounter = 1
    private var parteIdCounter = 1

    // ----------------------------------------------
    //              CRUD AVIONES
    // ----------------------------------------------
    fun insertAvion(nombre: String, fecha: String, operativo: Boolean, capacidad: Int): Boolean {
        val newAvion = mapOf(
            "id" to avionIdCounter,
            "nombre" to nombre,
            "fecha" to fecha,
            "operativo" to if (operativo) 1 else 0,
            "capacidad" to capacidad
        )
        aviones.add(newAvion)
        avionIdCounter++
        return true
    }

    fun updateAvion(avionId: Int, nombre: String, fecha: String, operativo: Boolean, capacidad: Int): Boolean {
        val index = aviones.indexOfFirst { it["id"] == avionId }
        if (index != -1) {
            val updatedAvion = mapOf(
                "id" to avionId,
                "nombre" to nombre,
                "fecha" to fecha,
                "operativo" to if (operativo) 1 else 0,
                "capacidad" to capacidad
            )
            aviones[index] = updatedAvion
            return true
        }
        return false
    }

    fun deleteAvion(avionId: Int): Boolean {
        val index = aviones.indexOfFirst { it["id"] == avionId }
        if (index != -1) {
            // Primero borrar partes asociadas a ese avión
            partes.removeAll { it["avion_id"] == avionId }
            // Luego eliminar el avión
            aviones.removeAt(index)
            return true
        }
        return false
    }

    // ----------------------------------------------
    //              CRUD PARTES
    // ----------------------------------------------
    fun insertParte(avionId: Int, nombreParte: String, precio: Double, horasUso: Int): Boolean {
        // Verificar que exista el avión al que se asocia la parte
        val avionIndex = aviones.indexOfFirst { it["id"] == avionId }
        if (avionIndex == -1) {
            return false // No existe el avión
        }
        val newParte = mapOf(
            "id" to parteIdCounter,
            "avion_id" to avionId,
            "nombre" to nombreParte,
            "precio" to precio,
            "horas" to horasUso
        )
        partes.add(newParte)
        parteIdCounter++
        return true
    }

    fun updateParte(parteId: Int, nombreParte: String, precio: Double, horasUso: Int): Boolean {
        val index = partes.indexOfFirst { it["id"] == parteId }
        if (index != -1) {
            val oldParte = partes[index]
            val avionId = oldParte["avion_id"] as Int
            val updatedParte = mapOf(
                "id" to parteId,
                "avion_id" to avionId,
                "nombre" to nombreParte,
                "precio" to precio,
                "horas" to horasUso
            )
            partes[index] = updatedParte
            return true
        }
        return false
    }

    fun deleteParte(parteId: Int): Boolean {
        val index = partes.indexOfFirst { it["id"] == parteId }
        if (index != -1) {
            partes.removeAt(index)
            return true
        }
        return false
    }

}
