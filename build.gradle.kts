plugins {
    java
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("application")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}
javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}

dependencies {
    // 기존 Spring Boot 의존성들
    /* ---------- Spring ---------- */
    implementation("org.springframework.boot:spring-boot-starter") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    /* ---------- WebSocket 구현체 ---------- */
    // API + 구현체를 한 번에 포함하는 Tyrus 번들 (2.1.x 는 Jakarta EE 10 호환)
    runtimeOnly("org.glassfish.tyrus.bundles:tyrus-standalone-client:2.1.3")

    /* ---------- Lombok ---------- */
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    /* ---------- JSON ---------- */
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    /* ---------- 개발/테스트 ---------- */
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

}
application {
    mainClass.set("org.example.kdt_bank_client2.Main")
}

/* 컴파일·테스트 공통 옵션 */
tasks.withType<JavaCompile> { options.encoding = "UTF-8" }
tasks.withType<Test> { useJUnitPlatform() }
