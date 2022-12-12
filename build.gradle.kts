plugins {
    java
    application
}

group = "com.cjburkey.itcs3112"
version = "1.0.0"

object MainStuff {
    const val MAIN_CLASS = "com.cjburkey.itcs3112.CJsSchedules";
}

tasks.withType<Jar> {
    manifest.attributes["Main-Class"] = MainStuff.MAIN_CLASS
}

application {
    mainClass.set(MainStuff.MAIN_CLASS)
}
