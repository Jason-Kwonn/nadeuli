package kr.nadeuli.service.member.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kr.nadeuli.category.DeliveryState;
import kr.nadeuli.category.TradeType;
import kr.nadeuli.dto.AddressDTO;
import kr.nadeuli.dto.BankAccountDTO;
import kr.nadeuli.dto.BlockDTO;
import kr.nadeuli.dto.GpsDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.NadeuliDeliveryDTO;
import kr.nadeuli.dto.NadeuliPayHistoryDTO;
import kr.nadeuli.dto.OriScheMemChatFavDTO;
import kr.nadeuli.dto.PortOneAccountDTO;
import kr.nadeuli.dto.ReportDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.dto.TradeReviewDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.OriScheMemChatFav;
import kr.nadeuli.entity.Product;
import kr.nadeuli.mapper.BankAccountMapper;
import kr.nadeuli.mapper.BlockMapper;
import kr.nadeuli.mapper.MemberMapper;
import kr.nadeuli.mapper.OriScheMemChatFavMapper;
import kr.nadeuli.mapper.ProductMapper;
import kr.nadeuli.mapper.ReportMapper;
import kr.nadeuli.scheduler.BlockScheduler;
import kr.nadeuli.service.member.BankAccountRepository;
import kr.nadeuli.service.member.BlockRepository;
import kr.nadeuli.service.member.MemberRepository;
import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.member.ReportRepository;
import kr.nadeuli.service.orikkiri.OriScheMenChatFavRepository;
import kr.nadeuli.service.product.ProductService;
import kr.nadeuli.service.auth.AuthService;
import kr.nadeuli.service.trade.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Transactional
@RequiredArgsConstructor
@Log4j2
@Service
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;

  private final BlockRepository blockRepository;

  private final MemberMapper memberMapper;

  private final BlockMapper blockMapper;

  private final WebClient.Builder webClientBuilder;

  private final BlockScheduler blockDayScheduler;

  private final AuthService authService;

  private final OriScheMenChatFavRepository oriScheMenChatFavRepository;

  private final OriScheMemChatFavMapper oriScheMemChatFavMapper;

  private final ProductService productService;

  private final ProductMapper productMapper;

  private final ReportRepository reportRepository;

  private final ReportMapper reportMapper;

  private final TradeService tradeService;

  private final ObjectMapper objectMapper;

  private final BankAccountRepository bankAccountRepository;

  private final BankAccountMapper bankAccountMapper;

  @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
  private String mapKey;

  @Value("${affinityTooltip}")
  private String affinityTooltip;

  @Value("${kakao.map.api-url}")
  private String mapApiUrl;

  @Value("${iamport.api.key}")
  private String IMP_KEY;

  @Value("${iamport.api.secret}")
  private String IMP_SECRET;

  private final String API_URL = "https://api.iamport.kr/";

  // 새로운 태그 생성 및 유효성 검사 메소드
  public String addTag() {
    String tag;

    do {
      // 랜덤한 네 자리 알파벳 대/소문자 또는 숫자 생성
      tag = generateRandomTag();
    } while (!isTagUnique(tag)); // 태그가 유니크한지 검사

    return tag;
  }

  // 랜덤한 네 자리 알파벳 대/소문자 또는 숫자 생성 메소드
  public String generateRandomTag() {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder tagBuilder = new StringBuilder();

    for (int i = 0; i < 4; i++) {
      int index = (int) (Math.random() * characters.length());
      tagBuilder.append(characters.charAt(index));
    }

    return tagBuilder.toString();
  }

  // 태그가 유니크한지 검사하는 메소드
  public boolean isTagUnique(String tag) {
    return memberRepository.findByTag(tag).isEmpty();
  }

  //회원 프로필 수정
  //todo 리팩토링 예정 -> record사용하여 코드중복제거
  @Override
  public MemberDTO updateMember(MemberDTO memberDTO) throws Exception {
    log.info("받은 member는{}", memberDTO);

    MemberDTO existMember = getMember(memberDTO.getTag());

    // Null이 아닌 값만 수정
    if (memberDTO.getPicture() != null) {
      log.info("받은 Picture는{}", memberDTO.getPicture());
      existMember.setPicture(memberDTO.getPicture());
    }
    if (memberDTO.getNickname() != null) {
      log.info("받은 Nickname는{}", memberDTO.getNickname());
      existMember.setNickname(memberDTO.getNickname());
    }
    if (memberDTO.getDongNe() != null) {
      log.info("받은 DongNe는{}", memberDTO.getDongNe());
      existMember.setDongNe(memberDTO.getDongNe());
    }
    if (memberDTO.getGu() != null) {
      log.info("받은 Gu는{}", memberDTO.getGu());
      existMember.setGu(memberDTO.getGu());
    }
    if (memberDTO.getNadeuliPayBalance() != null) {
      log.info("받은 NadeuliPayBalance는{}", memberDTO.getNadeuliPayBalance());
      existMember.setNadeuliPayBalance(memberDTO.getNadeuliPayBalance());
    }
    memberRepository.save(memberMapper.memberDTOToMember(existMember));

    return existMember;
  }

  @Override
  public MemberDTO updateTo(MemberDTO memberDTO) throws Exception {
    log.info("받은 member는 {}", memberDTO);

    MemberDTO existMember = getMember(memberDTO.getTag());

    // Null이 아니고 값이 다를 경우에만 수정
    if (memberDTO.getCellphone() != null && !memberDTO.getCellphone()
        .equals(existMember.getCellphone())) {
      log.info("받은 Cellphone는 {}", memberDTO.getCellphone());
      existMember.setCellphone(memberDTO.getCellphone());
    }
    if (memberDTO.getEmail() != null && !memberDTO.getEmail().equals(existMember.getEmail())) {
      log.info("받은 Email는 {}", memberDTO.getEmail());
      existMember.setEmail(memberDTO.getEmail());
    }

    memberRepository.save(memberMapper.memberDTOToMember(existMember));

    return existMember;
  }

  //내 프로필 조회
  @Override
  public MemberDTO getMember(String tag) throws Exception {
    return memberRepository.findByTag(tag).map(memberMapper::memberToMemberDTO).orElse(null);

  }

  //상대 프로필 조회
  @Override
  public MemberDTO getOtherMember(String tag) throws Exception {
    log.info("받은태그는{}", tag);
    //상대프로필은 프로필사진, 닉네임, 태그, 동네, 친화력이있어야한다.
    Member member = memberRepository.findByTag(tag).orElse(null);

    if (member != null) {

      MemberDTO memberDTO = MemberDTO.builder()
          .picture(member.getPicture())
          .nickname(member.getNickname())
          .tag(member.getTag())
          .dongNe(member.getDongNe())
          .affinity(member.getAffinity())
          .build();

      log.info("반환하는 셀러는{}", memberDTO);

      return memberDTO;
    }
    return null;
  }

  //회원 목록 조회
  @Override
  public List<MemberDTO> getMemberList(SearchDTO searchDTO) throws Exception {
    log.info("SearchDTO는 {} ", searchDTO);
    Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize());
    Page<Member> memberPage;
    if (searchDTO.getSearchKeyword() == null || searchDTO.getSearchKeyword().isEmpty()) {
      memberPage = memberRepository.findAll(pageable);
    } else {
      memberPage = memberRepository.findByNicknameContainingOrTagContaining(
          searchDTO.getSearchKeyword(), searchDTO.getSearchKeyword(), pageable);
    }
    log.info(memberPage);
    return memberPage.map(memberMapper::memberToMemberDTO).toList();
  }

  //회원가입시 동네 설정, 동네 수정
  @Override
  public AddressDTO addDongNe(String tag, GpsDTO gpsDTO) throws Exception {
    WebClient webClient = webClientBuilder.baseUrl(mapApiUrl)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + mapKey)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();

    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("x", String.valueOf(gpsDTO.getX()));
    queryParams.put("y", String.valueOf(gpsDTO.getY()));

    // Map을 MultiValueMap으로 변환
    MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
    multiValueMap.setAll(queryParams);

    String kakaoMapAddress = webClient.get()
        .uri(uriBuilder -> uriBuilder.queryParams(multiValueMap).build())
        .retrieve()
        .bodyToMono(String.class)
        .block();

    // Jackson ObjectMapper를 사용하여 JSON을 AddressDTO 객체로 변환
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    AddressDTO addressDTO = objectMapper.readValue(kakaoMapAddress, AddressDTO.class);

    log.info("WebClient를 이용한 주소 검색 결과: {}", addressDTO);

    // 존재하는 회원의 DongNe 업데이트
    MemberDTO existingMember = getMember(tag);
    if (existingMember != null) {
      existingMember.setDongNe(addressDTO.getDocuments().get(1).getAddressName());
      existingMember.setGu(addressDTO.getDocuments().get(1).getRegion2depthName());
      updateMember(existingMember);
    }

