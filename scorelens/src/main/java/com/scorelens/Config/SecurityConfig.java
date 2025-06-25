package com.scorelens.Config;

import com.google.api.Http;
import com.scorelens.Enums.StaffRole;
import com.scorelens.Enums.UserType;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.spec.SecretKeySpec;
import java.beans.Encoder;
import java.sql.Array;
import java.util.Arrays;

@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity(prePostEnabled = true)
@CrossOrigin(origins = "http://localhost:5173")
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/v*/auth/login", "/v*/auth/introspect", "/v*/auth/register", "/v*/auth/logout", "/v*/auth/refresh",
//            "/v*/",
            "/v*/ping"


    };
    private final String[] CUSTOMER_ENDPOINTS = {
            "/customers/**"
    };
    private final String[] ADMIN_ENDPOINTS = {
            "/customers/all",
            "/staffs",
    };
    private final String[] PERMISSION_ENDPOINTS = {
            "/permissions/"
    };
    private final String[] ROLE_ENDPOINTS = {
            "/roles/"
    };

    @Autowired
    @Lazy
    private CustomJwtDecoder customJwtDecoder;

    @Autowired
    private CookieJwtAuthenticationFilter cookieJwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
//                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
//                .requestMatchers(PERMISSION_ENDPOINTS).hasRole("ADMIN")
//                .requestMatchers(ROLE_ENDPOINTS).hasRole("ADMIN")
//                .requestMatchers(HttpMethod.PUT, "v*/teams/*").permitAll()
//                .requestMatchers(HttpMethod.GET, "/v*/modes").permitAll()
//                .requestMatchers(HttpMethod.GET, "/v*/modes/*").permitAll()
//                .requestMatchers(HttpMethod.GET, "/v*/tables/*").permitAll()
//                .requestMatchers(HttpMethod.POST, "/v*/billiardmatches").permitAll()
//
//                .anyRequest().authenticated());

                .anyRequest().permitAll());

        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer ->
                        jwtConfigurer.decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());

        // Thêm cookie JWT filter trước OAuth2 resource server
        http.addFilterBefore(cookieJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""); //đã set pattern bên phía AuthenticationService

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }

    //giải mã JWT -> ĐÃ DÙNG CUSTOMJWTDECODER
//    @Bean
//    JwtDecoder jwtDecoder(){
//        SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
//        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
//                .macAlgorithm(MacAlgorithm.HS512)
//                .build();
//    }

    //CORS handling
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://localhost:5173",
                "https://score-lens.vercel.app"
        ));
        //corsConfiguration.addAllowedOriginPattern("*"); // mở rộng cho tất cả các port localhost
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource url = new UrlBasedCorsConfigurationSource();
        url.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(url);
    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("http://localhost:3000")
//                        .allowedMethods("*")
//                        .allowCredentials(true);
//            }
//        };
//    }

    //Encoding password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
