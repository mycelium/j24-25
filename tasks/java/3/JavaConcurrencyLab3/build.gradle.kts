plugins {
    id("java")
}

group = "ru.lab"
version = ""

repositories {
    mavenCentral()
}

dependencies {

    implementation(files("libs/http-server.jar"))
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

// для формирования jar-файла под библиотеку

val jarBaseName = "http-server-testing"

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