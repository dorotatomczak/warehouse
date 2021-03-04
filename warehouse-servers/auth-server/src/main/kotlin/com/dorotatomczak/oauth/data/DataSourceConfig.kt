package com.dorotatomczak.oauth.data

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    val env: Environment
) {
    @Bean
    fun dataSource(): DataSource =
        DriverManagerDataSource().apply {
            setDriverClassName(env.getProperty("spring.datasource.driverClassName", ""))
            url = env.getProperty("spring.datasource.url")
            username = env.getProperty("spring.datasource.username")
            password = env.getProperty("spring.datasource.password", "")
        }
}
