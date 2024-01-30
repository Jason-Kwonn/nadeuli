package kr.nadeuli.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminRestController {

  @PostMapping("/sayHello")
  public ResponseEntity<String> sayHello(){
    return ResponseEntity.ok("반갑습니다 관리자님");
  }

}
