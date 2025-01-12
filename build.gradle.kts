import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.openapi.generator") version "7.5.0"
    id("org.sonarqube") version "5.1.0.4872"
    id("org.springframework.boot") version "3.3.7"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
    kotlin("plugin.jpa") version "1.9.0"
    jacoco
}

group = "com.onecosys"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_19

val testcontainersVersion = "1.20.4"
val jwtVersion = "0.12.6"
val openApiWebMvc = "2.6.0"
val jacksonModule = "2.18.2"
val postgresVersion = "42.7.4"
val h2databaseVersion = "2.3.232"
val jupiterVersion = "5.11.3"
val assertjVersion = "3.27.1"
val mockkVersion = "1.13.10"
val kotestVersion = "5.8.1"

repositories {
    mavenCentral()
}

sonarqube {
    properties {
        property("sonar.projectKey", System.getenv("SONAR_PROJECT_KEY"))
        property("sonar.organization", System.getenv("SONAR_ORG"))
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

jacoco {
    toolVersion = "0.8.8"
    reportsDirectory.set(layout.buildDirectory.dir("customJacocoReportDir"))
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonModule")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-security")
    runtimeOnly("org.postgresql:postgresql:$postgresVersion")
    runtimeOnly("com.h2database:h2:$h2databaseVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jwtVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.modulith:spring-modulith-starter-core")


    // Swagger / Open API
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiWebMvc")

    // Unit Testing stuff
    testImplementation("org.junit.jupiter:junit-jupiter:$jupiterVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Integration Testing
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:1.3.1")
    }
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$rootDir/src/main/resources/static/api/open-api.yml")
    configFile.set("$rootDir/src/main/resources/api-config.json")
    apiPackage.set("com.onecosys.getthingsdone.api")
    modelPackage.set("com.onecosys.getthingsdone.dto")
    configOptions.set(mapOf("useSpringBoot3" to "true"))
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("build/generate-resources/main/src/main/kotlin")
    }
}

tasks.withType<KotlinCompile> {
    dependsOn("openApiGenerate")
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "19"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.5".toBigDecimal()
            }
        }

        rule {
            isEnabled = false
            element = "CLASS"
            includes = listOf("org.gradle.*")

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "0.3".toBigDecimal()
            }
        }
    }
}

tasks.withType<JacocoReport> {
    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(
                "com/onecosys/getthingsdone/GetThingsDoneApplication.kt"
            )
        }
    )
}
