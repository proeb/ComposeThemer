plugins {
    kotlin("jvm")
}

rootProject.extra.apply {
    set("PUBLISH_GROUP_ID", Configuration.artifactGroup)
    set("PUBLISH_ARTIFACT_ID", "composethemer-processor")
}

apply(from ="${rootDir}/scripts/publish-module.gradle")

dependencies {
    implementation(project(":composethemer-core"))

    implementation(libs.ksp)
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet.ksp)

    testImplementation(libs.junit)
}