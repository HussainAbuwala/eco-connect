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
    METAL("Metal"),
    ALUMINIUM("Aluminium"),
    GLASS("Glass"),
    PLASTIC("Plastic"),
    CARDBOARD("Cardboard"),
    PP("Polypropylene"),
    PET("Polyethylene terephthalate"),
    PAPER("Paper")
}

enum class Category(val value: String) {
    ALCOHOL("alcohol"),
    WINE("wine"),
    BEER("beer"),
    SPIRIT("spirit"),
    BEVERAGE("beverage"),
    LIQUOR("liquor")
}