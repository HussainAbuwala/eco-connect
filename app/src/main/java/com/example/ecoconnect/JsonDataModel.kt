package com.example.ecoconnect

// Data Model For Getting Packaging Information
data class Product(val product: Packaging)

data class Packaging(val packagings: List<PackList>,
                     val nova_group: String,
                     val ecoscore_grade: String,
                     val nutriscore_grade: String)

data class PackList(val material: String,
                    val recycling: String,
                    val shape: String)