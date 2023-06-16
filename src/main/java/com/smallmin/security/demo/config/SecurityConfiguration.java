package com.smallmin.security.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全配置
 *
 * @author dengyumin
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 放行静态资源
                        .requestMatchers("/css/**", "/images/**", "/js/**").permitAll()
                        // 其他资源均需授权
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // 配置登录页相关配置
                        .loginPage("/login.html")
                        .loginProcessingUrl("/doLogin")
                        .usernameParameter("name")
                        .passwordParameter("pwd")
                        .successForwardUrl("/success")
                        .permitAll()
                )
                // 关闭csrf
                .csrf(csrf -> csrf.disable())
        ;
        return http.build();
    }

    /**
     * 配置密码储存
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 配置指定用户
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password("{noop}123456")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
