package kr.nadeuli.security;

import java.util.Collections;
import java.util.Map;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.mapper.MemberMapper;
import kr.nadeuli.oauthinfo.OAuth2CustomUser;
import kr.nadeuli.oauthinfo.OAuthAttributes;
import kr.nadeuli.service.member.MemberRepository;
import kr.nadeuli.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Log4j2
public class CustomUserDetailsService extends DefaultOAuth2UserService implements UserDetailsService {

  private final MemberRepository memberRepository;

  //1. JwtAuthenticationFilter에서 사용할 메소드 생성
  // - UserDetailsService는 Spring Security에서 사용자의 인증 정보를 검색하는 데 사용되는 인터페이스
  /*
   * 내부에 메서드를 생성하는 이유는 userDetailsService 메서드가 UserDetailsService 인터페이스를 구현한 객체를 반환해야 하기 때문
   * Spring Security는 사용자 인증에 사용될 UserDetailsService 구현체를 필요로 한다.
   * 그러나 UserDetailsService 인터페이스는 loadUserByUsername이라는 메서드를 반드시 구현해야 한다.
   * 이 메서드는 사용자 이름(일반적으로 이메일)을 기반으로 사용자 정보를 가져와야 한다.
   * 따라서 userDetailsService 메서드 내에서 익명 내부 클래스를 생성하고
   * loadUserByUsername 메서드를 구현하는 것은 UserDetailsService 인터페이스의 요구사항을 충족시키기 위한 것이다.
   * 이렇게 하면 사용자 정보를 검색하는 로직을 UserDetailsService 구현 내부에 둘 수 있으며,
   * Spring Security가 필요할 때 이 메서드를 호출하여 사용자를 인증할 수 있습니다.
   * 내부에 메서드를 생성하는 이러한 방식은 코드를 모듈화하고 관리하기 쉽게 만들며,
   * UserDetailsService의 구현을 하나의 메서드 내에서 정의할 수 있어 효율적이다.
   * */

  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    log.info("identifier:{}",identifier);
    if (isOAuth2Login()) {
      // OAuth2 로그인일 경우
      return loadUserBySocialId(identifier);
    } else {
      // 자체 회원가입일 경우
      return loadUserByTag(identifier);
    }
  }

  private UserDetails loadUserBySocialId(String socialId) {
    UserDetails userDetails = memberRepository.findBySocialId(socialId)
        .orElseThrow(() -> new UsernameNotFoundException("소셜 ID에 해당하는 사용자를 찾을 수 없습니다."));
    if (userDetails == null) {
      throw new UsernameNotFoundException("소셜 ID에 해당하는 사용자를 찾을 수 없습니다.");
    }
    return userDetails;
  }

  private UserDetails loadUserByTag(String tag) {
    UserDetails userDetails = memberRepository.findByTag(tag)
        .orElseThrow(() -> new UsernameNotFoundException("tag에 해당하는 사용자를 찾을 수 없습니다."));
    if (userDetails == null) {
      throw new UsernameNotFoundException("tag에 해당하는 사용자를 찾을 수 없습니다.");
    }
    return userDetails;
  }

  private boolean isOAuth2Login() {
    log.info("isOAuth2Login");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info("authentication:{}",authentication);
    if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
      // OAuth2 로그인일 경우
      return true;
    } else {
      // 자체 회원가입일 경우
      return false;
    }
  }

}