plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.google.code.gson:gson:2.10.1")

    // for logging
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("ch.qos.logback:logback-classic:1.5.15")

    implementation("org.xerial:sqlite-jdbc:3.47.0.0")

    implementation("org.bouncycastle:bcprov-jdk18on:1.80")
}

tasks.test {
    useJUnitPlatform()
}