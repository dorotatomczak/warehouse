package com.dorotatomczak.oauth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds.AUTHENTICATION_MANAGER
import org.springframework.security.config.BeanIds.USER_DETAILS_SERVICE
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
import javax.sql.DataSource

@Configuration
@EnableOAuth2Client
class WebSecurityConfiguration(
    val bcryptPasswordEncoder: PasswordEncoder,
    val dataSource: DataSource,
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth
            ?.jdbcAuthentication()
            ?.passwordEncoder(bcryptPasswordEncoder)
            ?.dataSource(dataSource)
            ?.apply {
                userDetailsService.setEnableGroups(true)
                userDetailsService.setEnableAuthorities(false)
            }
    }

    override fun configure(http: HttpSecurity?) {
        http
            ?.authorizeRequests()
            ?.antMatchers("/register**", "/h2-console**", "/google/verify")
            ?.permitAll()
            ?.and()
            ?.headers()?.addHeaderWriter(XFrameOptionsHeaderWriter(SAMEORIGIN))
            ?.and()
            ?.csrf()?.disable()
    }

    @Bean(USER_DETAILS_SERVICE)
    override fun userDetailsServiceBean(): UserDetailsService = super.userDetailsServiceBean()

    @Bean(AUTHENTICATION_MANAGER)
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()
}
