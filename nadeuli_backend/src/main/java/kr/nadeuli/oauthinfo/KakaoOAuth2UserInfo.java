package kr.nadeuli.oauthinfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

  public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
    public String getSocialId() {
      return String.valueOf(attributes.get("id"));
    }
  }