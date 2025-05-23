package com.relatosred.RedSocial.config;

import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.servicios.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http    // Desactivamos CSRF (el front maneja las peticiones vía JS).
                .csrf(csrf -> csrf.disable())

                // Autorización para las diferentes rutas.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**",
                                "/avatares/**",
                                "/registro",
                                "/usuario/crear",
                                "/categorias/subcategorias",
                                "/texto/*/etiquetas",
                                "/texto/*/comentarios",
                                "/usuario/*/textos",
                                "/usuario/*/mostrar",
                                "/contactos",
                                "/textos" // filtrarTextos().
                        ).permitAll() // obtenerTextoPorId().
                        .requestMatchers(HttpMethod.GET, "/texto/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/index", "/index.html").permitAll()
                        .requestMatchers("/admin/**").hasAuthority(Usuario.RolUsuario.ADMIN.name())
                        .requestMatchers("/usuario/**").hasAnyAuthority(
                                Usuario.RolUsuario.USUARIO.name(),
                                Usuario.RolUsuario.ADMIN.name())
                        .requestMatchers("/usuario/me").hasAnyAuthority(
                                "USUARIO", "ADMIN", "MODERADOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/index")
                        .loginProcessingUrl("/index")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .findFirst()
                    .orElse("USUARIO");

            if (role.equals("ADMIN")) {
                response.sendRedirect("/admin");
            } else {
                response.sendRedirect("/index");
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}