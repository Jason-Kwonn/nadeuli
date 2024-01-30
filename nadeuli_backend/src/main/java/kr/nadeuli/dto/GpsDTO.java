package kr.nadeuli.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpsDTO {
  //1. js의 geolocation을통한 좌표를 저장하는 DTO
  double x;
  double y;
}
