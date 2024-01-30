package kr.nadeuli.service.auth.impl;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import java.util.Random;
import kr.nadeuli.dto.AuthDTO;
import kr.nadeuli.service.auth.AuthRepository;
import kr.nadeuli.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@RequiredArgsConstructor
@Log4j2
@Service
public class AuthServiceImpl implements AuthService {

  @Value("${coolsms.api.key}")
  private String apiKey;
  @Value("${coolsms.api.secret}")
  private String apiSecretKey;
  @Value("${coolsms.sender.number}")
  private String senderNumber;
  @Value("${sender.text}")
  private String messageTest;
  @Value("${mail.title}")
  private String title;

  private final AuthRepository smsCertification;
  private DefaultMessageService messageService;

  private final AuthRepository smsCertificationRepository;

  private final JavaMailSender javaMailSender;

  private final SpringTemplateEngine templateEngine;

  @PostConstruct
  private void init(){
    this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr");
  }

  // 단일 메시지 발송 예제
  @Override
  public SingleMessageSentResponse sendSms(AuthDTO authDTO) {
    Message message = new Message();
    authDTO.setAuthNumber(generateRandomCode(5));
    // 발신번호 및 수신번호는 반드시 01012345678 형태.
    message.setFrom(senderNumber);
    message.setTo(authDTO.getTo());
    message.setText(messageTest +" "+ authDTO.getAuthNumber());

    SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

    // DB에 발송한 인증번호 저장
    smsCertification.addAuthNum(authDTO);

    return response;
  }

  public void sendMail(AuthDTO authDTO) throws MessagingException {
    authDTO.setAuthNumber(generateRandomCode(5));
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();


    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
    mimeMessageHelper.setTo(authDTO.getTo()); // 메일 수신자
    mimeMessageHelper.setSubject(title); // 메일 제목
    mimeMessageHelper.setText(setContext(authDTO.getAuthNumber()), true); // 메일 본문 내용, HTML 여부
    javaMailSender.send(mimeMessage);

    // DB에 발송한 인증번호 저장
    smsCertification.addAuthNum(authDTO);

    log.info("Success");


  }

  private String generateRandomCode(int length) {
    StringBuilder code = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      code.append(random.nextInt(10)); // 0부터 9까지의 랜덤한 숫자
    }
    return code.toString();
  }

  //사용자가 입력한 인증번호가 Redis에 저장된 인증번호와 동일한지 확인
  @Override
  public boolean verifySms(AuthDTO authDTO) throws Exception {
    if (!(smsCertificationRepository.isAuthNum(authDTO.getTo()) &&
        smsCertificationRepository.getAuthNum(authDTO.getTo())
            .equals(authDTO.getAuthNumber()))) {
      throw new Exception("인증번호가 일치하지 않습니다.");
    }

    smsCertificationRepository.deleteAuthNum(authDTO.getTo());

    return true;
  }

  // thymeleaf를 통한 html 적용
  public String setContext(String authNum) {
    Context context = new Context();
    context.setVariable("authNum", authNum);
    return templateEngine.process("email",context);
  }
}