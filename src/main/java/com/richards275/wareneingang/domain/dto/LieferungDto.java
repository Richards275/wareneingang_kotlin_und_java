package com.richards275.wareneingang.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LieferungDto extends BasisDto {
  private long lieferungId;

  @Builder
  public LieferungDto(long lieferantId, long lieferungId) {
    super(lieferantId);
    this.lieferungId = lieferungId;
  }

  // für Aufruf aus WareController.kt erforderlich
  public long getLieferungId() {
    return lieferungId;
  }
}