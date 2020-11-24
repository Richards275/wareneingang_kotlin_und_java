package com.richards275.wareneingang.security.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String username;
  private String email;
  private List<String> roles;
  private long lieferantId;


  public JwtResponse(String accessToken, Long id, String username, String email,
                     long lieferantId, List<String> roles) {
    this.token = accessToken;
    this.id = id;
    this.username = username;
    this.email = email;
    this.lieferantId = lieferantId;
    this.roles = roles;
  }

}
