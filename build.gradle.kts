import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-rc2"
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo-momi.gtemc.cn/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    implementation("net.momirealms:craft-engine-core:${rootProject.properties["craftengine_version"]}")
    implementation("net.momirealms:craft-engine-bukkit:${rootProject.properties["craftengine_version"]}")
    implementation("net.momirealms:craft-engine-nms-helper-mojmap:${rootProject.properties["nms_helper_version"]}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
    dependsOn(tasks.clean)
}

paper {
    main = "cn.gtemc.craftengine.CustomMaterial"
    bootstrapper = "cn.gtemc.craftengine.CustomMaterialBootstrap"
    version = rootProject.properties["project_version"] as String
    name = "craft-engine-material"
    apiVersion = "1.20"
    author = "jhqwqmc"
    website = "https://github.com/jhqwqmc"
    foliaSupported = true
    serverDependencies {
        register("CraftEngine") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

artifacts {
    archives(tasks.shadowJar)
}

tasks {
    shadowJar {
        archiveFileName = "${rootProject.name}-${rootProject.properties["project_version"]}.jar"
        destinationDirectory.set(file("$rootDir/target"))
    }
}