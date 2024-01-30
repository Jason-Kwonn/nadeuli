package kr.nadeuli.auth;


import java.util.Optional;
import kr.nadeuli.dto.AuthDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.mapper.MemberMapper;
import kr.nadeuli.service.auth.AuthRepository;
import kr.nadeuli.service.auth.AuthService;
import kr.nadeuli.service.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class AuthApplicationTests {


  @Autowired
  AuthService authService;

  @Autowired
  AuthRepository authRepository;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  MemberMapper memberMapper;


  @DisplayName("휴대폰 인증 번호 전송 테스트")
//  @Test
  public void testSendSms() throws Exception{
    AuthDTO authDTO = AuthDTO.builder()
        .to("01034431643")
        .build();

    // 1. cellphone을 통해 존재하는 회원인지 확인
    Optional<MemberDTO> existingMember = memberRepository.findByCellphone(authDTO.getTo())
        .map(memberMapper::memberToMemberDTO);

    // 2.회원이 존재할 경우
    if (existingMember.isPresent()) {
      authService.sendSms(authDTO);
    } else {

      // 3.회원이 존재하지 않을 경우 예외 처리
      System.out.println("존재하지 않는 회원입니다.");
    }
  }

  @DisplayName("이메일 인증 번호 전송 테스트")
//  @Test
  public void testSendMail() throws Exception{
    AuthDTO authDTO = AuthDTO.builder()
        .to("mayuaa2@naver.com")
        .build();

    // 1. cellphone을 통해 존재하는 회원인지 확인
    Optional<MemberDTO> existingMember = memberRepository.findByEmail(authDTO.getTo())
        .map(memberMapper::memberToMemberDTO);

    // 2.회원이 존재할 경우
    if (existingMember.isPresent()) {
      authService.sendMail(authDTO);
    } else {

      // 3.회원이 존재하지 않을 경우 예외 처리
      System.out.println("존재하지 않는 회원입니다.         ");
    }
  }

  @DisplayName("인증 번호 확인 테스트")
//  @Test
  public void testVerifySms() throws Exception{
    AuthDTO authDTO = AuthDTO.builder()
        .to("01034431643")
        .authNumber("38702")
        .build();

    System.out.println(authService.verifySms(authDTO));

  }

}