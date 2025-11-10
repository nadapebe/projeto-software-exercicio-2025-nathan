package br.edu.insper.exercicio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Libera preflight CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // cursos: listar e criar = autenticado; deletar = admin/permissão
                        .requestMatchers(HttpMethod.GET, "/cursos/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/cursos/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/cursos/**")
                        .hasAnyAuthority("ROLE_admin", "delete:cursos")

                        // (opcional) manter /pessoas autenticado
                        .requestMatchers("/pessoas/**").authenticated()

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                ));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(WebSecurityConfig::extractAuthorities);
        return converter;
    }

    private static List<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<GrantedAuthority> auths = new ArrayList<>();

        // permissions (Auth0 RBAC)
        JwtGrantedAuthoritiesConverter perms = new JwtGrantedAuthoritiesConverter();
        perms.setAuthoritiesClaimName("permissions");
        perms.setAuthorityPrefix(""); // sem "SCOPE_"
        auths.addAll(perms.convert(jwt));

        // roles em claim namespaced
        List<String> roles = jwt.getClaimAsStringList("https://curso-api/roles");
        if (roles != null) {
            for (String r : roles) auths.add(new SimpleGrantedAuthority("ROLE_" + r));
        }
        return auths;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5173",         // dev local
                        "https://SEU-APP.vercel.app"     // TROQUE pelo seu domínio da Vercel
                )
                .allowedMethods("GET","POST","DELETE","OPTIONS")
                .allowedHeaders("Authorization","Content-Type","Accept")
                .exposedHeaders("Location");
        // .allowCredentials(true); // só se usar cookies; com Bearer não precisa
    }
}