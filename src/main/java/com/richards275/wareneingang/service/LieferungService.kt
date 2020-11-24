package com.richards275.wareneingang.service

import com.richards275.wareneingang.domain.AnzeigeFrontend
import com.richards275.wareneingang.domain.Lieferung
import com.richards275.wareneingang.domain.LieferungsStatus
import com.richards275.wareneingang.repositories.LieferungRepository
import com.richards275.wareneingang.repositories.WareRepository
import com.richards275.wareneingang.security.UserDetailsImpl
import com.richards275.wareneingang.security.domain.ERole
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function

@Service
open class LieferungService(
    private val lieferungRepository: LieferungRepository,
    private val wareRepository: WareRepository,
    private val wareService: WareService
) {
  open fun getAlleLieferungen(authentication: Authentication): List<Lieferung>? {
    val isMitarbeiterin = authentication
        .authorities
        .map { obj: GrantedAuthority -> obj.authority }
        .any { auth: String -> auth == ERole.ROLE_MITARBEITERIN.toString() }

    val isLieferant = authentication
        .authorities
        .map { obj: GrantedAuthority -> obj.authority }
        .any { auth: String -> auth == ERole.ROLE_LIEFERANT.toString() }

    val userDetails = authentication.principal as UserDetailsImpl

    val lieferungMitarbeiterin = listOf(
        LieferungsStatus.BESTAETIGT, LieferungsStatus.INBEARBEITUNG, LieferungsStatus.VERARBEITET
    )

    val lieferungList: MutableList<Lieferung> = mutableListOf()

    if (isMitarbeiterin) {
      lieferungList.addAll(
          lieferungRepository
              .findAll()
              .filter { lieferung -> lieferungMitarbeiterin.contains(lieferung.lieferungsStatus) }
      )
    }

    if (isLieferant) {
      lieferungList.addAll(
          lieferungRepository
              .findByLieferantId(userDetails.getLieferantId())!!
      )
    }

    return lieferungList
        .map { lieferung: Lieferung ->
          lieferung.csvFehlerList!!.clear()
          lieferung.wareSet.clear()
          lieferung
        }
  }

  open fun <T> getWareBzwCsvFehler(lieferungId: Long, name: String,
                                   getElemente: Function<Long, List<T>?>?
  ): MutableMap<String, Any?> where T : AnzeigeFrontend {
    val response: MutableMap<String, Any?> = HashMap()
    val lieferung: Lieferung? = lieferungRepository.findById(lieferungId).orElse(null);
    if (lieferung != null) {
      response["wareEingangList"] = wareService.getWareDtoList(lieferungId) { !it.istInSpalteBearbeitet() }
      response[name] = getElemente?.apply(lieferung.id)
    }
    return response
  }

  @JvmOverloads
  open fun verarbeite(id: Long, lieferungsStatus: LieferungsStatus?,
                      filterLieferung: ((Lieferung) -> Boolean)?
  ): Lieferung? {
    // und nun einmal mit sequence
    return sequenceOf(lieferungRepository.findById(id).orElse(null)) // Optional zu Nullable
        .filterNotNull()
        //.filter(filterLieferung) //  wegen Mocks des Tests nicht möglich, NPE
        .filter { lieferung -> filterLieferung?.invoke(lieferung)!! }
        .map {
          it.lieferungsStatus = lieferungsStatus
          it
        }
        .map { lieferungRepository.save(it) }
        .map {
          it.csvFehlerList?.clear()
          it.wareSet.clear()
          it
        }
        .firstOrNull()
  }

  open fun delete(id: Long) {

    sequenceOf(lieferungRepository.findById(id).orElse(null))
        .filter { it.lieferungsStatus === LieferungsStatus.FEHLER }
        .map {
          wareRepository
              .findByLieferung_Id(it.id)
              ?.forEach { ware -> wareRepository.delete(ware) }
          lieferungRepository.deleteById(it.id)
        }
        .firstOrNull()

  }
}