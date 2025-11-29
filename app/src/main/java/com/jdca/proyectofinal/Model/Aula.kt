package com.jdca.proyectofinal.Model

import java.io.Serializable

data class Aula(
    val codAula: String,
    val pabellon: String,
    val piso: String
) : Serializable