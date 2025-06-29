import org.jreleaser.model.Active

plugins {
    id("java")
    `maven-publish`
    signing
    alias(libs.plugins.jreleaser)
}

subprojects {
    group = "dev.hogoshi.tensai"
    version = properties["version"].toString()

    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jreleaser")

    repositories {
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        withSourcesJar()
        withJavadocJar()
    }

    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["java"])
                groupId = project.group.toString()
                artifactId = project.name.lowercase()
                version = project.version.toString()

                pom {
                    packaging = "jar"

                    name = project.name
                    description = "Lightweight Configuring system which allows you to use a bunch of configuration types"
                    url = "https://github.com/zhogoshi/tensai"

                    licenses {
                        license {
                            name = "MIT License"
                            url = "http://www.opensource.org/licenses/mit-license.php"
                        }
                    }

                    developers {
                        developer {
                            id = "Hogoshi"
                            name = "Vadim Kusov"
                            organization = "None"
                            email = "hogoshi@yandex.ru"
                            url = "https://hogoshi.dev"
                        }
                    }

                    scm {
                        url = "https://github.com/zhogoshi/tensai"
                        connection = "scm:git:git://github.com/zhogoshi/tensai.git"
                        developerConnection = "scm:git:ssh://github.com/zhogoshi/tensai.git"
                    }
                }
            }
        }
        repositories {
            maven {
                setUrl(layout.buildDirectory.dir("staging-deploy"))
            }
        }
    }

    jreleaser {
        gitRootSearch = true
        project {
            name = this@subprojects.name
            version = this@subprojects.version.toString()

            description = "Lightweight Configuring system which allows you to use a bunch of configuration types"
            longDescription = "Lightweight Configuring system which allows you to use a bunch of configuration types"

            versionPattern = "SEMVER"
            inceptionYear = "2025"
            author("Hogoshi")
        }
        release {
            github {
                skipRelease = true
                skipTag = true
                sign = true
                branch = "main"
                branchPush = "main"
                overwrite = true
            }
        }
        signing {
            active = Active.ALWAYS
            armored = true
            verify = true
        }
        deploy {
            maven {
                mavenCentral.create("sonatype") {
                    applyMavenCentralRules = true
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository(this@subprojects.layout.buildDirectory.dir("staging-deploy").get().toString())
                    setAuthorization("Basic")
                    retryDelay = 60
                }
            }
        }
    }
}