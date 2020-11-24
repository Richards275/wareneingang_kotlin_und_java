package com.richards275.wareneingang.service

import com.richards275.wareneingang.domain.Lieferant
import com.richards275.wareneingang.repositories.LieferantRepository
import com.richards275.wareneingang.security.payload.request.LieferantRequest
import org.springframework.stereotype.Service

@Service
open class LieferantService(private val lieferantRepository: LieferantRepository) {
  open fun findAll(): List<Lieferant> {
    return lieferantRepository.findAll()
  }

  open fun registerLieferant(lieferantRequest: LieferantRequest?): Lieferant {
    return lieferantRepository.save(
        Lieferant(lieferantRequest?.name!!, true)
    )
  }

  open fun wechsleAktivInaktiv(id: Long): Lieferant? {
    return lieferantRepository
        .findById(id)
        .map { lieferant: Lieferant ->
          lieferant.istAktiv = !lieferant.istAktiv
          lieferantRepository.save(lieferant)
        }
        .orElse(null)
  }
}