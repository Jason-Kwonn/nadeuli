package kr.nadeuli.oauthinfo;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import kr.nadeuli.category.Role;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

/**
 * DefaultOAuth2User를 상속하고, email과 role 필드를 추가로 가진다.
 */
@Getter
@Log4j2
public class OAuth2CustomUser extends DefaultOAuth2User {

  private String email;
  private final Role role;

  /**
   * Constructs a {@code DefaultOAuth2User} using the provided parameters.
   *
   * @param authorities      the authorities granted to the user
   * @param attributes       the attributes about the user
   * @param nameAttributeKey the key used to access the user's &quot;name&quot; from
   *                         {@link #getAttributes()}
   */
  public OAuth2CustomUser(Collection<? extends GrantedAuthority> authorities,
      Map<String, Object> attributes, String nameAttributeKey,
      String email, Role role) {
    super(authorities, attributes, nameAttributeKey);
    this.email = email;
    this.role = role;
    log.info("OAuth2CustomUser에서 받은 socialId는 {}", email);
  }

  public UserDetails toUserDetails() {
    // GrantedAuthority 생성 (ROLE_USER 등)
    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());
    log.info("toUserDetails의 email: {}", email);
    // UserDetails 생성
    return new User(email, "", Collections.singletonList(authority));
  }
}