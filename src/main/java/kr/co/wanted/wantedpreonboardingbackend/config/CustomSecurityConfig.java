package kr.co.wanted.wantedpreonboardingbackend.config;

import kr.co.wanted.wantedpreonboardingbackend.security.APIUserDetailsService;
import kr.co.wanted.wantedpreonboardingbackend.security.filter.ApiLoginFilter;
import kr.co.wanted.wantedpreonboardingbackend.security.filter.RefreshTokenFilter;
import kr.co.wanted.wantedpreonboardingbackend.security.filter.TokenCheckFilter;
import kr.co.wanted.wantedpreonboardingbackend.security.handler.APILoginSuccessHandler;
import kr.co.wanted.wantedpreonboardingbackend.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final JWTUtil jwtUtil;
    private final APIUserDetailsService apiUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations());
    }

    /**
     *
     * AbstractAuthenticationProcessingFilter 는 로그인 처리를 담당하기 때문에 다른 필터과 달리 로그인을 처리하는 경로에 대한 설정과 실제 인증을 처리를 담당하는 AuthenticationManager 객체의 설정이 필수로 필요한다.
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        //AuthenticationManager 설정
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(apiUserDetailsService).passwordEncoder(passwordEncoder());
        // Get AuthenticationManager
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        //반드시 필요
        http.authenticationManager(authenticationManager);

        //APILoginFilter
        ApiLoginFilter apiLoginFilter = new ApiLoginFilter("/api/member/login");
        apiLoginFilter.setAuthenticationManager(authenticationManager);


        //APILoginSuccessHandler
        APILoginSuccessHandler successHandler = new APILoginSuccessHandler(jwtUtil);
        //SuccessHandler 세팅
        apiLoginFilter.setAuthenticationSuccessHandler(successHandler);


        //APILoginFilter의 위치 조정
        http.addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class);

        //api로 시작하는 모든 경로는 TokenCheckFilter 동작
        http.addFilterBefore(
                tokenCheckFilter(jwtUtil, apiUserDetailsService),
                UsernamePasswordAuthenticationFilter.class
        );

        //refreshToken 호출 처리
        http.addFilterBefore(new RefreshTokenFilter("/refreshToken", jwtUtil),
                TokenCheckFilter.class);

        http.csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();

    }

    private TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil, APIUserDetailsService apiUserDetailsService) {
        return new TokenCheckFilter(jwtUtil, apiUserDetailsService);
    }
}
