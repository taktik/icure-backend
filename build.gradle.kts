/*
 *    Copyright 2020 Taktik SA
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun
import java.text.SimpleDateFormat
import java.util.*

val ktlint by configurations.creating

val repoUsername: String by project
val repoPassword: String by project
val mavenReleasesRepository: String by project
val kmapVersion = "0.1.23-0a1725c2ab"

plugins {
    kotlin("jvm") version "1.6.21"
    id("org.sonarqube") version "3.3"
    id("com.google.devtools.ksp") version "1.6.21-1.0.5"
    `maven-publish`
}

sonarqube {
    properties {
        property("sonar.projectKey", "icure-io_icure-kotlin-sdk")
        property("sonar.organization", "icure-io")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

buildscript {
    repositories {
      mavenCentral()
      gradlePluginPortal()
      maven { url = uri("https://maven.taktik.be/content/groups/public") }
      maven { url = uri("https://repo.spring.io/plugins-release") }
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.5.13")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("org.jetbrains.kotlin:kotlin-allopen:1.6.10")
        classpath("com.taktik.gradle:gradle-plugin-docker-java:2.1.0")
        classpath("com.taktik.gradle:gradle-plugin-git-version:2.0.1")
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

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
}

dependencies {
    implementation(group = "io.icure", name = "kmap", version = kmapVersion)
    ksp(group = "io.icure", name = "kmap", version = kmapVersion)

    implementation(group = "io.projectreactor", name = "reactor-core", version = "3.4.17")
    implementation(group = "io.projectreactor", name = "reactor-tools", version = "3.4.17")
    implementation(group = "io.projectreactor.netty", name = "reactor-netty", version = "1.0.18")

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8", version = "1.6.21")
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = "1.6.21")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.6.1")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core-jvm", version = "1.6.1")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-reactive", version = "1.6.1")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-reactor", version = "1.6.1")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-collections-immutable-jvm", version = "0.3.5")

    //Jackson
	implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = "2.12.6")
	implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.12.6")
    implementation(group = "com.fasterxml.jackson.datatype", name="jackson-datatype-jsr310", version = "2.12.6")
    implementation(group = "org.mapstruct", name = "mapstruct", version = "1.3.1.Final")

    //Krouch
    implementation(group = "org.taktik.couchdb", name = "krouch", version = "jack211-1.0.2-96-g9eff2f70a0")
    implementation(group = "io.icure", name = "async-jackson-http-client", version = "0.1.12-dd2039b194")
    implementation(group = "io.icure", name = "mapper-processor", version = "0.1.1-32d45af2a6")

    implementation(group = "org.springframework.boot", name = "spring-boot-starter-mail", version = "2.5.5")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-webflux", version = "2.5.13")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-security", version = "2.5.13")
	implementation(group = "org.springframework.boot", name= "spring-boot-starter-cache", version = "2.5.13")


	implementation(group = "org.springframework", name = "spring-aspects", version = "5.3.10")
    implementation(group = "org.springframework", name = "spring-websocket", version = "5.3.10")

    implementation(group = "org.springframework.session", name = "spring-session-core", version = "2.5.2")

    implementation(group = "org.hibernate.validator", name = "hibernate-validator", version = "6.1.5.Final")
    implementation(group = "org.hibernate.validator", name = "hibernate-validator-annotation-processor", version = "6.1.5.Final")
    implementation(group = "org.hibernate.validator", name = "hibernate-validator-cdi", version = "6.1.5.Final")

    implementation(group = "com.github.ben-manes.caffeine", name = "caffeine", version = "3.0.6")

    // Logging
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.11")
    implementation(group = "ch.qos.logback", name = "logback-access", version = "1.2.11")

    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.36")
    implementation(group = "org.slf4j", name = "jul-to-slf4j", version = "1.7.32")
    implementation(group = "org.slf4j", name = "jcl-over-slf4j", version = "1.7.32")
    implementation(group = "org.slf4j", name = "log4j-over-slf4j", version = "1.7.36")

    // Java Commons
    implementation(group = "org.taktik.commons", name = "commons-uti", version = "1.0")

    // APIs
    implementation(group = "javax.servlet", name = "javax.servlet-api", version = "3.1.0")
    implementation(group = "javax.annotation", name = "jsr250-api", version = "1.0")
    implementation(group = "javax.activation", name = "activation", version = "1.1.1")
    implementation(group = "javax.xml.bind", name = "jaxb-api", version = "2.3.1")
    implementation(group = "javax.el", name = "javax.el-api", version = "3.0.0")

    implementation(group = "org.glassfish.jaxb", name = "jaxb-runtime", version = "2.3.1")
    implementation(group = "org.glassfish", name = "javax.el", version = "3.0.0")
    implementation(group = "org.reflections", name = "reflections", version = "0.9.12")

    // Commons
    implementation(group = "com.google.guava", name = "guava", version = "31.1-jre")
    implementation(group = "commons-io", name = "commons-io", version = "2.11.0")
    implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.12.0")
    implementation(group = "org.apache.commons", name = "commons-compress", version = "1.21")
    implementation(group = "org.apache.commons", name = "commons-math3", version = "3.6.1")
    implementation(group = "commons-beanutils", name = "commons-beanutils", version = "1.9.4")

    // Bouncy Castle
    implementation(group = "org.bouncycastle", name = "bcprov-jdk15on", version = "1.70")
    implementation(group = "org.bouncycastle", name = "bcmail-jdk15on", version = "1.70")

    //2FA
    implementation(group = "org.jboss.aerogear", name = "aerogear-otp-java", version = "1.0.0")

    // Swagger
    implementation(group = "org.springdoc", name = "springdoc-openapi-webflux-ui", version = "1.6.7")
    implementation(group = "org.springdoc", name = "springdoc-openapi-kotlin", version = "1.5.13")

    //Saxon
    implementation(group = "net.sf.saxon", name = "Saxon-HE", version = "9.6.0-6")

    //EH Validator
    implementation(group = "com.ibm.icu", name = "icu4j", version = "57.1")
    implementation(files("libs/ehvalidator-service-core-2.1.1.jar"))

    // Mustache
    implementation(group = "com.github.spullara.mustache.java", name = "compiler", version = "0.9.10")

    //Sendgrid
    implementation(group = "com.sendgrid", name = "sendgrid-java", version = "4.4.7")

    ktlint("com.pinterest:ktlint:0.45.2") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
    // additional 3rd party ruleset(s) can be specified here
    // just add them to the classpath (e.g. ktlint 'groupId:artifactId:version') and
    // ktlint will pick them up

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.4.2")
    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-test", version = "2.5.13")

}

val outputDir = "${project.buildDir}/reports/ktlint/"
val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

val ktlintCheck by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    outputs.dir(outputDir)
    description = "Check Kotlin code style."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("src/**/*.kt")
}

val ktlintFiles by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    val split = if (project.hasProperty("inputFiles")) project.property("inputFiles")?.toString()?.split(',') ?: emptyList() else emptyList()

    description = "Check Kotlin code style."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args = split
}

val ktlintFormat by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("-F", "src/**/*.kt")
}

val setupKtlintPreCommitHook by tasks.creating(Exec::class) {
    exec {
        workingDir = File("${rootProject.projectDir}")
        commandLine = listOf("sh", "-c", "git config --local include.path ./.gitconfig > /dev/null 2>&1 || true")
    }
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
