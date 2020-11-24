package com.richards275.wareneingang.web.api.v1

import com.richards275.wareneingang.domain.Lieferant
import com.richards275.wareneingang.security.payload.request.LieferantRequest
import com.richards275.wareneingang.service.LieferantService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
class LieferantController(
    private val lieferantService: LieferantService
) {

  @GetMapping("/lieferant")
  @ResponseStatus(HttpStatus.OK)
  fun findAll(): List<Lieferant> {
    return lieferantService.findAll()
  }

  @PostMapping("/lieferant/register")
  @ResponseStatus(HttpStatus.OK)
  fun registerLieferant(@RequestBody lieferantRequest: @Valid LieferantRequest): Lieferant {
    return lieferantService.registerLieferant(lieferantRequest)
  }

  @GetMapping("/lieferant/wechsle/{id}")
  @ResponseStatus(HttpStatus.OK)
  fun wechsleAktivInaktiv(@PathVariable id: Long): Lieferant? {
    return lieferantService.wechsleAktivInaktiv(id)
  }
}