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

    implementation("com.h2database:h2:2.2.224")

    implementation("org.bouncycastle:bcprov-jdk18on:1.80")

    implementation("org.springframework:spring-core:6.2.5")
    implementation("org.springframework:spring-context:6.2.5")
    implementation("org.springframework:spring-web:6.2.5")
    implementation("org.springframework:spring-webmvc:6.2.5")
    implementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.0-M19")
    implementation("org.apache.tomcat.embed:tomcat-embed-jasper:11.0.0-M19")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.0")
    implementation("org.hibernate.orm:hibernate-core:6.6.13.Final")
    implementation("org.springframework.data:spring-data-jdbc:3.4.4")
    implementation("org.springframework.data:spring-data-jpa:3.4.4")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.6.13.Final")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0-M2")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0-M2")


}

tasks.test {
    useJUnitPlatform()
}