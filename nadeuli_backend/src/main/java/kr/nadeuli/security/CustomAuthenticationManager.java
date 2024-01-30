package kr.nadeuli.security;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

  //1. 사용자의 인증 정보를 검색하는데 사용되는 인터페이스인 CustomUserDetailsService를 주입받음
  private final CustomUserDetailsService customUserDetailsService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    //2. 전달된 Authentication 객체에서 사용자 이름을 추출하여 UserDetails를 가져옴
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(authentication.getName());
    log.info("아씨발");
    //3. 각종 처리를 구현
    //3-1. 기본만 구현할 것이기 때문에 나중을위해 커스텀한것
    //3-2. UserDetails에서 사용자 이름과 권한을 추출하여 UsernamePasswordAuthenticationToken을 생성함

    // UserDetails에서 사용자 이름과 권한을 추출하여 UsernamePasswordAuthenticationToken을 생성하고 반환
    return new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                                                   userDetails.getAuthorities());
  }
}
