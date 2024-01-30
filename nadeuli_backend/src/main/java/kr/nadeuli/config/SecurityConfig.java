package kr.nadeuli.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import kr.nadeuli.category.Role;
import kr.nadeuli.security.CustomAuthenticationManager;
import kr.nadeuli.security.CustomUserDetailsService;
import kr.nadeuli.security.OAuth2LoginFailureHandler;
import kr.nadeuli.security.OAuth2LoginSuccessHandler;
import kr.nadeuli.service.oauth.impl.CustomOauth2MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfig {
  /** 번호를 잘 따라갈것. 용어설명부터 시작하여 흐름을 제공하겠다.
  *  1. 인증및 권한을 부여, 보호하는 기능을 제공
  *  1-1. credential(principal -> username, credential -> password) 방식을 사용
  *       securityFilterChain을 기반으로 동작한다.
  *       XML설정을 안하고 @Bean을 사용해서 유지관리에 용이하다.
  *
  *  2. SecurityContextHolder, SecurityContext, Authentication 컴포넌트
  *
  *            SecurityContextHolder > SecurityContext > Authentication
  *
  *  2-1. SecurityContextHolder는 Spring Security에서 현재 실행 중인 스레드의 보안 컨텍스트를 저장하고 제공하는 역할
  *       보안 컨텍스트에는 현재 사용자의 인증 및 권한 정보가 포함되어있다.
  *       웹 APP의 경우 HTTP요청을 처리하는동안 현재 사용자의 보안 정보를 제공한다.
  *       즉 SecurityContextHolder를 사용하면 현재 사용자의 정보를 쉽게 얻을 수 있다.
  *
  *  2-2. SecurityContext는 SecurityContextHolder보다 작은 개념이다.
  *       현재 실행 중인 스레드와 관련된 보안 정보(사용자의 인증 및 권한 정보)를 저장하는 객체다
  *       여러 스레드가 동시에 실행되는 경우 각 스레드에 대해 별도의 SecurityContext가 관리된다.
  *       SecurityContextHolder를 통해서 액세스된다.
  *
  *  2-3. Authentication는 인터페이스이며 사용자가 누구인지 확인하는 데 필요한 정보를 제공한다.
  *       Principal과 GrantedAuthority를 포함한다.
  *       Principal은 현재 사용자를 나타내는 객체이며 UserDetails를 구현한 객체다. -> 사용자 식별값 -> 나드리시스템에서는 tag
  *       GrantedAuthority는 사용자에게 부여된 권한을 나타내는 객체다 -> ROLE_USER
  *
  *  3. UsernamePasswordAuthenticationToken 컴포넌트
  *
  *  3-1. Autentication을 구현한 AbstractAuthenticationToken의 하위의 하위클래스로
  *       사용자의 아이디와 비밀번호를 저장하는데 사용
  *       주로 폼 기반 로그인(form-based login)에서 사용자가 제출한
  *       아이디(principal)와 비밀번호(credential)를 이 클래스를 활용하여 인증
  *       첫번째 생성자는 인증 전에 객체를 생성하고, 두번째는 인증이 완료된 객체를 생성
  *
  *  4. AuthenticationManager 컴포넌트
  *
  *  4-1. 사용자를 인증하는 인터페이스이며 하나 이상의 AuthenticationProvider를 가지며, 실제로 사용자를 인증한다.
  *       AuthenticationManager는 authenticate 메서드를 가지고 있다.
  *       authenticate 메서드는 Authentication(사용자객체, 권한)객체를 받아 사용자를 인증하고,
  *       성공하면 Authentication 객체를 반환한다. 만약 인증에 실패하면 예외를 던진다.
  *
  *  4-2. AuthenticationManager의 구현체로는 ProviderManager가있다.
  *       ProviderManager는 여러개의  AuthenticationProvider를 가지고있고
  *       AuthenticationProvider에게 실제 인증 작업을 위임한다.
  *
  *  5. AuthenticationProvider 컴포넌트
  *
  *  5-1. 사용자의 인증을 처리하는 인터페이스이며 커스텀 가능하다.
  *       AuthenticationProvider는 하나의 메서드, 즉 authenticate 메서드를 가지는데
  *       AuthenticationManager를 구현했기때문에 기능은 같다.
  *       부가적으로 Authentication의 객체타입을 지정하는 supports 메서드도 가진다.
  *       UsernamePasswordAuthenticationToken를 지원한다.
  *
  *  6. ProviderManager 컴포넌트
  *
  *  6-1. 여러 개의 AuthenticationProvider를 관리하고 순서대로 시도하여 인증을 시도하는 매니저
  *       여러 인증 프로바이더가 등록되어 있을 때 각각에 대해 시도하며, 첫 번째로 성공한 프로바이더의 결과를 반환
  *       authenticate 메서드를 호출하여 사용자 인증을 시도하면, 등록된 AuthenticationProvider들이 순서대로 호출
  *
  *  7. UserDetailsService 컴포넌트
  *
  *  7-1. 사용자 정보를 데이터베이스나 다른 저장소에서 가져오기 위한 인터페이스이며
  *       인터페이스를 구현하여 사용자 정보를 로드하고, Spring Security가 이 정보를 활용하여 인증을 수행
  *       UserDetailsService의 주요 메서드는 loadUserByUsername다.
  *       사용자 이름(또는 식별자)을 받아 해당 사용자의 정보를 로드하고 UserDetails 객체로 반환
  *
  *  8. UserDetails 컴포넌트
  *
  *  8-1. 사용자의 정보를 나타내는 인터페이스이며 구현한 객체는 사용자의 자격 증명 및 권한 정보를 제공한다.
  *       보통 User 클래스를 사용하여 UserDetails를 구현한다.
  *       인증에 성공하여 생성된 UserDetails클래스는 Authentication 객체를 구현한
  *       UsernamePasswordAuthenticationToken을 생성하기 위해 사용된다.
  *
  *  9. AuthenticationFilter 컴포넌트
  *
  *  9-1. 사용자의 인증(Authentication)을 처리하는 역할을하고
  *       사용자가 로그인하거나 특정 자원에 접근할 때 인증을 수행하는 데에 쓰인다.
  *       인증 요청을 가로채고, Authentication 객체를 만든 뒤 AuthenticationManager에게 인증 역할을 위임함
  *       로그인 URL, 로그인 성공/실패 핸들러, 필터 순서 등을 조정할 수 있다
  *
  * */



  /** Spring Security 흐름
  *  1. 사용자의 로그인 정보,인증요청이 담긴 Http Request를 수신한다
  *
  *  2. AuthenticationFilter가 요청을 가로챈다(Jwt를 사용하기때문에 JwtAuthenticationFilter를 커스텀)
  *  2-1. 가로챈 정보를 통해 UsernamePasswordAuthenticationToken의 인증용 객체를 생성한다.
  *
  *  3. AuthenticationManager을 구현한 ProviderManager에게 생성한 UsernamePasswordToken객체 전달
  *
  *  4. AutenticationManger는 등록된 AuthenticationProvider들을 조회하며 인증을 요구
  *
  *  5. 실제 데이터베이스에서 사용자 인증정보를 가져오는 UserDetailsService에 사용자 정보를 넘김
  *
  *  6. UserDetails를 이용해 User객체에 대한 정보 탐색
  *  6-1. 넘겨받은 사용자 정보를 통해 데이터베이스에서 찾아낸 사용자 정보인 UserDetails 객체를 만든다.
  *
  *  7. User 객체의 정보들을 UserDetails가 UserDetailsService(LoginService)로 전달
  *  7-1. AuthenticaitonProvider들은 UserDetails를 넘겨받고 사용자 정보를 비교한다.
  *
  *  8. 인증이 완료되면 권한 등의 사용자 정보를 담은 Authentication 객체를 반환한다.
  *
  *  9. 인증이 끝나면 다시 최초의 AuthenticationFilter에 Authentication 객체가 반환된다
  *
  *  10. SecurityContext에 인증 객체를 설정
  *  10-1. Authentication 객체를 Security Context에 저장한다.
  *  10-2. 최종적으로는 SecurityContextHolder는 세션 영역에 있는 SecurityContext에 Authentication 객체를 저장.
  *        사용자 정보를 저장한다는 것은 스프링 시큐리티가 전통적인 세선-쿠키 기반의 인증 방식을 사용한다는 것을 의미.
  * */

  ///Field
  // JWT가 유효한지 체크하는 인증필터
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  // UserDetailsService를 커스텀한 서비스
  private final CustomUserDetailsService customUserDetailsService;

  // 소셜로그인 OAuth2Login의 성공/실패에 대한 처리
  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
  private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

  // Oauth2MemberService를 커스텀한 서비스
  private final CustomOauth2MemberServiceImpl customOauth2MemberService;

  // AuthenticationManager를 커스텀
  private final CustomAuthenticationManager customAuthenticationManager;

  public static final String DEFAULT_FILTER_PROCESSES_URI = "/login/oauth2/code/*"; // 기본 값

  ///Method
  /**
   * 이 메서드는 정적 자원에 대해 보안을 적용하지 않도록 설정한다.
   * 정적 자원은 보통 HTML, CSS, JavaScript, 이미지 파일 등을 의미하며,
   * 이들에 대해 보안을 적용하지 않음으로써 성능을 향상시킬 수 있다.
   */
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*")); // 허용할 origin을 나열
    configuration.setAllowedMethods(Arrays.asList("*"));
    configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "Content-Type", "Authorization", "X-XSRF-token"));
    configuration.setAllowCredentials(false);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    configuration.addExposedHeader("Authorization");
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
  //6. SecurityFilterChain을 반환하는 메소드 생성
  //6-1. 보안설정에대해서 어떻게 처리해나갈지 흐름 제공
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        //7. CSRF(Cross-Site Request Forgery) 공격을 비활성화하여 보안문제를 방지하고 웹 애플리케이션의 안전성을 강화
        //7-1. 웹 애플리케이션의 취약점 중 하나로, 사용자가 자신의 의지와는 무관하게 공격자가 의도한 행위를 하도록 만드는 공격
        //7-2. csrf를 비활성화하기위해 AbstractHttpConfigurer를 disable
        .csrf(AbstractHttpConfigurer::disable)
        /**
         * CORS(Cross-Origin Resource Sharing)는 다른 도메인의 리소스에 웹 페이지가 접근할 수 있도록 브라우저에게 권한을 부여하는 메커니즘이다.
         * 아래의 설정은 특정 CORS 구성 소스(corsConfigurationSource())를 사용하여 CORS 설정을 적용한다.
         * */
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        //8. HTTP 요청에 대한 접근 권한을 설정
        .authorizeHttpRequests((request) -> request
            //8-1. .permitAll()에 해당하는 URI는 인증되지않은 회원도 접근 가능
            .requestMatchers("/api/v1/auth/**","/resources/**","/nadeulidelivery/**","/product/**","/nadeuliPay/**","/trade/**","/orikkiri/**","/orikkiriManage/**","/dongNe/**","/nadeuli/**","/auth/**","/login","/error","/index.html","/oauth2/**","/api/**").permitAll()
            //8-2. ADMIN만 접근가능
            .requestMatchers("/api/v1/admin/**").hasAnyAuthority(Role.ADMIN.name())
            //8-3. USER만 접근가능
            .requestMatchers("/member/**").hasAnyAuthority(Role.USER.name())
            //8-4. 모든 요청에 대해 인증이 필요함
            .anyRequest().authenticated())
        //세션관리 비활성화,상태를 저장하지 않는 세션을 사용하며
        //모든 요청은 세션에 의존하지 않는다. 이는 주로 토큰 기반의 인증을 사용할 때 사용
        .sessionManagement(
            manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .formLogin(login -> login
            .disable() // 기본 로그인 페이지 생성 필터를 비활성화
        )
        .httpBasic(Customizer.withDefaults()) // httpBasic 사용 X
        .oauth2Login(oauth2Configurer -> oauth2Configurer
                         .userInfoEndpoint(userInfo -> userInfo
                             .userService(customOauth2MemberService))
                         .successHandler(oAuth2LoginSuccessHandler)
                         .failureHandler(oAuth2LoginFailureHandler)
        )

        //사용자 인증을 처리
        //사용자의 인증을 검증하고 사용자 정보를 가져오는 역할
        //사용자의 JWT 토큰을 검증하고, 사용자를 인증
//        .authenticationManager(customAuthenticationManager)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(logout -> logout
            .logoutUrl("/api/v1/member/logout")  // 로그아웃 URL 지정
            .logoutSuccessHandler((request, response, authentication) -> {
              // SecurityContext에서 Authentication을 지워 로그아웃
              SecurityContextHolder.clearContext();

              // 쿠키 삭제
              Cookie cookie = new Cookie(HttpHeaders.AUTHORIZATION, null);
              cookie.setPath("/");
              cookie.setMaxAge(0);
              response.addCookie(cookie);

              // 헤더에서도 삭제
              response.setHeader(HttpHeaders.AUTHORIZATION, "");

              // 로그아웃 성공 시 리다이렉트 또는 메시지 등 추가할 수 있음
              response.setStatus(HttpServletResponse.SC_OK);
            })
        );

    // SecurityFilterChain 반환
    //비밀번호 인증 필터 클래스 제공

    System.out.println("시큐리티필터체인");
    return http.build();

  }

  //사용자의 인증을 처리하는 역할
  @Bean
  public AuthenticationProvider authenticationProvider() throws Exception{
    System.out.println("프로바이더");
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    //사용자 정보를 가져오는 데 사용될 UserDetailsService를 설정
    authenticationProvider.setUserDetailsService(customUserDetailsService);
    // 비밀번호 암호화를 처리하는 데 사용될 PasswordEncoder를 설정
    // authenticationProvider.setPasswordEncoder(passwordEncoder());

    // 사용자 정보를 로드하기 전에 실행되는 동작
    authenticationProvider.setPreAuthenticationChecks(userDetails -> {
      // 여기에 로그 출력 또는 확인하고자 하는 동작 추가
      System.out.println("사용자 정보 로드 전 동작");
    });

    // 사용자 정보를 로드한 후에 실행되는 동작
    authenticationProvider.setPostAuthenticationChecks(userDetails -> {
      // 여기에 로그 출력 또는 확인하고자 하는 동작 추가
      System.out.println("사용자 정보 로드 후 동작");
    });
    return authenticationProvider;
  }



}

