package kr.nadeuli.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kr.nadeuli.dto.BlockDTO;
import kr.nadeuli.dto.GpsDTO;
import kr.nadeuli.dto.ImageDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.OriScheMemChatFavDTO;
import kr.nadeuli.dto.PortOneAccountDTO;
import kr.nadeuli.dto.PostDTO;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.dto.ReportDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.dto.TokenDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.service.image.ImageService;
import kr.nadeuli.service.jwt.JWTService;
import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/nadeuli/member")
@RequiredArgsConstructor
@Log4j2
public class MemberRestController {

  private final MemberService memberService;

  private final JWTService jwtService;

  private final ImageService imageService;

  private final ProductService productService;

  @Value("${pageSize}")
  private int pageSize;

  @Autowired
  private ObjectMapper objectMapper;

  @PostMapping("/logout")
  public String logout(HttpServletRequest request, HttpServletResponse response) {
    log.info("/member/logout : GET");
    // SecurityContext에서 Authentication을 가져와서 로그아웃
    SecurityContextHolder.clearContext();

    // 쿠키 삭제
    Cookie cookie = new Cookie(HttpHeaders.AUTHORIZATION, null);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);

    // 헤더에서도 삭제
    response.setHeader(HttpHeaders.AUTHORIZATION, "");

    return "{\"success\": true}";
  }

  @GetMapping("/getMember/{tag}")
  public MemberDTO getMember(@PathVariable String tag) throws Exception{
    log.info("/member/getMember/{} : GET : ",tag);

    return memberService.getMember(tag);

  }

  @GetMapping("/getOtherMember/{tag}")
  public MemberDTO getOtherMember(@PathVariable String tag) throws Exception{
    log.info("/member/getOtherMember/{} : GET : ",tag);

    return memberService.getOtherMember(tag);

  }

  @PostMapping("/getMemberList")
  public List<MemberDTO> getMemberList(@RequestBody Map<String, Object> requestData) throws Exception {
    log.info("/member/getMemberList : POST : {}",requestData);
    SearchDTO searchDTO = objectMapper.convertValue(requestData.get("searchDTO"), SearchDTO.class);
    searchDTO.setPageSize(1000);
    log.info("멤버리스트는{}",memberService.getMemberList(searchDTO));
    return memberService.getMemberList(searchDTO);
  }

  @PostMapping("/updateDongNe")
  public MemberDTO updateDongNe(@RequestBody Map<String, Object> requestData) throws Exception{
    log.info("/member/updateDongNe : POST : {}", requestData);

    MemberDTO memberDTO = objectMapper.convertValue(requestData.get("memberDTO"), MemberDTO.class);
    GpsDTO gpsDTO = objectMapper.convertValue(requestData.get("gpsDTO"), GpsDTO.class);

    memberService.addDongNe(memberDTO.getTag(), gpsDTO);

    return getMember(memberDTO.getTag());
  }

  @PostMapping("/addBlockMember")
  public String addBlockMember(@RequestBody Map<String, Object> requestData) throws Exception{
    log.info("/member/addBlockMember : POST : {}", requestData);

    MemberDTO memberDTO = objectMapper.convertValue(requestData.get("memberDTO"), MemberDTO.class);
    BlockDTO blockDTO = objectMapper.convertValue(requestData.get("blockDTO"), BlockDTO.class);

    memberService.addBlockMember(blockDTO, memberDTO.getTag());

    return "{\"success\": true}";
  }

  @GetMapping("/deleteBlockMember/{tag}")
  public String deleteBlockMember(@PathVariable String tag) throws Exception{
    log.info("/member/deleteBlockMember : GET : {}", tag);

    memberService.deleteBlockMember(tag);

    return "{\"success\": true}";
  }

  @GetMapping("/handleMemberActivate/{tag}")
  public String handleMemberActivate(@PathVariable String tag) throws Exception{
    log.info("/member/handleMemberActivate : GET : {}", tag);

    memberService.handleMemberActivate(tag);

    return "{\"success\": true}";
  }

  @PostMapping("/updateMember")
  public MemberDTO updateMember(@RequestBody MemberDTO memberDTO) throws Exception{
    log.info("/member/updateMember : POST : {}", memberDTO);
    return memberService.updateMember(memberDTO);
  }

  @PostMapping("/updateTo")
  public MemberDTO updateTo(@RequestBody MemberDTO memberDTO) throws Exception {
    log.info("/member/updateTo : POST : {}", memberDTO);

    // 기존 멤버 정보 조회
    MemberDTO existMember = memberService.getMember(memberDTO.getTag());

    // 이메일 또는 휴대폰 번호가 이미 존재하는 경우
    if (existMember != null) {
      if (existMember.getEmail() != null && existMember.getEmail().equals(memberDTO.getEmail())) {
        // 이미 존재하는 이메일입니다. 경고창 띄우기 또는 예외 처리 등을 수행할 수 있습니다.
        throw new Exception("이미 존재하는 이메일입니다.");
      }

      if (existMember.getCellphone() != null && existMember.getCellphone().equals(memberDTO.getCellphone())) {
        // 이미 존재하는 휴대폰 번호입니다. 경고창 띄우기 또는 예외 처리 등을 수행할 수 있습니다.
        throw new Exception("이미 존재하는 휴대폰 번호입니다.");
      }
    }

    // 존재하지 않는 경우에만 업데이트 수행
    return memberService.updateTo(memberDTO);
  }


  @GetMapping("/addFavorite/{tag}/{productId}")
  public String addFavorite(@PathVariable String tag, @PathVariable Long productId) throws Exception{
    log.info("/member/addFavorite : GET : {},{}", tag,productId);

    memberService.addFavorite(tag,productId);

    return "{\"success\": true}";
  }

  @GetMapping("/deleteFavorite/{tag}/{productId}")
  public String deleteFavorite(@PathVariable String tag, @PathVariable Long productId) throws Exception{
    log.info("/member/deleteFavorite : GET : {},{}", tag,productId);

    memberService.deleteFavorite(tag,productId);

    return "{\"success\": true}";
  }

  @PostMapping("/getFavoriteList")
  public List<OriScheMemChatFavDTO> getFavoriteList(@RequestBody Map<String, Object> requestData) throws Exception {
    MemberDTO memberDTO = objectMapper.convertValue(requestData.get("memberDTO"), MemberDTO.class);
    SearchDTO searchDTO = objectMapper.convertValue(requestData.get("searchDTO"), SearchDTO.class);

    log.info(memberDTO);
    log.info(searchDTO);

    searchDTO.setPageSize(pageSize);
    List<OriScheMemChatFavDTO> list = memberService.getFavoriteList(memberDTO.getTag(), searchDTO);
    log.info("받은 OriScheMemChatFavDTO는 {}", list);

    // ProductDTO 객체의 productId를 사용하여 객체를 가져와서 각각의 OriScheMemChatFavDTO에 설정
    Iterator<OriScheMemChatFavDTO> iterator = list.iterator();
    while (iterator.hasNext()) {
      OriScheMemChatFavDTO favDTO = iterator.next();
      log.info("추출한 favDTO는 {}", favDTO);

      if (favDTO.getProduct() != null) {
        long productId = favDTO.getProduct().getProductId();
        ProductDTO productDTO = productService.getProduct(productId, null);

        // ProductDTO의 seller를 설정
        MemberDTO sellerDTO = memberService.getMember(productDTO.getSeller().getTag());
        productDTO.setSeller(sellerDTO);

        favDTO.setProduct(productDTO);
      } else {
        // favDTO의 Product가 null이면 리스트에서 제거
        iterator.remove();
      }
    }

    log.info(list.stream().toList());

    return list;
  }

  @PostMapping("/addReport")
  public String addReport(@RequestBody Map<String, Object> requestData) throws Exception{
    log.info("/member/addReport : POST : {}", requestData);
    ReportDTO reportDTO = objectMapper.convertValue(requestData.get("reportDTO"), ReportDTO.class);

    memberService.addReport(reportDTO);
    return "{\"success\": true}";
  }

  @GetMapping("/getAffinityToolTip")
  public String getAffinityToolTip() throws Exception{
    return memberService.getAffinityToolTip();
  }

  @GetMapping("/handleNadeuliDelivery/{tag}")
  public String handleNadeuliDelivery(@PathVariable String tag) throws Exception{
    log.info("/member/handleNadeuliDelivery : GET : {}", tag);
    memberService.handleNadeuliDelivery(tag);
    return "{\"success\": true}";
  }


  @PostMapping("/addNickname")
  public MemberDTO addNickname(@RequestBody Map<String, Object> requestData) throws Exception{
    log.info("/member/addNickname : POST : {}", requestData);

    TokenDTO tokenDTO = objectMapper.convertValue(requestData.get("tokenDTO"), TokenDTO.class);
    GpsDTO gpsDTO = objectMapper.convertValue(requestData.get("gpsDTO"), GpsDTO.class);
    MemberDTO memberDTO = objectMapper.convertValue(requestData.get("memberDTO"), MemberDTO.class);

    String tag = jwtService.extractUserName(tokenDTO.getAccessToken());
    memberService.addDongNe(tag, gpsDTO);
    MemberDTO existMemberDTO = memberService.getMember(tag);
    existMemberDTO.setNickname(memberDTO.getNickname());

    // 디버깅: memberDTO의 내용 다시 확인
    log.info("Updated existMemberDTO: {}", existMemberDTO);

    return memberService.updateMember(existMemberDTO);
  }

  @PostMapping("/getSocialMember")
  public MemberDTO getSocialMember(@RequestBody Map<String, Object> requestData) throws Exception{
    log.info("/member/getSocialMember : POST : {}", requestData);

    TokenDTO tokenDTO = objectMapper.convertValue(requestData.get("tokenDTO"), TokenDTO.class);

    String tag = jwtService.extractUserName(tokenDTO.getAccessToken());

    return memberService.getMember(tag);
  }

  @PostMapping("/updateProfile")
  public MemberDTO updateProfile(@ModelAttribute MemberDTO memberDTO,@RequestParam("image") MultipartFile image) throws Exception {
    log.info("/member/updateProfile : POST : {}", memberDTO);
    String fileName = memberDTO.getPicture();
    imageService.deleteProfile(fileName);

    // 이미지 업로드 및 저장
    imageService.addProfile(image, memberDTO);
    return getMember(memberDTO.getTag());
  }

  @GetMapping("/getPortOneToken")
  public String getPortOneToken() throws Exception{

    return memberService.getPortOneToken();
  }

  @PostMapping("/checkPortOneAccountName")
  public ResponseEntity<MemberDTO> checkPortOneAccountName(@RequestBody Map<String, Object> requestData) throws Exception {
    PortOneAccountDTO portOneAccountDTO = objectMapper.convertValue(requestData.get("portOneAccountDTO"), PortOneAccountDTO.class);
    log.info("받은 DTO는{}", portOneAccountDTO);

    boolean isAccountNameValid = memberService.checkPortOneAccountName(portOneAccountDTO.getName(), portOneAccountDTO.getAccountNum(), portOneAccountDTO.getCode());

    if (isAccountNameValid) {
      // true일 경우에만 addBankAccount 메소드 실행
      memberService.addBankAccount(portOneAccountDTO);
      MemberDTO memberDTO = memberService.getMember(portOneAccountDTO.getTag());
      return ResponseEntity.ok(memberDTO);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @PostMapping("/deleteMemberBackAccount")
  public ResponseEntity<MemberDTO> deleteBackAccount(@RequestBody Map<String, Object> requestData) throws Exception {
    MemberDTO memberDTO= objectMapper.convertValue(requestData.get("memberDTO"), MemberDTO.class);
    log.info("받은 DTO는{}", memberDTO);

      return ResponseEntity.ok(memberService.deleteBankAccount(memberDTO));

  }

}
