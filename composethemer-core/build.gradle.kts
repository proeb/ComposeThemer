plugins {
    kotlin("jvm")
}

rootProject.extra.apply {
    set("PUBLISH_GROUP_ID", Configuration.artifactGroup)
    set("PUBLISH_ARTIFACT_ID", "composethemer-core")
}

apply(from ="${rootDir}/scripts/publish-module.gradle")