//    return addressDTO.getDocuments().get(1).getAddressName() + addressDTO.getDocuments().get(1).getRegion2depthName();
    return addressDTO;
  }

  //회원 정지
  @Override
  public void addBlockMember(BlockDTO blockDTO, String tag) throws Exception {
    log.info("받은 BlockDTO는: {}", blockDTO);
    log.info("받은 tag는: {}", tag);

    MemberDTO memberDTO = getMember(tag);

    // 정지기간을 현재시간에 정지일수 추가
    LocalDateTime blockEnd = LocalDateTime.now().plusDays(blockDTO.getBlockDay());
    blockDTO.setBlockEnd(blockEnd);

    // BlockEnd가 있는 경우에만 정지하지 않도록 조건 추가
    if (memberDTO.getBlockEnd() == null) {
      // Block의 값을 가져와서 지속적인 Join 방지
      memberDTO.setBlockDay(blockDTO.getBlockDay());
      memberDTO.setBlockEnd(blockEnd);
      memberDTO.setBlockReason(blockDTO.getBlockReason());

      memberRepository.save(memberMapper.memberDTOToMember(memberDTO));

      blockDTO.setBlockMember(memberDTO);

      blockRepository.save(blockMapper.blockDTOToBlock(blockDTO));

      blockDayScheduler.startBlockDayScheduler(tag);
    } else {
      log.info("이미 정지된 회원입니다.");
    }
  }

  //회원 정지 해제
  @Override
  public void deleteBlockMember(String tag) throws Exception {

    MemberDTO memberDTO = getMember(tag);

    memberDTO.setBlockDay(null);
    memberDTO.setBlockEnd(null);
    memberDTO.setBlockReason(null);
    memberRepository.save(memberMapper.memberDTOToMember(memberDTO));
    blockRepository.deleteByBlockMemberTag(tag);

  }

  //회원 비활성화 및 활성화
  @Override
  public void handleMemberActivate(String tag) throws Exception {
    log.info("tag는 {}", tag);

    MemberDTO memberDTO = getMember(tag);

    // 현재 상태가 true이면 false로, false이면 true로 변경
    memberDTO.setActivate(!getMember(tag).isActivate());

    memberRepository.save(memberMapper.memberDTOToMember(memberDTO));

  }

  //나드리부름 알림 비활성화 및 활성화
  @Override
  public void handleNadeuliDelivery(String tag) throws Exception {
    log.info("tag는 {}", tag);

    MemberDTO memberDTO = getMember(tag);

    // 현재 상태가 true이면 false로, false이면 true로 변경
    memberDTO.setNadeuliDelivery(!getMember(tag).isNadeuliDelivery());

    memberRepository.save(memberMapper.memberDTOToMember(memberDTO));

  }

  //상품 즐겨찾기 추가
  @Override
  public void addFavorite(String tag, Long productId) throws Exception {
    Member member = memberMapper.memberDTOToMember(getMember(tag));
    Product product = productMapper.productDTOToProduct(productService.getProduct(productId, null));

    // 이미 등록되어 있는지 확인
    if (oriScheMenChatFavRepository.existsByMemberAndProduct(member, product)) {
      throw new Exception("이미 등록된 즐겨찾기입니다.");

    }

    OriScheMemChatFav oriScheMemChatFav = OriScheMemChatFav.builder()
        .product(product)
        .member(member)
        .build();

    oriScheMenChatFavRepository.save(oriScheMemChatFav);
  }

  //상품 즐겨찾기 해제
  @Override
  public void deleteFavorite(String tag, Long productId) throws Exception {
    Member member = memberMapper.memberDTOToMember(getMember(tag));
    Product product = productMapper.productDTOToProduct(productService.getProduct(productId, null));

    oriScheMenChatFavRepository.deleteByMemberAndProduct(member, product);
  }

  //상품 즐겨찾기 목록 조회
  @Override
  public List<OriScheMemChatFavDTO> getFavoriteList(String tag, SearchDTO searchDTO)
      throws Exception {
    Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize());
    return oriScheMenChatFavRepository
        .findByMemberTagAndOrikkiriScheduleIsNullAndOrikkiriIsNullAndAnsQuestionsIsNullAndProductNotNull(tag,pageable)
        .map(oriScheMemChatFavMapper::oriScheMemChatFavToOriScheMemChatFavDTO)
        .toList();
  }

  //신고
  @Override
  public void addReport(ReportDTO reportDTO) throws Exception {
    log.info("ReportDTO는 {}", reportDTO);
    reportRepository.save(reportMapper.reportDTOToReport(reportDTO));
  }

  //친화력 툴팁
  @Override
  public String getAffinityToolTip() throws Exception {
    return affinityTooltip;
  }

  //나드리페이 입금
  //nadeuliPayCharge
  @Override
  public boolean handleNadeuliPayBalance(String tag, NadeuliPayHistoryDTO nadeuliPayHistoryDTO,
      NadeuliDeliveryDTO nadeuliDeliveryDTO, Long beforeDeposit) throws Exception {
    log.info("전달받은 tag와 nadeuliPayHistoryDTO와 nadeuliDeliveryDTO는 {},{},{}", tag,
             nadeuliPayHistoryDTO, nadeuliDeliveryDTO);
    boolean handleResult = true;
    //1. 전달받은 tag로 현재 멤버 잔액 조회
    MemberDTO memberDTO = getMember(tag);
    log.info("조회한 memberDTO는 {}", memberDTO);

    //2. 멤버 잔액 변수에 저장
    Long memberNadeuliPayBalance = memberDTO.getNadeuliPayBalance();

    //3. 나드리페이거래내역DTO가 null이아닐떄 거래타입을 가져옴
    TradeType tradeType =
        (nadeuliPayHistoryDTO != null) ? nadeuliPayHistoryDTO.getTradeType() : null;
    //4. 나드리부름DTO가 null이아닐떄 부름상태를 가져옴
    DeliveryState deliveryState =
        (nadeuliDeliveryDTO != null) ? nadeuliDeliveryDTO.getDeliveryState() : null;

    //5. 나드리페이 입금, 출금이 참이면 beforeDeposit 을 +, 이후 새 deposit 금액을 현재 잔액에서 -
    if (withdrawAndDepositNadeuliPayBalance(beforeDeposit)) {

      Long afterDeposit = getHandleMoney(nadeuliPayHistoryDTO, nadeuliDeliveryDTO);
      Long result = memberNadeuliPayBalance + beforeDeposit - afterDeposit;
      // 잔액 부족 예외 처리
      if (result < 0) {
        handleResult = false;
        log.error("잔액이 부족합니다.");
      } else {
        // 나드리페이 계산결과를 잔액에 set
        memberDTO.setNadeuliPayBalance(result);
      }

    }

    //6. 나드리페이 입금이 참이면 현재잔액에 +
    else if (depositNadeuliPayBalance(tradeType, deliveryState)) {
      Long tradingMoney = getHandleMoney(nadeuliPayHistoryDTO, nadeuliDeliveryDTO);
      memberDTO.setNadeuliPayBalance(memberNadeuliPayBalance + tradingMoney);
    }

    //7. 나드리페이 출금이 참이면 현재잔액에 -
    else if (withdrawNadeuliPayBalance(tradeType, deliveryState)) {
      Long tradingMoney = getHandleMoney(nadeuliPayHistoryDTO, nadeuliDeliveryDTO);
      Long result = memberNadeuliPayBalance - tradingMoney;

      // 잔액 부족 예외 처리
      if (result < 0) {
        handleResult = false;
        log.error("잔액이 부족합니다.");

      } else {
        // 나드리페이 계산결과를 잔액에 set
        memberDTO.setNadeuliPayBalance(result);
      }

    }
    //8. 멤버 잔액 업데이트
    updateMember(memberDTO);
    return handleResult;
  }

  // DTO별로 null체크해서 계산할 금액 가져오기
  private Long getHandleMoney(NadeuliPayHistoryDTO nadeuliPayHistoryDTO,
      NadeuliDeliveryDTO nadeuliDeliveryDTO) {
    if (nadeuliPayHistoryDTO != null) {
      return nadeuliPayHistoryDTO.getTradingMoney();
    } else if (nadeuliDeliveryDTO != null) {
      return nadeuliDeliveryDTO.getDeposit();
    }
    return 0L;
  }

  //나드리페이 입금일 때 참
  private boolean depositNadeuliPayBalance(TradeType tradeType, DeliveryState deliveryState) {
    return tradeType == TradeType.CHARGE ||
        (deliveryState != null && (deliveryState == DeliveryState.CANCEL_ORDER ||
            deliveryState == DeliveryState.CANCEL_DELIVERY ||
            deliveryState == DeliveryState.COMPLETE_DELIVERY));
  }

  //나드리페이 출금일 때 참
  private boolean withdrawNadeuliPayBalance(TradeType tradeType, DeliveryState deliveryState) {
    return (tradeType == TradeType.PAYMENT || tradeType == TradeType.WITHDRAW) ||
        (deliveryState != null && deliveryState == DeliveryState.DELIVERY_ORDER);
  }

  // 이전 보증금이 null 이 아닐 때 참
  private boolean withdrawAndDepositNadeuliPayBalance(Long beforeDeposit) {
    return beforeDeposit != null;
  }

  //거래후기 친화력점수 반영
  @Override
  public void updateAffinity(String tag) throws Exception {
    MemberDTO memberDTO = getMember(tag);
    //1. getTradeReviewList에 넘겨줄 검색조건
    SearchDTO searchDTO = SearchDTO.builder()
        .currentPage(0)
        .pageSize(Integer.MAX_VALUE)
        .build();

    //2. 회원 태그에 해당하는 거래후기 전부를 가지고옴
    List<TradeReviewDTO> tradeReviewDTOList = tradeService.getTradeReviewList(tag, searchDTO);

    //3. 친화력 점수만 List로 반영
    List<Long> affinityScoreList = new ArrayList<>(tradeReviewDTOList.stream()
                                                       .map(TradeReviewDTO::getAffinityScore)
                                                       .toList());
    //3-1. 친화력점수에 회원의 기존 친화력 추가

    affinityScoreList.add(50L);

    //4. 정수로 올림된 평균 계산
    //4-1. affinityScores를 전부 Long에서 Double로 바꾸고 갯수로 평균을 구한다.
    //4-2. Long에서 double로 바꾸는 이유는 평균계산을 간단하게해주는
    //     DoubleStream을 사용하기위해서다.
    Long averageAffinity = (long) Math.ceil(affinityScoreList.stream()
                                                .mapToDouble(Long::valueOf)
                                                .average()
                                                .orElse(0.0));

    //5. 친화력 설정
    memberDTO.setAffinity(averageAffinity);

//    log.info("거래후기의 친화력 점수: {}", affinityScoreList);
//    log.info("평균 친화력: {}", averageAffinity);
//    log.info("이전 친화력: {}", memberDTO.getAffinity());
//    log.info("업데이트된 친화력: {}", memberDTO.getAffinity());

    memberRepository.save(memberMapper.memberDTOToMember(memberDTO));
  }

  //계정 찾기
  @Override
  public boolean findAccount(String email) throws Exception {
    log.info("email는 {}", email);

    // 이메일로 회원을 찾습니다.
    MemberDTO memberDTO = memberMapper.memberToMemberDTO(
        memberRepository.findByEmail(email).orElse(null));

    // 찾은 회원이 존재하면 true, 없으면 false를 반환합니다.
    return memberDTO != null;
  }

  @Override
  public boolean updateCellphone(MemberDTO memberDTO) throws Exception {
    log.info("memberDTO는 {}", memberDTO);

    // 이메일로 회원을 찾습니다.
    MemberDTO existMemberDTO = memberMapper.memberToMemberDTO(
        memberRepository.findByEmail(memberDTO.getEmail()).orElse(null));

    // Null이 아니고 값이 다를 경우에만 수정
    if (memberDTO.getCellphone() != null && !memberDTO.getCellphone()
        .equals(existMemberDTO.getCellphone())) {
      log.info("받은 Cellphone는 {}", memberDTO.getCellphone());
      existMemberDTO.setCellphone(memberDTO.getCellphone());
      memberRepository.save(memberMapper.memberDTOToMember(existMemberDTO));
      return true;
    }
    return false;
  }

  @Override
  public String getPortOneToken() throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    Map<String, String> json =  Map.of("imp_key", IMP_KEY, "imp_secret", IMP_SECRET);
    String requestBody = objectMapper.writeValueAsString(json);
    // HttpEntity를 사용하여 바디에 데이터를 담음
    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

    RestTemplate restTemplate = new RestTemplate();

    // POST 요청 보내기
    ResponseEntity<String> response = restTemplate.postForEntity(API_URL +"users/getToken", requestEntity, String.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      System.out.println("Error: " + response.getStatusCode());
    }
    String responseBody = response.getBody();
    JsonNode rootNode = objectMapper.readTree(responseBody);

    JsonNode accessTokenNode = rootNode.path("response").path("access_token");
    String accessToken = accessTokenNode.asText();
    return accessToken;
  }

  @Override
  public String getPortOneAccountName(String accessToken, String account, String code) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(API_URL +"vbanks/holder?bank_code=" + code + "&bank_num=" + account, HttpMethod.GET, requestEntity , String.class);
    if (!response.getStatusCode().is2xxSuccessful()) {
      System.out.println("Error: " + response.getStatusCode());
    }
    String responseBody = response.getBody();
    JsonNode rootNode = objectMapper.readTree(responseBody);

    JsonNode accessTokenNode = rootNode.path("response").path("bank_holder");
    String name = accessTokenNode.asText();
    return name;
  }

  @Override
  public boolean checkPortOneAccountName(String name, String account, String code) throws JsonProcessingException {
    String accessToken = this.getPortOneToken();
    return name.equals(this.getPortOneAccountName(accessToken, account, code));
  }

  @Override
  public void addBankAccount(PortOneAccountDTO portOneAccountDTO) throws Exception{
    MemberDTO memberDTO = getMember(portOneAccountDTO.getTag());

    memberDTO.setBankAccountNum(portOneAccountDTO.getAccountNum());
    memberDTO.setBankName(portOneAccountDTO.getCode());

    BankAccountDTO bankAccountDTO = BankAccountDTO.builder()
        .bankAccountNum(portOneAccountDTO.getAccountNum())
        .bankName(portOneAccountDTO.getCode())
        .build();

//    updateMember(memberDTO);
    memberRepository.save(memberMapper.memberDTOToMember(memberDTO));
//    bankAccountRepository.save(bankAccountMapper.bankAccountDTOToBankAccount(bankAccountDTO));
  }

  @Override
  public MemberDTO deleteBankAccount(MemberDTO memberDTO) throws Exception{
    MemberDTO existMemberDTO = getMember(memberDTO.getTag());

    existMemberDTO.setBankAccountNum(null);
    existMemberDTO.setBankName(null);
    log.info(existMemberDTO);
    return memberMapper.memberToMemberDTO(memberRepository.save(memberMapper.memberDTOToMember(existMemberDTO)));
  }
}

