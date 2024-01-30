package kr.nadeuli.member;


import java.time.LocalDateTime;
import java.util.List;
import kr.nadeuli.category.DeliveryState;
import kr.nadeuli.category.Role;
import kr.nadeuli.category.TradeType;
import kr.nadeuli.dto.AuthDTO;
import kr.nadeuli.dto.BlockDTO;
import kr.nadeuli.dto.GpsDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.NadeuliDeliveryDTO;
import kr.nadeuli.dto.NadeuliPayHistoryDTO;
import kr.nadeuli.dto.OriScheMemChatFavDTO;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.dto.ReportDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.mapper.MemberMapper;
import kr.nadeuli.service.auth.AuthRepository;
import kr.nadeuli.service.auth.AuthService;
import kr.nadeuli.service.jwt.AuthenticationService;
import kr.nadeuli.service.member.MemberRepository;
import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MemberApplicationTests {

  @Autowired
  MemberService memberService;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  MemberMapper memberMapper;

  @Autowired
  AuthenticationService authenticationService;

  @Autowired
  AuthService authService;

  @Autowired
  AuthRepository authRepository;

  @Autowired
  ProductService productService;

  @Value("${pageSize}")
  private int pageSize;

  @DisplayName("자체회원가입 테스트")
//  @Test
  public void testAddMember() throws Exception {
    //입력받는 값
    String nickname = "최*현";
    String cellPhone = "01034431643";

    AuthDTO authDTO = AuthDTO.builder()
        .authNumber("11111")
        .to(cellPhone)
        .build();

    GpsDTO gpsDTO = GpsDTO.builder()
        .x(127.0292881)
        .y(37.4923615)
        .build();

    // 1. 휴대폰 번호 인증 후 로그인이 가능하기때문에 SmsService의 목 객체(mock) 생성
    AuthService mockAuthService = Mockito.mock(AuthService.class);

    // 2. 인증번호 확인 메소드인 verifySms()가 항상 true를 반환하도록 설정
    Mockito.when(mockAuthService.verifySms(authDTO)).thenReturn(true);

    // 3. 인증번호 확인이 완료되었다면...
    if (mockAuthService.verifySms(authDTO)) {
      MemberDTO memberDTO = MemberDTO.builder()
          .affinity(50L)
          .cellphone(cellPhone)
          .nickname(nickname)
          .dongNe("서울특별시 강서구 공항동")
          .picture("empty.jpg")
          .nadeuliPayBalance(0L)
          .isActivate(false)
          .isNadeuliDelivery(false)
          .gu("")
          .role(Role.USER)
          .build();

      System.out.println(authenticationService.addMember(memberDTO,gpsDTO));
    } else {
      System.out.println("휴대폰 인증을 완료해야합니다..");
    }

  }

  @DisplayName("자체로그인 테스트")
  //  @Test
  public void testLogin() throws Exception {
    //입력받는 값
    String cellphone = "01086258914";

    AuthDTO authDTO = AuthDTO.builder()
        .authNumber("11111")
        .to(cellphone)
        .build();

    // 1. 휴대폰 번호 인증 후 로그인이 가능하기때문에 SmsService의 목 객체(mock) 생성
    AuthService mockAuthService = Mockito.mock(AuthService.class);

    // 2. 인증번호 확인 메소드인 verifySms()가 항상 true를 반환하도록 설정
    Mockito.when(mockAuthService.verifySms(authDTO)).thenReturn(true);

    // 3. 인증번호 확인이 완료되었다면...
    if (mockAuthService.verifySms(authDTO)) {
      System.out.println(authenticationService.login(cellphone));
    } else {
      System.out.println("존재하지 않는 회원입니다.");
    }
  }

  @DisplayName("동네설정 테스트")
//  @Test
  public void testGetDongNe() throws Exception {
    String tag = "#4FzB";

    // 실제 현재 위치 좌표는 Js의 geolocation 사용 예정
    double x = 127.0292881;
    double y = 37.4923615;

//    double x = 126.8110081;
//    double y = 37.5599801;

    GpsDTO gpsDTO = new GpsDTO();
    gpsDTO.setX(x);
    gpsDTO.setY(y);

    memberService.addDongNe(tag, gpsDTO);

  }

  @DisplayName("내프로필조회 테스트")
//  @Test
  public void testGetMember() throws Exception{
    String tag="#4FzB";
    MemberDTO memberDTO = memberService.getMember(tag);
    System.out.println(memberDTO);
  }

  @DisplayName("상대프로필조회 테스트")
//  @Test
  public void testGetOtherMember() throws Exception{
    String tag="#4FzB";
    MemberDTO memberDTO = memberService.getOtherMember(tag);
    System.out.println(memberDTO);
  }

  @DisplayName("회원목록조회 테스트")
//  @Test
  public void testGetMemberList() throws Exception{
    SearchDTO searchDTO = SearchDTO.builder()
        .currentPage(0)
        .searchKeyword("엄")
        .pageSize(pageSize)
        .build();

    List<MemberDTO> memberDTOList = memberService.getMemberList(searchDTO);
    System.out.println(memberDTOList);

  }

  @DisplayName("정지회원 등록 테스트")
//  @Test
  public void testAddBlockMember() throws Exception{
    String tag = "#4FzB";
    BlockDTO blockDTO = BlockDTO.builder()
        .id(1L)
        .blockReason("걍 띠꺼움")
        .blockEnd(LocalDateTime.now().plusDays(7))
        .blockDay(7L)
        .build();

    memberService.addBlockMember(blockDTO, tag);
    System.out.println(memberService.getMember(tag));
  }

  @DisplayName("정지회원 삭제 테스트")
//  @Test
  public void testDeleteBlockMember() throws Exception{
    String tag = "#4FzB";
    memberService.deleteBlockMember(tag);
    System.out.println(memberService.getMember(tag));
  }

  @DisplayName("회원 비활성화, 비활성화 해제 테스트")
//  @Test
  public void testHandleMemberActivate() throws Exception{
    String tag = "#4FzB";
    memberService.handleMemberActivate(tag);

  }

  @DisplayName("회원 비활성화, 비활성화 해제 테스트")
//  @Test
  public void testHandleNadeuliDelivery() throws Exception{
    String tag = "#4FzB";
    memberService.handleNadeuliDelivery(tag);

  }


  @DisplayName("회원 수정 테스트")
//  @Test
  public void testUpdateMemeber() throws Exception{
    MemberDTO memberDTO = MemberDTO.builder()
        .tag("#4FzB")
        .picture("1234.jpg")
        .nickname("롤로노아 김동헌")
        .cellphone("01088888888")
        .email("sex@gmail.com")
        .build();

    memberService.updateMember(memberDTO);
  }

  @DisplayName("즐겨찾기 추가 테스트")
//  @Test
  public void testaddFavorite() throws Exception{
    String tag = "#4FzB";
    Long productId = 6L;

    memberService.addFavorite(tag, productId);
  }

  @DisplayName("즐겨찾기 삭제 테스트")
//  @Test
  public void testDeleteFavorite() throws Exception{
    String tag = "#4FzB";
    Long productId = 5L;

    memberService.deleteFavorite(tag, productId);
  }

  @DisplayName("즐겨찾기 목록 조회 테스트")
//  @Test
  public void testGetFavoriteList() throws Exception{
    SearchDTO searchDTO = new SearchDTO();
    searchDTO.setCurrentPage(0);
    searchDTO.setPageSize(pageSize);
    searchDTO.setSearchKeyword("");
    List<OriScheMemChatFavDTO> oriScheMemChatFavDTOList = memberService.getFavoriteList("#4FzB", searchDTO);
    System.out.println(oriScheMemChatFavDTOList);

  }

  @DisplayName("친화력 툴팁 테스트")
//  @Test
  public void testGetAffinityToolTip() throws Exception {
    System.out.println(memberService.getAffinityToolTip());

  }

  @DisplayName("신고 테스트")
//  @Test
  public void testReport() throws Exception{
    MemberDTO memberDTO = memberService.getMember("#4FzB");
    ProductDTO productDTO = productService.getProduct(5L);
    ReportDTO reportDTO = ReportDTO.builder()
        .reportId(1L)
        .content("걍 개띠꺼움ddd     ")
        .reporter(memberDTO)
        .product(productDTO)
        .build();

    memberService.addReport(reportDTO);

  }

  @DisplayName("나드리페이 계산 테스트")
//  @Test
  public void testHandleNadeuliPayBalance() throws Exception{
    NadeuliPayHistoryDTO nadeuliPayHistoryDTO = NadeuliPayHistoryDTO.builder()
        .nadeuliPayHistoryId(1L)
        .tradingMoney(10000L)
//        .tradingMoney(4000L)
//        .tradeType(TradeType.CHARGE)
//        .tradeType(TradeType.PAYMENT)
        .tradeType(TradeType.WITHDRAW)
        .build();

    NadeuliDeliveryDTO nadeuliDeliveryDTO = NadeuliDeliveryDTO.builder()
        .nadeuliDeliveryId(1L)
        .deposit(10001L)
        .deliveryState(DeliveryState.CANCEL_DELIVERY)
        .deliveryState(DeliveryState.DELIVERY_ORDER)
        .build();
    String tag = "#1qZL";

//    memberService.handleNadeuliPayBalance(tag,null,nadeuliDeliveryDTO);
    memberService.handleNadeuliPayBalance(tag,nadeuliPayHistoryDTO,null,null);

  }

  @DisplayName("친화력 점수 반영 테스트")
//  @Test
  public void testUpdateAffinity() throws Exception{
    String tag = "Kv4G";
    Long affinityScore = 5L;
    Long affinityScore1 = -5L;

    memberService.updateAffinity(tag);

  }
}