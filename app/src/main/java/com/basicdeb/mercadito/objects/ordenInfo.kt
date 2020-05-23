package com.basicdeb.mercadito.objects

data class ordenInfo(
    val direccion1: String? = "",
    val direccion2: String? = "",
    val direccion3: String? = "",
    val telefono: String? = "",
    val usuario: String? = "",
    val coordenadas: String? = "",
    val total: Double? = 0.0)