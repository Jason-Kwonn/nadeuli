package kr.nadeuli.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import kr.nadeuli.dto.GpsDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.OauthTokenDTO;
import kr.nadeuli.dto.RefreshTokenDTO;
import kr.nadeuli.dto.TokenDTO;
import kr.nadeuli.service.jwt.AuthenticationService;
import kr.nadeuli.service.jwt.JWTService;
import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.oauth.impl.CustomOauth2MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nadeuli")
@RequiredArgsConstructor
@Log4j2
public class AuthenticationRestController {
  //1. Jwt 토큰 발급을 위한 RestController
  private final AuthenticationService authenticationService;

  private final MemberService memberService;

  private final JWTService jwtService;

  private final CustomOauth2MemberServiceImpl customOauth2MemberService;

  @Autowired
  private ObjectMapper objectMapper;

  @PostMapping("/addMember")
  public ResponseEntity<MemberDTO> addMember(@RequestBody Map<String, Object> requestData) throws Exception {
    log.info("/member/login : POST");

    MemberDTO memberDTO = objectMapper.convertValue(requestData.get("memberDTO"), MemberDTO.class);
    GpsDTO gpsDTO = objectMapper.convertValue(requestData.get("gpsDTO"), GpsDTO.class);

    log.info("addMember에서 받은 memberDTO는 {}", memberDTO);
    log.info("addMember에서 받은 gpsDTO는  {}", gpsDTO);

    TokenDTO tokenDTO = authenticationService.addMember(memberDTO, gpsDTO);

    MemberDTO existMember = null;

    if (tokenDTO.getAccessToken() != null) {
      String memberTag = jwtService.extractUserName(tokenDTO.getAccessToken());
      log.info("멤버태그는: {}", memberTag);
      existMember = memberService.getMember(memberTag);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + tokenDTO.getAccessToken());

    return new ResponseEntity<>(existMember, headers, HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<MemberDTO> login(@RequestBody Map<String, Object> requestData) throws Exception {
    log.info("/member/login : POST");
    MemberDTO memberDTO = objectMapper.convertValue(requestData.get("memberDTO"), MemberDTO.class);

    log.info("addMember에서 받은 memberDTO는 {}", memberDTO);

    TokenDTO tokenDTO = authenticationService.login(memberDTO.getCellphone());

    MemberDTO existMember = null;

    if (tokenDTO.getAccessToken() != null) {
      String memberTag = jwtService.extractUserName(tokenDTO.getAccessToken());
      log.info("멤버태그는: {}", memberTag);
      existMember = memberService.getMember(memberTag);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + tokenDTO.getAccessToken());

    return new ResponseEntity<>(existMember, headers, HttpStatus.OK);
  }


  @PostMapping("/refresh")
  public TokenDTO refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) throws Exception {

    log.info("/api/v1/auth/json/refresh : POST");
    log.info("refresh에서 받은 tokenDTO는 {}",refreshTokenDTO);

    return authenticationService.refreshToken(refreshTokenDTO);
  }
  @PostMapping("/findAccount")
  public boolean findAccount(@RequestBody Map<String, Object> requestData) throws Exception {
    log.info("/nadeuli/findAccount : POST : {}", requestData);

    MemberDTO memberDTO = objectMapper.convertValue(requestData.get("memberDTO"), MemberDTO.class);

    // memberDTO가 null이거나, findAccount가 false인 경우에 false를 반환.
    return memberDTO != null && memberService.findAccount(memberDTO.getEmail());
  }

  @PostMapping("/updateCellphone")
  public int updateCellphone(@RequestBody MemberDTO memberDTO) throws Exception {
    log.info("/nadeuli/updateCellphone : POST : {}", memberDTO);
    int result = memberService.updateCellphone(memberDTO) ? 1 : 0;
    log.info("Result: {}", result);
    return result;
  }


}
