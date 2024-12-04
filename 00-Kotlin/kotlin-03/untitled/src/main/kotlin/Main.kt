package org.example

fun calcularSueldo(
    sueldo: Double,
    tasa: Double = 12.00,
    bonoEspecial: Double? = null
): Double {
    return if (bonoEspecial == null) {
        sueldo * (100 / tasa)
    } else {
        sueldo * (100 / tasa) * bonoEspecial
    }
}

abstract class Numeros(
    protected val numeroUno: Int,
    protected val numeroDos: Int,
    parametroNoUsado: Int
) {
    init {
        println("Inicializando")
    }
}

class EjemploClase(
    numeroUno: Int,
    numeroDos: Int
) : Numeros(numeroUno, numeroDos, 0) {
    fun mostrarNumeros() {
        println("Número uno: $numeroUno, Número dos: $numeroDos")
    }
}

fun main(args: Array<String>) {
    // Uso de calcularSueldo
    println(calcularSueldo(sueldo = 10.00))
    println(calcularSueldo(sueldo = 10.00, tasa = 15.00, bonoEspecial = 20.00))
    println(calcularSueldo(sueldo = 10.00, bonoEspecial = 20.00))

    // Uso de clases
    val ejemplo = EjemploClase(5, 10)
    ejemplo.mostrarNumeros()
}