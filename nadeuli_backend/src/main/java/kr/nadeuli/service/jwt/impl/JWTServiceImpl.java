package kr.nadeuli.service.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import kr.nadeuli.service.jwt.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Log4j2
@Service
public class JWTServiceImpl implements JWTService {

  @Value("${jwt.secretKey}")
  private String secretKey;

  @Value("${jwt.accessTokenExpirationTime}")
  private long accessTokenExpirationTime;

  @Value("${jwt.refreshTokenExpirationTime}")
  private long refreshTokenExpirationTime;


  ///Method
  //1. 생성을 담당하는 토큰 생성 메서드
  // - 사용자 세부 정보를 허용하며 문자열을 반환한다.
  public String generateToken(UserDetails userDetails){

    //2.빌더를사용하여 JWT의 제목을 사용자의 이메일로 생성
    // - 발행날짜를 현시간대로 설정
    // - 만료일을 원하는 시간대로 설정 => 하루로 설정함
    // - 서명은 HS256형식으로 signKey메소드를 활용하여 서명진행
    // - 압축메소드 호출
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
        .signWith(getSignKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  // 소셜 로그인에서 socialId를 이용하여 토큰 생성
  public String generateSocialLoginToken(UserDetails userDetails) {
    log.info("userDetails:{}",userDetails.toString());
    log.info("userDetails의 Username:{}",userDetails.getUsername());
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
        .signWith(getSignKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails){

    //2.빌더를사용하여 JWT의 제목을 사용자의 이메일로 생성
    // - 발행날짜를 현시간대로 설정
    // - 만료일을 원하는 시간대로 설정 => 하루로 설정함
    // - 서명은 HS256형식으로 signKey메소드를 활용하여 서명진행
    // - 압축메소드 호출
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
        .signWith(getSignKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  //3. key를 반환하는 getSignKey 메소드 생성
  private Key getSignKey(){
    // - JWT에서 제공하는 디코더를 사용하여 바이트리스트를 반환하는 키 할당
    // - 32자의 비밀키를 입력한 뒤 토큰 생성 메소드 호출 시 아래 값을 가져올 수 있다.
    byte[] key = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(key);
  }

  //4. 토큰에서 클레임을 추출하는 메소드 생성
  // - claim은 페이로드(Payload)에 포함된 정보를 나타내는 부분을 의미
  // - claim은 key-value형식
  // - 페이로드는 인코딩 된 토큰에 포함된 정보를 의미함
  // - 인자로 토큰을 받아서 claim을 추출하고 함수형 인터페이스인 claimResolvers에 넘겨져 사용된다.
  // - 이를위해서는 extractAllClaims메소드를 정의해야한다
  private <T> T extractClaim(String token, Function<Claims, T> claimResolvers){

    final Claims claims = extractAllClaims(token);
    return claimResolvers.apply(claims);
  }

  //5. 토큰의 claim을 추출하는 extractAllClaims메소드 생성
  // - JWT토큰을 파싱하기위해 Jwts.parserBuilder() 사용
  // - setSigningKey(getSignKey())를 호출하여 서명 키를 설정
  // - parseClaimsJws(token)를 호출하여 주어진 토큰을 파싱하고 서명을 검증 후 클레임들을 추출
  // - getBody()를 호출하여 추출된 클레임들을 포함하는 Claims 객체를 반환
  private Claims extractAllClaims(String token){
    return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
  }

  //6. 사용자 이름을 추출하는 메소드 생성
  // - 사용자 정보가 담긴 토큰에서 이름 추출하기위해 인자로 토큰을 받음
  // - 메소드를 외부에서 사용하기위해 public 지정
  // - 메소드 참조형태로 함수형 인터페이스 Function<Claims, T>를 구현하는 함수를 전달
  // - Java 8에 도입된 메서드 참조는 class::methodName 구문을 사용하여 클래스 또는 객체에서 메서드를 참조
  public String extractUserName(String token){
    log.info("extractUserName에서 받은 token은 {}", token);
    log.info("extractUserName은 {}", extractClaim(token, Claims::getSubject));
    log.info("extractAllClaims은 {}", extractAllClaims(token));
    return extractClaim(token, Claims::getSubject);
  }


  //토큰 유효성 확인
  public boolean isTokenValid(String token, UserDetails userDetails){
    //사용자의 이름을 토큰에서 추출
    log.info("받은 토큰은: {}", token);
    log.info("받은 유저는: {}", userDetails);
    final String username = extractUserName(token);
    log.info("유저네임은: {}", username);
    log.info("토큰 만료 여부: {}", isTokenExpired(token));
    log.info("userDetails.getUsername()는: {}", userDetails.getUsername());

    boolean isTokenValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    log.info("토큰 유효 결과는: {}", isTokenValid);

    return isTokenValid;
  }

  //만료된 토큰인지 확인하는 메소드 생성
  private boolean isTokenExpired(String token){
    //extractClaim메소드를 통해 토큰의 만료시간을 추출하고 시간대를 비교함
    //.before 첫 번째 Date 객체가 두 번째 Date 객체보다 이전인지를 비교
    //첫번쨰 Date가 과거일경우 true반환
    return extractClaim(token, Claims::getExpiration).before(new Date());

    //JwtAuthenticationFilter로 이동하여 토큰 유효성 확인
  }

}