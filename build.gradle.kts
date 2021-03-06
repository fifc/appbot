import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val gradle_version = "6.7"

buildscript {
    extra.apply {
        set("kotlinVersion", "1.4.20-RC")
    }

    repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
	maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
	maven { url = uri("https://repo.eclipse.org/content/repositories/paho-snapshots") }
	//flatDir { dirs = "lib" }
    }
}

plugins {
	id("org.springframework.boot") version "2.4.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "${property("kotlinVersion")}"
	kotlin("plugin.spring") version "${property("kotlinVersion")}"
}

group = "com.y"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_15

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
	maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
	maven { url = uri("https://repo.eclipse.org/content/repositories/paho-snapshots") }
	//flatDir { dirs = "lib" }
}

dependencies {
	//implementation("joda-time:joda-time")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("io.grpc:grpc-protobuf:+")
	implementation("io.netty:netty-all:4.1.53.Final-SNAPSHOT")
	//implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:+")
	//implementation("org.eclipse.paho:org.eclipse.paho.mqttv5.client:1.2.2-SNAPSHOT")
	implementation("org.eclipse.paho:org.eclipse.paho.mqttv5.client:+")
	implementation("org.osgi:org.osgi.util.promise:+")
	implementation("org.osgi:org.osgi.util.function:1.1.0")
	implementation("org.osgi:org.osgi.util.pushstream:1.0.0")
	implementation("com.squareup.okhttp3:okhttp")

	//classpath("org.jetbrains.kotlin:kotlin-allopen")
	//classpath("org.jetbrains.kotlin:kotlin-gradle-plugin")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("junit:junit")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "15"
	}
}

tasks.wrapper{
    gradleVersion = gradle_version
    distributionType = Wrapper.DistributionType.ALL
}
