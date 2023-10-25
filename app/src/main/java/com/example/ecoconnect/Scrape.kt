package com.example.ecoconnect

import org.jsoup.Jsoup
import java.io.File
import java.net.URI

fun main(){

    val BEER_STORE_URL = "https://www.thebeerstore.ca/about-us/environmental-leadership/bag-it-back-odrp/"

    val doc = Jsoup.connect(BEER_STORE_URL).get()

    /*
    Shape.values().forEach {

        val matchedElements = doc.select(":matchesWholeOwnText(${it.value})")
        println("########----------------------------------#################")
        println(it.value)
        println()
        println(matchedElements)
        println()
        println("########----------------------------------#################")
        println()

    }*/
    val x = "https://www.return-it.ca/beverage/products,"
    println(x.trim().split(",").toMutableList()[1].isEmpty())


}