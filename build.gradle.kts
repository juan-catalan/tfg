plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.6")
    // https://mvnrepository.com/artifact/org.ow2.asm/asm-tree
    implementation("org.ow2.asm:asm-tree:9.6")
    implementation("org.ow2.asm:asm-util:9.6")
    implementation("org.ow2.asm:asm-analysis:9.6")

    // https://mvnrepository.com/artifact/org.jgrapht/jgrapht-core
    implementation("org.jgrapht:jgrapht-core:1.5.2")



    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.jar {
    manifest.attributes["Premain-Class"] = "org.example.AddPrintConditionsAgent"
    manifest.attributes["Can-Redefine-Classes"] = true
    manifest.attributes["Can-Retransform-Classes"] = true

    archiveFileName = "agent.jar"
}


/*
tasks.jar {
    manifest.attributes["Main-Class"] = "org.example.Main"
}
*/


tasks.test {
    useJUnitPlatform()
}