package kr.nadeuli.service.auth;

import jakarta.mail.MessagingException;
import kr.nadeuli.dto.AuthDTO;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;

public interface AuthService {

  public SingleMessageSentResponse sendSms(AuthDTO authDTO);
//  public void sendOne(String cellphone);

  public void sendMail(AuthDTO authDTO) throws MessagingException;

  public boolean verifySms(AuthDTO authDTO) throws Exception;


}