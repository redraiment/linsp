rootProject.name = "linsp"

dependencyResolutionManagement {
    versionCatalogs {
        create("zzp") {
            from(files("gradle/catalogs.toml"))
        }
    }
}
