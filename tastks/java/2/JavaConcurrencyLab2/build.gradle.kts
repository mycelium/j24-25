plugins {
    id("java")
}

group = "ru.lab"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    // for logging
    implementation("org.slf4j:slf4j-log4j12:2.0.7")
    implementation("log4j:log4j:1.2.17")

    implementation(files("libs/json_parser.jar"))

    // for testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

// для формирования jar-файла под библиотеку

val jarBaseName = "http-server"

tasks.withType<Jar> {
    archiveBaseName.set(jarBaseName)
}

task("fatJar", type = Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Implementation-Title"] = "Gradle Jar File"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}