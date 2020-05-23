package com.basicdeb.mercadito.objects

data class Ordenes(
    val ordenInfo: ordenInfo,
    val prodListL: MutableList<ordenProd>
)