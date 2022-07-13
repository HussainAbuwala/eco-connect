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
    GABLETOP("gable top"),
    JUG("jug"),
    BRICK("brick"),
    BRIK("brik")
}

enum class Material(val value: String) {
    METAL("metal"),
    ALUMINIUM("aluminium"),
    TIN("tin"),
    GLASS("glass"),
    PLASTIC("plastic"),
    CARDBOARD("cardboard"),
    PP("polypropylene"),
    PET("polyethylene terephthalate"),
    PAPER("paper"),
    TETRA("tetra")
}

enum class Category(val value: String) {
    ALCOHOL("alcohol"),
    WINE("wine"),
    BEER("beer"),
    SPIRIT("spirit"),
    BEVERAGE("beverage"),
    LIQUOR("liquor")
}