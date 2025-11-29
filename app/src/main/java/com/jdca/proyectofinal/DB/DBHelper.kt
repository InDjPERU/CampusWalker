package com.jdca.proyectofinal.DB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.jdca.proyectofinal.Model.Aula

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, "ubicacion_universidad.db", null, 5) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE usuarios (
                correo TEXT PRIMARY KEY,
                contrasena TEXT
            );
            """.trimIndent()
        )

        // ✅ Aquí guardas AULAS y también LABS (como ubicaciones)
        db.execSQL(
            """
            CREATE TABLE aulas (
                codigo TEXT PRIMARY KEY,
                pabellon TEXT,
                piso TEXT
            );
            """.trimIndent()
        )

        insertarDatosIniciales(db)
    }
    private fun insertarDatosIniciales(db: SQLiteDatabase) {
        // Aulas:
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('AULI', 'A', 'Semisótano');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('AUFOG', 'A', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('BS1', 'B', 'Semisótano');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('CI', 'D', '1, 8 y 9');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('ESGER', 'B', '7');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A101', 'A', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A102', 'A', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A103', 'A', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A104', 'A', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A105', 'A', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A106', 'A', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A107', 'A', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A108', 'A', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A201', 'A', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A202', 'A', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A203', 'A', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A204', 'A', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A205', 'A', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A206', 'A', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A301', 'A', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A302', 'A', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A303', 'A', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A304', 'A', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A501', 'A', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A502', 'A', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A601', 'A', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('A602', 'A', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B101', 'B', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B102', 'B', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B103', 'B', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B104', 'B', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B105', 'B', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B201', 'B', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B202', 'B', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B203', 'B', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B204', 'B', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B205', 'B', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B301', 'B', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B302', 'B', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B303', 'B', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B304', 'B', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B305', 'B', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B306', 'B', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B401', 'B', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B402', 'B', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B403', 'B', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B404', 'B', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B501', 'B', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B502', 'B', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B503', 'B', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B504', 'B', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B505', 'B', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B506', 'B', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B601', 'B', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B602', 'B', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B603', 'B', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B604', 'B', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B605', 'B', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B606', 'B', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B607', 'B', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B608', 'B', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B701', 'B', '7');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('B702', 'B', '7');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D201', 'D', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D401', 'D', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D402', 'D', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D403', 'D', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D404', 'D', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D405', 'D', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D501', 'D', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D502', 'D', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D503', 'D', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D504', 'D', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D505', 'D', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D601', 'D', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D602', 'D', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D603', 'D', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D604', 'D', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D605', 'D', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D701', 'D', '7');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D702', 'D', '7');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D703', 'D', '7');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D704', 'D', '7');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('D705', 'D', '7');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LAB3D', 'B', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LACOM', 'D', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LAFOT', 'B', 'Semisótano');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LAUTO', 'A', 'Semisótano');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LAZAM', 'B', 'Semisótano');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM1', 'A', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM2', 'A', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM3', 'A', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM4', 'A', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM6', 'B', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM7', 'B', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM8', 'B', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM9', 'B', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM10', 'B', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM11', 'D', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM12', 'D', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM13', 'D', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOM14', 'D', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCON', 'C', 'Semisótano');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOS', 'D', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LCOS2', 'D', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LELEC', 'A', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LESFU', 'D', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LFIS', 'A', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LHIDR', 'C', 'Semisótano');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LHOS', 'D', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LMUL1', 'B', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LMUL2', 'D', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LQUI', 'A', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LRED1', 'A', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('LSUE', 'C', 'Semisótano');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TAEDI', 'B', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TAUDI', 'B', '1');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TARQ1', 'A', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TARQ2', 'A', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TARQ3', 'B', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TARQ4', 'B', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TARQ5', 'A', '4');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TARQ6', 'A', '5');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TARQ7', 'A', '6');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TARQ8', 'A', '7');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TCOBA', 'A', '7');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TEFI', 'D', '2');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('THAB', 'D', '3');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('THKEEPI', 'B', 'Semisótano');")
        db.execSQL("INSERT INTO aulas (codigo, pabellon, piso) VALUES ('TMCON', 'B', 'Semisótano');")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS aulas")
        onCreate(db)
    }

    fun buscarAulaPorCodigo(codigo: String): Aula? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM aulas WHERE codigo = ?",
            arrayOf(codigo)
        )

        return if (cursor.moveToFirst()) {
            Aula(
                codAula = cursor.getString(cursor.getColumnIndexOrThrow("codigo")),
                pabellon = cursor.getString(cursor.getColumnIndexOrThrow("pabellon")),
                piso = cursor.getString(cursor.getColumnIndexOrThrow("piso"))
            )
        } else null
            .also { cursor.close() }
    }
}


