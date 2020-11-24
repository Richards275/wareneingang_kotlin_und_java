package com.richards275.wareneingang.web.api.v1

import com.richards275.wareneingang.domain.dto.LieferungDto
import com.richards275.wareneingang.domain.dto.WareDtoAnFrontend
import com.richards275.wareneingang.domain.dto.WareDtoVonFrontend
import com.richards275.wareneingang.service.LieferungService
import com.richards275.wareneingang.service.WareService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
class WareController(
    private val wareService: WareService,
    private val lieferungService: LieferungService
) {

  // DEFINITIV POST! Sonst Probleme im Frontend bei Suchfunktion
  // kein leakage der id erwünscht, erkenne an name und nummer
  // und lieferung_id

  @PostMapping("/ware/allezulieferung")
  @ResponseStatus(HttpStatus.OK)
  fun getAllZuLieferungId(@RequestBody lieferungDto: LieferungDto): MutableMap<String, Any?> {
    return lieferungService.getWareBzwCsvFehler(
        lieferungDto.lieferungId,
        "wareBearbeitetList"
    ) { lieferungId: Long -> wareService.getWareDtoList(lieferungId) { ware -> ware.istInSpalteBearbeitet() } }
  }

  @PostMapping("/ware/geliefert")
  @ResponseStatus(HttpStatus.OK)
  fun verschiebeAusEingangInBearbeitet(
      @RequestBody wareDtoVonFrontend: WareDtoVonFrontend): WareDtoAnFrontend? {
    return wareService.verschiebe(wareDtoVonFrontend)
    { it.editOderVerschiebeAusEingangWare(wareDtoVonFrontend) }
  }

  @PostMapping("/ware/zubearbeiten")
  @ResponseStatus(HttpStatus.OK)
  fun verschiebeAusBearbeitetInEingang(
      @RequestBody wareDtoVonFrontend: WareDtoVonFrontend): WareDtoAnFrontend? {
    println("wareDtoVonFrontend")
    println(wareDtoVonFrontend)
    return wareService.verschiebe(wareDtoVonFrontend)
    { it.verschiebeAusBearbeitetInEingang() }
  }
}