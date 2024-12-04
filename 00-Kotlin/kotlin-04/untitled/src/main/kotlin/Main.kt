package org.example

abstract class Numeros(
    protected val numeroUno: Int,
    protected val numeroDos: Int
)

class Suma(
    unoParametro: Int,
    dosParametro: Int
) : Numeros(unoParametro, dosParametro) {
    public val soyPublicoExplicito: String = "Públicas"
    val soyPublicoImplicito = "Públicas"

    init {
        this.numeroUno
        this.numeroDos
        numeroUno
        numeroDos
        this.soyPublicoImplicito
        soyPublicoExplicito
    }

    constructor(uno: Int?, dos: Int) : this(
        if (uno == null) 0 else uno,
        dos
    )

    constructor(uno: Int?, dos: Int?) : this(
        if (uno == null) 0 else uno,
        if (dos == null) 0 else dos
    )

    fun sumar(): Int {
        val total = numeroUno + numeroDos
        agregarHistorial(total)
        return total
    }

    companion object {
        val pi = 3.14
        fun elevarAlCuadrado(num: Int): Int {
            return num * num
        }

        val historialSumas = arrayListOf<Int>()
        fun agregarHistorial(nuevaSuma: Int) {
            historialSumas.add(nuevaSuma)
        }
    }
}

fun main(args: Array<String>) {
    val sumaA = Suma(unoParametro = 1, dosParametro = 1)
    val sumaB = Suma(uno = null, dos = 1)
    val sumaC = Suma(uno = 1, dos = null)
    val sumaD = Suma(uno = null, dos = null)

    sumaA.sumar()
    sumaB.sumar()
    sumaC.sumar()
    sumaD.sumar()

    println(Suma.pi)
    println(Suma.elevarAlCuadrado(num = 2))
    println(Suma.historialSumas)
}