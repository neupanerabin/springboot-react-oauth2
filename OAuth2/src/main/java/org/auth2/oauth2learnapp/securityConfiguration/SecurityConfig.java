package org.auth2.oauth2learnapp.securityConfiguration;

/*
 * @author : rabin
 */

import jakarta.servlet.http.HttpServletResponse;
import org.auth2.oauth2learnapp.serviceImpl.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomOidcUserService customOidcUserService;

    public SecurityConfig(CustomOidcUserService customOidcUserService) {
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configure CORS to allow requests from the frontend[react](http://localhost:5173)
                .cors(cors -> cors.configurationSource(req -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.addAllowedOrigin("http://localhost:5173");
                    configuration.setAllowCredentials(true);
                    return configuration;
                }))
                // Disable CSRF protection since the app uses OAuth2 and likely no form-based submissions
                .csrf(AbstractHttpConfigurer::disable)
                // if login then passed to frontend or go to the login page
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/", "/login").permitAll() // allow permit to all
                        .anyRequest().authenticated())
                // Configure OAuth2 login with custom user service
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOidcUserService))
                        .defaultSuccessUrl("http://localhost:5173/", true))

                // Logout handling
                .logout(logout -> logout
                        .logoutSuccessUrl("http://localhost:5173/login")    // redirect to frontend login page after logout
                        .invalidateHttpSession(true)    //Invalidate the session
                        .clearAuthentication(true)  // clear authentication context
                        .deleteCookies("JESSIONID") // remove session cookie
                        .permitAll())   // Allow logout endpoint for all users

                // Exception handling (unauthenticated or forbidden cases)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);    // When not logged in → return 401 Unauthorized
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN);    // When logged in but forbidden (e.g. missing ADMIN role) → return 403
                        }));
        return http.build();

    }
}
