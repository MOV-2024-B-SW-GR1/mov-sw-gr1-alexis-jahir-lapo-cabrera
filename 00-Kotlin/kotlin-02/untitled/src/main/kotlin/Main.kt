package org.example

import java.util.Date


fun main() {
    // Variables mutables e inmutables
    val inmutable: String = "Vicente"
    var mutable: String = "Vicente"
    mutable = "Juan"

    // Duck Typing y variables primitivas
    val ejemploVariable = "Kevin"
    ejemploVariable.trim()
    val edadEjemplo: Int = 12

    val nombreProfesor: String = "Adrian Eguez"
    val sueldo: Double = 1.2
    val estadoCivil: Char = 'C'
    val mayorEdad: Boolean = true
    val fechaNacimiento: Date = Date()

    // When (Switch)
    val estadoCivilWhen = "C"
    when (estadoCivilWhen) {
        "C" -> {
            println("Casado")
        }
        "S" -> {
            println("Soltero")
        }
        else -> {
            println("No reconocido")
        }
    }

    // If-else simplificado
    val esSoltero = (estadoCivilWhen == "S")
    val coqueto = if (esSoltero) "Si" else "No"

    // Llamada a la función con template strings
    imprimirNombre("Kevin")
}

// Función con template strings y función anidada
fun imprimirNombre(nombre: String): Unit {
    fun otraFuncionAdentro() {
        println("Otra funcion adentro")
    }

    // Template Strings
    println("Nombre: $nombre")                    // Uso sin llaves
    println("Nombre: ${nombre}")                  // Uso con llaves
    println("Nombre: ${nombre + nombre}")         // Uso con llaves concatenando
    println("Nombre: ${nombre.toString()}")       // Uso con llaves función
    println("Nombre: $nombre.toString()")         // INCORRECTO! No se puede usar sin llaves

    otraFuncionAdentro()
}