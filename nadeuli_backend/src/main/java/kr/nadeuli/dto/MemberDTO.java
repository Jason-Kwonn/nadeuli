package kr.nadeuli.dto;

import java.time.LocalDateTime;
import kr.nadeuli.category.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {

  private String tag;
  private String cellphone;
  private String nickname;
  private Long affinity;
  private String email;
  private String dongNe;
  private String picture;
  private Long nadeuliPayBalance;
  private boolean isActivate;
  private boolean isNadeuliDelivery;
  private Role role;
  private String gu;
  private String bankName;
  private String bankAccountNum;
  private String blockReason;
  private LocalDateTime blockEnd;
  private Long blockDay;
  private String socialId;

}