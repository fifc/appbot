import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.0.BUILD-SNAPSHOT"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.3.70"
	kotlin("plugin.spring") version "1.3.70"
}

group = "com.y"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_14

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
	maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
	maven { url = uri("https://repo.eclipse.org/content/repositories/paho-snapshots") }
	//flatDir { dirs = "lib" }
}

dependencies {
	implementation("joda-time:joda-time")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("io.grpc:grpc-protobuf:+")
	implementation("io.netty:netty-all:4.1.42.Final-SNAPSHOT")
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
		jvmTarget = "13"
	}
}

tasks.wrapper{
    gradleVersion = "6.3-rc-3"
    distributionType = Wrapper.DistributionType.ALL
}
