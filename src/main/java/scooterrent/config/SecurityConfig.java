package scooterrent.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // =============================================
                // 1. 公开 — 无需登录
                // =============================================
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/scooters", "/api/scooters/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                 "/webjars/**", "/swagger-resources/**",
                                 "/images/**", "/static/**").permitAll()

                // =============================================
                // 2. 管理员专属
                // =============================================
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/roles/**").hasRole("ADMIN")
                .requestMatchers("/api/statistics/**").hasRole("ADMIN")
                // 滑板车写操作
                .requestMatchers(HttpMethod.POST, "/api/scooters").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/scooters/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/scooters/**").hasRole("ADMIN")
                // 用户管理
                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/users/register").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                // 订单管理
                .requestMatchers(HttpMethod.GET, "/api/orders").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/orders/status/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/orders/**/status").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/orders/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/orders/date-range").hasRole("ADMIN")
                // 支付管理
                .requestMatchers(HttpMethod.GET, "/api/payments/status/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/payments/date-range").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/payments/**/status").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/payments/**/simulate").hasRole("ADMIN")
                // 反馈管理
                .requestMatchers(HttpMethod.GET, "/api/feedback").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/feedback/status/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/feedback/type/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/feedback/**/status").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/feedback/**/response").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/feedback/**").hasRole("ADMIN")

                // =============================================
                // 3. 动态：自己或管理员（替代 @PreAuthorize 的 #username 检查）
                // =============================================
                .requestMatchers(HttpMethod.GET, "/api/users/{username}").access(selfOrAdmin())
                .requestMatchers(HttpMethod.PUT, "/api/users/{username}").access(selfOrAdmin())
                .requestMatchers(HttpMethod.POST, "/api/users/{username}/recharge").access(selfOrAdmin())
                .requestMatchers(HttpMethod.GET, "/api/orders/user/{username}").access(selfOrAdmin())
                .requestMatchers(HttpMethod.GET, "/api/orders/user/{username}/date-range").access(selfOrAdmin())
                .requestMatchers(HttpMethod.GET, "/api/rentals/user/{username}").access(selfOrAdmin())
                .requestMatchers(HttpMethod.GET, "/api/feedback/user/{username}").access(selfOrAdmin())

                // =============================================
                // 4. 登录即可（USER / DISCOUNT / ADMIN）
                // =============================================
                .requestMatchers("/api/rentals/**").hasAnyRole("USER", "DISCOUNT", "ADMIN")
                .requestMatchers("/api/orders/**").hasAnyRole("USER", "DISCOUNT", "ADMIN")
                .requestMatchers("/api/payments/**").hasAnyRole("USER", "DISCOUNT", "ADMIN")
                .requestMatchers("/api/feedback/**").hasAnyRole("USER", "DISCOUNT", "ADMIN")
                .requestMatchers("/api/users/verify-discount").hasAnyRole("USER", "DISCOUNT", "ADMIN")

                // =============================================
                // 5. 其余一律拒绝（漏配即 403，fail-closed）
                // =============================================
                .anyRequest().denyAll()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 动态权限：当前用户 == URL 中的 username，或者角色是 ADMIN。
     * 替代原来散落在 Controller 上的 hasRole('ADMIN') or #username == authentication.principal.username
     */
    private AuthorizationManager<RequestAuthorizationContext> selfOrAdmin() {
        return (authentication, context) -> {
            if (authentication == null || !authentication.get().isAuthenticated()) {
                return new AuthorizationDecision(false);
            }
            // 管理员放行
            boolean isAdmin = authentication.get().getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) {
                return new AuthorizationDecision(true);
            }
            // 检查 URL 中的 username 是否等于当前登录用户
            String pathUser = context.getVariables().get("username");
            String currentUser = authentication.get().getName();
            return new AuthorizationDecision(pathUser != null && pathUser.equals(currentUser));
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
