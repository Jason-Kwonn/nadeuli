package kr.nadeuli.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import kr.nadeuli.dto.BlockDTO;
import kr.nadeuli.dto.GpsDTO;
import kr.nadeuli.dto.ImageDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.OriScheMemChatFavDTO;
import kr.nadeuli.dto.PostDTO;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.dto.ReportDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.dto.TokenDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.service.image.ImageService;
import kr.nadeuli.service.jwt.JWTService;
import kr.nadeuli.service.member.MemberService;
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
    searchDTO.setPageSize(pageSize);
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
    memberService.updateMember(memberDTO);

    return getMember(memberDTO.getTag());
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

  @PostMapping ("/getFavoriteList")
  public List<OriScheMemChatFavDTO> getFavoriteList(@RequestParam String tag, @RequestBody SearchDTO searchDTO) throws Exception{
    log.info("/member/deleteFavorite : GET : {},{}", tag,searchDTO);
    searchDTO.setPageSize(pageSize);
    return memberService.getFavoriteList(tag,searchDTO);
  }

  @PostMapping("/addReport")
  public String addReport(@RequestBody ReportDTO reportDTO) throws Exception{
    log.info("/member/addReport : POST : {}", reportDTO);
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
    MemberDTO existMemberDTO = memberService.getMember(tag);
    memberService.addDongNe(existMemberDTO.getTag(), gpsDTO);
    existMemberDTO.setNickname(memberDTO.getNickname());

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

}
