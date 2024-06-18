rootProject.name = System.getProperty("user.dir").substringAfterLast(File.separator)

dependencyResolutionManagement {
    versionCatalogs {
        create("zzp") {
            from(files("gradle/catalogs.toml"))
        }
    }
}
