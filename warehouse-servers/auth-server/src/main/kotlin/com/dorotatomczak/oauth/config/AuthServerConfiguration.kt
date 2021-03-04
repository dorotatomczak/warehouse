package com.dorotatomczak.oauth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import javax.sql.DataSource

@Configuration
class AuthServerConfiguration(
    val authenticationConfiguration: AuthenticationConfiguration,
    val bcryptPasswordEncoder: PasswordEncoder,
    val userDetailsService: UserDetailsService,
    val dataSource: DataSource
) : AuthorizationServerConfigurerAdapter() {

    override fun configure(security: AuthorizationServerSecurityConfigurer?) {
        security
            ?.checkTokenAccess("permitAll()")
            ?.passwordEncoder(bcryptPasswordEncoder)
    }

    override fun configure(clients: ClientDetailsServiceConfigurer?) {
        clients
            ?.jdbc(dataSource)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer?) {
        endpoints
            ?.authenticationManager(authenticationConfiguration.authenticationManager)
            ?.userDetailsService(userDetailsService)
            ?.tokenStore(tokenStore())
    }

    @Bean
    fun tokenStore(): TokenStore? = JdbcTokenStore(dataSource)
}
