/*
 *    Copyright 2020 Taktik SA
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun
import java.text.SimpleDateFormat
import java.util.Date

val repoUsername: String by project
val repoPassword: String by project
val mavenReleasesRepository: String by project

plugins {
    kotlin("jvm") version "1.4.21"
    kotlin("kapt") version "1.4.21"
    `maven-publish`
}

buildscript {
    repositories {
      mavenCentral()
      gradlePluginPortal()
      jcenter()
      maven { url = uri("https://maven.taktik.be/content/groups/public") }
      maven { url = uri("https://repo.spring.io/plugins-release") }
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
        classpath("org.jetbrains.kotlin:kotlin-allopen:1.4.21")
        classpath("com.taktik.gradle:gradle-plugin-docker-java:2.0.6")
        classpath("com.taktik.gradle:gradle-plugin-git-version:1.0.13")
    }
}

apply(plugin = "git-version")

val gitVersion: String? by project
group = "org.taktik.icure"
version = gitVersion ?: "0.0.1-SNAPSHOT"

apply(plugin = "kotlin-spring")
apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management")
apply(plugin = "docker-java")

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://maven.taktik.be/content/groups/public") }
    maven { url = uri("https://www.e-contract.be/maven2/") }
    maven { url = uri("https://repo.ehealth.fgov.be/artifactory/maven2/") }
}

apply(plugin = "kotlin")
apply(plugin = "maven-publish")

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.isFork = true
    options.fork("memoryMaximumSize" to "4096m")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<PublishToMavenRepository> {
    doFirst {
        println("Artifact >>> ${project.group}:${project.name}:${project.version} <<< published to Maven repository")
    }
}

tasks.withType<BootJar> {
    mainClass.set("org.taktik.icure.ICureBackendApplicationKt")
    manifest {
        attributes(mapOf(
            "Built-By"        to System.getProperties()["user.name"],
            "Build-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(Date()),
            "Build-Revision"  to gitVersion,
            "Created-By"      to "Gradle ${gradle.gradleVersion}",
            "Build-Jdk"       to "${System.getProperties()["java.version"]} (${System.getProperties()["java.vendor"]} ${System.getProperties()["java.vm.version"]})",
            "Build-OS"        to "${System.getProperties()["os.name"]} ${System.getProperties()["os.arch"]} ${System.getProperties()["os.version"]}"
        ))
    }
}

tasks.withType<BootRun> {
    if ( project.hasProperty("jvmArgs") ) {
        jvmArgs = (project.getProperties()["jvmArgs"] as String).split(Regex("\\s+"))
    }
}

configurations {
    all {
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
        exclude(group = "log4j", module = "log4j")
    }
    listOf(apiElements, runtimeElements).forEach {
        it.get().outgoing.artifacts.removeIf {
            it.buildDependencies.getDependencies(null).any { it is Jar }
        }
        it.get().outgoing.artifact(tasks.withType<BootJar>().first())
    }
}

dependencies {
    api("com.github.pozo:mapstruct-kotlin:1.3.1.2")
    kapt("com.github.pozo:mapstruct-kotlin-processor:1.3.1.2")

    implementation(group = "io.projectreactor", name = "reactor-core", version = "3.4.0")
    implementation(group = "io.projectreactor", name = "reactor-tools", version = "3.4.0")
    implementation(group = "io.projectreactor.netty", name = "reactor-netty", version = "1.0.1")

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8")
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.4.2")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-reactor", version = "1.4.2")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-collections-immutable-jvm", version = "0.3")

    //Jackson
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = "2.11.3")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.11.3")
    implementation(group = "org.mapstruct", name = "mapstruct", version = "1.3.1.Final")

    //Krouch
    implementation(group = "org.taktik.couchdb", name = "krouch", version = "jack211-1.0.2-42-ga934401c85")

    implementation(group = "org.springframework.boot", name = "spring-boot-starter-mail", version = "2.4.0")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-webflux", version = "2.4.0")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-security", version = "2.4.0")

    implementation(group = "org.springframework", name = "spring-aspects", version = "5.3.1")
    implementation(group = "org.springframework", name = "spring-websocket", version = "5.3.1")
    implementation(group = "org.springframework", name = "spring-orm", version = "5.3.1")

    implementation(group = "org.springframework.session", name = "spring-session-core", version = "2.2.1.RELEASE")

    implementation(group = "org.hibernate.validator", name = "hibernate-validator", version = "6.1.5.Final")
    implementation(group = "org.hibernate.validator", name = "hibernate-validator-annotation-processor", version = "6.1.5.Final")
    implementation(group = "org.hibernate.validator", name = "hibernate-validator-cdi", version = "6.1.5.Final")

    // Logging
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
    implementation(group = "ch.qos.logback", name = "logback-access", version = "1.2.3")

    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.12")
    implementation(group = "org.slf4j", name = "jul-to-slf4j", version = "1.7.12")
    implementation(group = "org.slf4j", name = "jcl-over-slf4j", version = "1.7.12")
    implementation(group = "org.slf4j", name = "log4j-over-slf4j", version = "1.7.12")

    // Java Commons
    implementation(group = "org.taktik.commons", name = "commons-uti", version = "1.0")

    // APIs
    implementation(group = "javax.servlet", name = "javax.servlet-api", version = "3.1.0")
    implementation(group = "javax.annotation", name = "jsr250-api", version = "1.0")
    implementation(group = "javax.activation", name = "activation", version = "1.1")
    implementation(group = "javax.xml.bind", name = "jaxb-api", version = "2.3.1")
    implementation(group = "javax.el", name = "javax.el-api", version = "3.0.0")

    implementation(group = "org.glassfish.jaxb", name = "jaxb-runtime", version = "2.3.1")
    implementation(group = "org.glassfish", name = "javax.el", version = "3.0.0")
    implementation(group = "org.reflections", name = "reflections", version = "0.9.11")

    // Commons
    implementation(group = "com.google.guava", name = "guava", version = "20.0")
    implementation(group = "commons-io", name = "commons-io", version = "2.5")
    implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.5")
    implementation(group = "org.apache.commons", name = "commons-compress", version = "1.12")
    implementation(group = "org.apache.commons", name = "commons-math3", version = "3.2")
    implementation(group = "commons-beanutils", name = "commons-beanutils", version = "1.9.3")

    // Bouncy Castle
    implementation(group = "org.bouncycastle", name = "bcprov-jdk15on", version = "1.53")
    implementation(group = "org.bouncycastle", name = "bcmail-jdk15on", version = "1.53")

    //2FA
    implementation(group = "org.jboss.aerogear", name = "aerogear-otp-java", version = "1.0.0")

    // Swagger
    implementation(group = "org.springdoc", name = "springdoc-openapi-webflux-ui", version = "1.5.2")
    implementation(group = "org.springdoc", name = "springdoc-openapi-kotlin", version = "1.5.2")

    //Saxon
    implementation(group = "net.sf.saxon", name = "Saxon-HE", version = "9.6.0-6")

    //EH Validator
    implementation(group = "com.ibm.icu", name = "icu4j", version = "57.1")
    implementation(files("libs/ehvalidator-service-core-2.1.1.jar"))

    // Mustache
    implementation(group = "com.github.spullara.mustache.java", name = "compiler", version = "0.9.5")

    //Sendgrid
    implementation(group = "com.sendgrid", name = "sendgrid-java", version = "4.4.7")

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.4.2")
    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-test", version = "2.3.6.RELEASE")

}

publishing {
    publications {
        create<MavenPublication>("icure-oss") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "Taktik"
            url = uri(mavenReleasesRepository)
            credentials {
                username = repoUsername
                password = repoPassword
            }
        }
    }
}
