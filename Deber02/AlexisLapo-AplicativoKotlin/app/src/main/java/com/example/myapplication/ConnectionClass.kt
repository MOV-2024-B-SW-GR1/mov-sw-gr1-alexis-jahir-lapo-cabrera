package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ConnectionClass(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mydb.db"
        private const val DATABASE_VERSION = 1

        // Constantes para la tabla Avion
        const val TABLE_AVION = "avion"
        const val COL_AVION_ID = "id"
        const val COL_AVION_NOMBRE = "nombre"
        const val COL_AVION_FECHA = "fecha_fabricacion"
        const val COL_AVION_OPERATIVO = "estado_operativo"
        const val COL_AVION_CAPACIDAD = "capacidad_pasajeros"

        // Constantes para la tabla Parte
        const val TABLE_PARTE = "parte"
        const val COL_PARTE_ID = "id"
        const val COL_PARTE_AVION_ID = "avion_id"
        const val COL_PARTE_NOMBRE = "nombre"
        const val COL_PARTE_PRECIO = "precio_repuesto"
        const val COL_PARTE_HORAS = "horas_uso"

         @Volatile
        private var instance: ConnectionClass? = null
        private var database: SQLiteDatabase? = null

        fun getInstance(context: Context): ConnectionClass {
            return instance ?: synchronized(this) {
                instance ?: ConnectionClass(context.applicationContext).also { instance = it }
            }
        }
        fun getConnection(context: Context): SQLiteDatabase {
            if (database?.isOpen != true) {
                database = getInstance(context).writableDatabase
            }
            return database!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("ConnectionClass", "Creating tables")
        // Crear tabla Avion
        db.execSQL("""      
            CREATE TABLE $TABLE_AVION (      
                $COL_AVION_ID INTEGER PRIMARY KEY AUTOINCREMENT,      
                $COL_AVION_NOMBRE TEXT NOT NULL,      
                $COL_AVION_FECHA TEXT NOT NULL,      
                $COL_AVION_OPERATIVO INTEGER NOT NULL,      
                $COL_AVION_CAPACIDAD INTEGER NOT NULL      
            )      
        """.trimIndent())

        // Crear tabla Parte
        db.execSQL("""      
            CREATE TABLE $TABLE_PARTE (      
                $COL_PARTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,      
                $COL_PARTE_AVION_ID INTEGER NOT NULL,      
                $COL_PARTE_NOMBRE TEXT NOT NULL,      
                $COL_PARTE_PRECIO REAL NOT NULL,      
                $COL_PARTE_HORAS INTEGER NOT NULL,      
                FOREIGN KEY($COL_PARTE_AVION_ID) REFERENCES $TABLE_AVION($COL_AVION_ID)      
            )      
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("ConnectionClass", "Upgrading database from version $oldVersion to $newVersion")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PARTE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_AVION")
        onCreate(db)
    }
}