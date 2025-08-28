plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.jgit)
}

val namespace = "ir.amirroid.clipshare.git"

gradlePlugin {
    plugins {
        register("gitVersioning") {
            id = "${namespace}.git-versioning"
            implementationClass = "${namespace}.GitVersioningPlugin"
        }
    }
}