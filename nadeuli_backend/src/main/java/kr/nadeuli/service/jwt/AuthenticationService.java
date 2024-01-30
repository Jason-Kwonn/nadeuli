package kr.nadeuli.service.jwt;

import kr.nadeuli.dto.GpsDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.OauthTokenDTO;
import kr.nadeuli.dto.RefreshTokenDTO;
import kr.nadeuli.dto.TokenDTO;

public interface AuthenticationService {

  public TokenDTO addMember(MemberDTO memberDTO, GpsDTO gpsDTO) throws Exception;

  public TokenDTO login(String cellphone) throws Exception;

  public TokenDTO accessToken(MemberDTO memberDTO) throws Exception ;

  public TokenDTO refreshToken(RefreshTokenDTO refreshTokenDTO) throws Exception ;

//  public OauthTokenDTO getOauthToken(String code) throws Exception;

//  public void getOauthUserInfo(OauthTokenDTO oauthTokenDTO) throws Exception;
}
