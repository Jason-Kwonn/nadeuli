package kr.nadeuli.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDTO {
  //1. 카카오 맵 api JSON응답결과 매핑하는 DTO

  public ArrayList<Document> documents;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Document{
    @JsonProperty("address_name")
    public String addressName;
    @JsonProperty("region_1depth_name")
    public String region1depthName;
    @JsonProperty("region_2depth_name")
    public String region2depthName;
    @JsonProperty("region_3depth_name")
    public String region3depthName;
  }

}
