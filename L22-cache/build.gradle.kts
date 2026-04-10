dependencies {
    implementation("ch.qos.logback:logback-classic")
    implementation("org.ehcache:ehcache")
    implementation("com.zaxxer:HikariCP")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    implementation("org.postgresql:postgresql")
    implementation(project(":L18-jdbc:demo"))
    implementation(project(":L18-jdbc:homework"))
}
