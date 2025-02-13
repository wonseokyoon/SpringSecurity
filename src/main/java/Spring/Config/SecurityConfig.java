package Spring.Config;

import Spring.Jwt.CustomLogoutFilter;
import Spring.Jwt.JWTFilter;
import Spring.Jwt.JWTUtil;
import Spring.Jwt.LoginFilter;
import Spring.Repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final RefreshRepository refreshRepository;
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, CustomAccessDeniedHandler customAccessDeniedHandler, RefreshRepository refreshRepository) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil=jwtUtil;
        this.refreshRepository = refreshRepository;
        this.customAccessDeniedHandler=new CustomAccessDeniedHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors((cors) ->cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));

        //csrf disable
        http
                .csrf((auth) -> auth.disable());
        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());
        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());
        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/join","/joinAdmin","/reissue").permitAll()    // 권한 허용
                        .requestMatchers("/admin").hasRole("ADMIN") // admin만 접근 가능
//                                .requestMatchers("/profile").authenticated());
                        .anyRequest().authenticated()); // 로그인하고 접근
        http
                .addFilterBefore(new JWTFilter(jwtUtil,refreshRepository), LoginFilter.class);
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,refreshRepository), UsernamePasswordAuthenticationFilter.class);
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        //세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 접근 거부 처리
        http
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(customAccessDeniedHandler));
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

}
