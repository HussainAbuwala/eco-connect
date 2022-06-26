package com.example.ecoconnect


enum class Shape(val value: String) {
    CONTAINER("container"),
    CAN("can"),
    BOTTLE("bottle"),
    CAP("cap"),
    LID("lid"),
    CARTON("carton"),
    BOX("box"),
    BAG("bag"),
    JAR("jar"),
    CLAMSHELL("clamshell"),
    POUCH("pouch"),
    GABLETOP("gable top")
}

enum class Material(val value: String) {
    METAL("metal"),
    ALUMINIUM("aluminium"),
    GLASS("glass"),
    PLASTIC("plastic"),
    CARDBOARD("cardboard"),
    PP("Polypropylene"),
    PET("Polyethylene terephthalate")
}

enum class Category(val value: String) {
    ALCOHOL("alcohol"),
    WINE("wine"),
    BEER("beer"),
    SPIRIT("spirit"),
    BEVERAGE("beverage"),
    LIQUOR("liquor")
}