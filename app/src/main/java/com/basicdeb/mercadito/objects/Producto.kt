package com.basicdeb.mercadito.objects

data class Producto(var nombre:String,
                    var descripcion:String,
                    var precio: Float,
                    var unidad:String,
                    var NegocioId:String,
                    var imagen:String,
                    var disponible:Boolean,
                    var id:String)