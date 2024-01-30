package kr.nadeuli.service.jwt.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Optional;
import kr.nadeuli.category.Role;
import kr.nadeuli.dto.GpsDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.OauthTokenDTO;
import kr.nadeuli.dto.RefreshTokenDTO;
import kr.nadeuli.dto.TokenDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.mapper.MemberMapper;
import kr.nadeuli.security.CustomAuthenticationManager;
import kr.nadeuli.security.CustomAuthenticationToken;
import kr.nadeuli.service.jwt.AuthenticationService;
import kr.nadeuli.service.jwt.JWTService;
import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.member.MemberRepository;
import kr.nadeuli.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {

  @Value("${affinity}")
  private Long affinity;

  private final MemberMapper memberMapper;

  private final CustomAuthenticationManager authenticationManager;

  private final JWTService jwtService;

  private final MemberRepository memberRepository;

  private final MemberService memberService;

  private final AuthService authService;


  @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
  private String kakaoClientId;

  @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
  private String kakaoClientSecret;

  @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
  private String kakaoRedirectUri;

  @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
  private String kakaoTokenUri;

  public TokenDTO addMember(MemberDTO memberDTO, GpsDTO gpsDTO) throws Exception {
    // findByCellphone로 회원을 찾음
    Optional<MemberDTO> existingMember = memberRepository.findByCellphone(memberDTO.getCellphone())
        .map(memberMapper::memberToMemberDTO);
    // 회원이 이미 존재하는 경우 예외를 던짐

    if (existingMember.isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 회원입니다. 회원의 정보: " + existingMember.get());
    }else{
      // 회원이 존재하지 않는 경우
      memberDTO.setTag(memberService.addTag());
      memberDTO.setAffinity(affinity);
      //기본값이 유저이기떄문에 필요없음
      memberDTO.setRole(Role.USER);
      memberDTO.setNadeuliPayBalance(0L);
      memberDTO.setDongNe(memberService.addDongNe(memberDTO.getTag(),gpsDTO).getDocuments().get(1).getAddressName());
      memberDTO.setGu(memberService.addDongNe(memberDTO.getTag(),gpsDTO).getDocuments().get(1).getRegion2depthName());
      memberDTO.setPicture("https://kr.object.ncloudstorage.com/nadeuli/image/nadeuli20231221213746683.png");
      Member member = memberMapper.memberDTOToMember(memberDTO);
      memberRepository.save(member);
      MemberDTO existMember = memberRepository.findByTag(memberDTO.getTag()).map(memberMapper::memberToMemberDTO).orElseThrow(()-> new IllegalArgumentException("없는 태그입니다."));
      return accessToken(existMember);
    }
  }

  public TokenDTO login(String cellphone) throws Exception {
    // findByCellphone로 회원을 찾음
    Optional<MemberDTO> existingMember = memberRepository.findByCellphone(cellphone)
        .map(memberMapper::memberToMemberDTO);

      MemberDTO existMember = existingMember.get();
      return accessToken(existMember);

  }

  @Override
  public TokenDTO accessToken(MemberDTO memberDTO) throws Exception {
    CustomAuthenticationToken authRequest = new CustomAuthenticationToken(memberDTO.getTag());
    // CustomAuthenticationToken을 사용하려면 CustomAuthenticationManager의 authenticate가 호출되도록 해야 합니다.
    // 따라서 여기에서는 CustomAuthenticationManager를 직접 호출하게 됩니다.
    authenticationManager.authenticate(authRequest);
    Member member = memberRepository.findByTag(memberDTO.getTag()).orElseThrow(()-> new IllegalArgumentException("없는 태그입니다."));
    TokenDTO tokenDTO = new TokenDTO();

    tokenDTO.setAccessToken(jwtService.generateToken(member));
    tokenDTO.setRefreshToken(jwtService.generateRefreshToken(new HashMap<>(), member));

    return tokenDTO;
  }

  @Override
  public TokenDTO refreshToken(RefreshTokenDTO refreshTokenDTO) throws Exception  {
    // 받은 RefreshToken을 통해 유저 정보 중 아이디값추출 -> subject
    String memberTag = jwtService.extractUserName(refreshTokenDTO.getRefreshToken());
    // 추출한 아이디로 데이터를가져옴
    Member member = memberRepository.findByTag(memberTag).orElseThrow(()-> new IllegalArgumentException("없는 태그입니다."));

    //
    if(jwtService.isTokenValid(refreshTokenDTO.getRefreshToken(),member)){

      TokenDTO token = new TokenDTO();
      token.setAccessToken(jwtService.generateToken(member));
      token.setRefreshToken(jwtService.generateRefreshToken(new HashMap<>(),member)); // 새로운 리프레시 토큰 설정

      return token;
    }

    return null;
  }

  public OauthTokenDTO getOauthToken(String code) {

    //(2)
    RestTemplate rt = new RestTemplate();

    //(3)
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    //(4)
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", kakaoClientId);
    params.add("redirect_uri", kakaoRedirectUri);
    params.add("code", code);
    params.add("client_secret", kakaoClientSecret); // 생략 가능!

    //(5)
    HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
        new HttpEntity<>(params, headers);

    //(6)
    ResponseEntity<String> accessTokenResponse = rt.exchange(
        "https://kauth.kakao.com/oauth/token",
        HttpMethod.POST,
        kakaoTokenRequest,
        String.class
    );

    //(7)
    ObjectMapper objectMapper = new ObjectMapper();
    OauthTokenDTO oauthToken = null;
    try {
      oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthTokenDTO.class);
      log.info(oauthToken);
      getOauthUserInfo(oauthToken);
    }catch (Exception e) {
      throw new RuntimeException(e);
    }

    return oauthToken; //(8)
  }

  public void getOauthUserInfo(OauthTokenDTO oauthTokenDTO) throws Exception{

    //(2)
    RestTemplate rt = new RestTemplate();

    //(3)
    HttpHeaders headers = new HttpHeaders();

    // 2. 토큰으로 카카오 API 호출
    // HTTP Header 생성
    headers.add("Authorization", "Bearer " + oauthTokenDTO.getAccessToken());
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    // HTTP 요청 보내기
    HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
    ResponseEntity<String> response = rt.exchange(
        "https://kapi.kakao.com/v2/user/me",
        HttpMethod.POST,
        kakaoUserInfoRequest,
        String.class
    );

    log.info(response);
  };

}
