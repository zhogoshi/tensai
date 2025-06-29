plugins {
    java
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    compileOnly(libs.jetbrains.annotations)
}

tasks.jar {
    from(sourceSets.main.get().output)
}