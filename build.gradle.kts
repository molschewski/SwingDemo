import org.springframework.boot.gradle.tasks.run.BootRun

 plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://drewnoakes/com/maven2")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("com.drewnoakes:metadata-extractor:2.19.0")
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

//tasks.withType<JavaCompile> {
//	options.compilerArgs.add("-Xlint:unchecked")
//}

// debug
//tasks.withType<BootRun> {
//	jvmArgs = listOf("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005")
//}


tasks.jar {
	manifest.attributes["Main-Class"] = "com.example.swingdemo.SwingdemoApplication"
}