plugins {
    `kotlin-dsl`
}

rootProject.extra.apply {
    set("PUBLISH_GROUP_ID", Configuration.artifactGroup)
    set("PUBLISH_ARTIFACT_ID", "composethemer-generator")
}

apply(from ="${rootDir}/scripts/publish-module.gradle")

gradlePlugin {
    plugins {
        create("generateThemeTask") {
            id = "com.github.proeb.composethemer"
            implementationClass = "com.proeb.composethemer.generator.ThemerPlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())

    implementation("com.squareup:kotlinpoet-ksp:1.13.2")
    implementation("com.google.code.gson:gson:2.10.1")
}

