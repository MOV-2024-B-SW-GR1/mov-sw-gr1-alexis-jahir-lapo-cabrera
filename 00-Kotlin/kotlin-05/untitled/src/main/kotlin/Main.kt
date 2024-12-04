package org.example

fun main() {
    // Arreglos Estáticos
    val arregloEstatico: Array<Int> = arrayOf(1, 2, 3)
    println(arregloEstatico)

    // Arreglos Dinámicos
    val arregloDinamico: ArrayList<Int> = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    println(arregloDinamico)
    arregloDinamico.add(11)
    arregloDinamico.add(12)
    println(arregloDinamico)

    // Iterar con forEach
    arregloDinamico.forEach { valorActual: Int ->
        println("Valor actual: $valorActual")
    }
    arregloDinamico.forEach {
        println("Valor actual (it): $it")
    }

    // Transformar con map
    val respuestaMap: List<Double> = arregloDinamico.map { valorActual: Int ->
        return@map valorActual.toDouble() + 100.00
    }
    println(respuestaMap)
    val respuestaMapDos = arregloDinamico.map { it + 15 }
    println(respuestaMapDos)

    // Filtrar con filter
    val respuestaFilter: List<Int> = arregloDinamico.filter { valorActual: Int ->
        val mayoresCinco: Boolean = valorActual > 5
        return@filter mayoresCinco
    }
    println(respuestaFilter)
    val respuestaFilterDos = arregloDinamico.filter { it <= 5 }
    println(respuestaFilterDos)

    // Validaciones con all y any
    val respuestaAny: Boolean = arregloDinamico.any { valorActual: Int ->
        return@any valorActual > 5
    }
    println(respuestaAny) // true
    val respuestaAll: Boolean = arregloDinamico.all { valorActual: Int ->
        return@all valorActual > 5
    }
    println(respuestaAll) // false

    // Acumulación con reduce
    val respuestaReduce = arregloDinamico.reduce { acumulado: Int, valorActual: Int ->
        return@reduce acumulado + valorActual
    }
    println(respuestaReduce) // 78
}