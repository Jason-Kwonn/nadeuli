package kr.nadeuli.service.oauth.impl;




import java.util.Collections;
import java.util.Map;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.mapper.MemberMapper;
import kr.nadeuli.oauthinfo.OAuth2CustomUser;
import kr.nadeuli.oauthinfo.OAuthAttributes;
import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2MemberServiceImpl extends DefaultOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final MemberRepository memberRepository;

  private final MemberMapper memberMapper;

  private final MemberService memberService; // 추가

  @SneakyThrows
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.info("CustomOauth2MemberServiceImpl.loadUser() 실행 - OAuth2 로그인 요청 진입");

    /**
     * DefaultOAuth2UserService 객체를 생성하여, loadUser(userRequest)를 통해 DefaultOAuth2User 객체를 생성 후 반환
     * DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서
     * 사용자 정보를 얻은 후, 이를 통해 DefaultOAuth2User 객체를 생성 후 반환한다.
     * 결과적으로, OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
     */
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    /**
     * userRequest에서 registrationId 추출 후 registrationId으로 SocialType 저장
     * http://localhost:8080/oauth2/authorization/kakao에서 kakao가 registrationId
     * userNameAttributeName은 이후에 nameAttributeKey로 설정된다.
     */
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    log.info("registrationId:{}",registrationId);
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 로그인 시 키(PK)가 되는 값
    Map<String, Object> attributes = oAuth2User.getAttributes(); // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)
    log.info("attributes:{}",oAuth2User.getAttributes());

    // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
    OAuthAttributes extractAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);
    MemberDTO member = getUser(extractAttributes); // getUser() 메소드로 User 객체 생성 후 반환
    log.info("member:{}",member);
    // DefaultOAuth2User를 구현한 CustomOAuth2User 객체를 생성해서 반환
    return new OAuth2CustomUser(
        Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
        attributes,
        extractAttributes.getNameAttributeKey(),
        member.getSocialId(),
        member.getRole()
    );
  }


  /**
   * attributes에 들어있는 소셜 로그인의 식별값 email을 통해 회원을 찾아 반환하는 메소드 만약 찾은 회원이 있다면, 그대로 반환하고 없다면
   * saveUser()를 호출하여 회원을 저장한다.
   */
  private MemberDTO getUser(OAuthAttributes attributes) throws Exception {
    log.info("attributes:{}",attributes.getOauth2UserInfo().getSocialId());
    Member member = memberRepository.findBySocialId(attributes.getOauth2UserInfo().getSocialId()).orElse(null);

    if(member == null){
      saveUser(attributes);
      member = memberRepository.findBySocialId(attributes.getOauth2UserInfo().getSocialId()).orElse(null);
    }
    MemberDTO memberDTO = memberMapper.memberToMemberDTO(member);
    return memberDTO;
  }

  /**
   * OAuthAttributes의 toEntity() 메소드를 통해 빌더로 User 객체 생성 후 반환
   * 생성된 User 객체를 DB에 저장 : socialType, socialId, email, role 값만 있는 상태
   */
  private void saveUser(OAuthAttributes attributes) throws Exception {
    MemberDTO memberDTO = attributes.toEntity(attributes.getOauth2UserInfo());
    memberDTO.setTag(memberService.addTag()); // 변경
    memberDTO.setAffinity(0L);
    memberDTO.setPicture("https://kr.object.ncloudstorage.com/nadeuli/image/nadeuli20231221213746683.png");
    memberRepository.save(memberMapper.memberDTOToMember(memberDTO));
  }
}
