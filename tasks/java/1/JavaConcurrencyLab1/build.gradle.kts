plugins {
    id("java")
}

group = "ru.lab.json_parser"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

// для формирования jar-файла под библиотеку

val jarBaseName = "json_parser"

tasks.withType<Jar> {
    archiveBaseName.set(jarBaseName)
}

//tasks.withType<JavaCompile> {
//    options.compilerArgs.add("-parameters")
//}

task("fatJar", type = Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Implementation-Title"] = "Gradle Jar File"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}