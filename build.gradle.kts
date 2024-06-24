plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.7")
    // https://mvnrepository.com/artifact/org.ow2.asm/asm-tree
    implementation("org.ow2.asm:asm-tree:9.7")
    implementation("org.ow2.asm:asm-util:9.7")
    implementation("org.ow2.asm:asm-analysis:9.7")

    // https://mvnrepository.com/artifact/org.jgrapht/jgrapht-core
    implementation("org.jgrapht:jgrapht-core:1.5.2")

    // https://mvnrepository.com/artifact/org.jgrapht/jgrapht-io
    implementation("org.jgrapht:jgrapht-io:1.5.2")


    // https://mvnrepository.com/artifact/org.thymeleaf/thymeleaf
    implementation("org.thymeleaf:thymeleaf:3.1.2.RELEASE")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-nop
    implementation("org.slf4j:slf4j-nop:2.0.13")



    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}


tasks.shadowJar{
    archiveBaseName.set("agent")
    archiveClassifier.set("all")
    archiveVersion.set("")
    isEnableRelocation = true
    relocationPrefix = "coverage2edge"

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