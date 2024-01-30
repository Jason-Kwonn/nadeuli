package kr.nadeuli.security;

import io.netty.handler.codec.http.cookie.CookieHeaderNames.SameSite;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.oauthinfo.OAuth2CustomUser;
import kr.nadeuli.service.jwt.JWTService;
import kr.nadeuli.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JWTService jwtService;
  private final CustomUserDetailsService customUserDetailsService;
  private final MemberService memberService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    log.info("OAuth2 Login 성공!");

    // OAuth2 사용자 정보 가져오기
    OAuth2CustomUser oAuth2User = (OAuth2CustomUser) authentication.getPrincipal();

    // JWT 토큰을 생성하고 응답 헤더에 추가
      // OAuth2 사용자 정보를 UserDetails로 변환
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(oAuth2User.toUserDetails().getUsername());

      log.info("userDetails은 {}", userDetails.toString());

      // UserDetails를 이용하여 JWT 토큰, RefreshToken 생성
      String accessToken = jwtService.generateSocialLoginToken(userDetails);
      String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), userDetails);
      log.info("accessToken은 {}",accessToken);
      log.info("refreshToken은 {}",refreshToken);

    // AccessToken 쿠키 추가
    Cookie accessTokenCookie = new Cookie("Authorization", accessToken);
    accessTokenCookie.setMaxAge(3600); // 쿠키 유효 시간 설정 (예: 1시간)
    accessTokenCookie.setSecure(true); // HTTPS 전용으로 설정
//    accessTokenCookie.setHttpOnly(true); // JavaScript에서 쿠키 접근을 막음
    accessTokenCookie.setDomain("www.nadeuli.kr");
    accessTokenCookie.setPath("/"); // 쿠키의 유효 경로 설정 (루트 경로로 설정하면 전체 애플리케이션에서 사용 가능)
    response.addCookie(accessTokenCookie);

    // RefreshToken 쿠키 추가
    Cookie refreshTokenCookie = new Cookie("Refresh-Token", refreshToken);
    refreshTokenCookie.setMaxAge(2592000); // 쿠키 유효 시간 설정 (예: 30일)
    refreshTokenCookie.setSecure(true); // HTTPS 전용으로 설정
//    refreshTokenCookie.setHttpOnly(true); // JavaScript에서 쿠키 접근을 막음
    accessTokenCookie.setDomain("www.nadeuli.kr");
    refreshTokenCookie.setPath("/"); // 쿠키의 유효 경로 설정 (루트 경로로 설정하면 전체 애플리케이션에서 사용 가능)
    response.addCookie(refreshTokenCookie);
    response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

    // 리다이렉션 URL을 생성할 때 액세스 토큰을 포함
    String redirectUrl = "https://www.nadeuli.kr/oAuth2RedirectHandler?accessToken=" + accessToken;
    response.sendRedirect(redirectUrl);
  }


}