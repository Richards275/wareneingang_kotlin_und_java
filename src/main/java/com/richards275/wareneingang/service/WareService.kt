package com.richards275.wareneingang.service

import com.richards275.wareneingang.domain.Ware
import com.richards275.wareneingang.domain.dto.WareDtoAnFrontend
import com.richards275.wareneingang.domain.dto.WareDtoVonFrontend
import com.richards275.wareneingang.repositories.WareRepository
import org.springframework.stereotype.Service
import java.util.function.Function

@Service
open class WareService(private val wareRepository: WareRepository) {

  open fun verschiebe(wareDtoVonFrontend: WareDtoVonFrontend?,
                      verarbeite: Function<Ware, Ware>?): WareDtoAnFrontend? {
    return if (wareDtoVonFrontend != null)
      wareRepository
          .findByNameAndNummerAndLieferung_Id(
              wareDtoVonFrontend.name,
              wareDtoVonFrontend.nummer,
              wareDtoVonFrontend.lieferungId
          )
          .filter() { it.lieferung?.darfBearbeitetWerden()!! }
          .map(verarbeite)
          .map { wareRepository.save(it) }
          .map { it.zuWareDtoAnFrontend() }
          .orElse(null)
    else null
  }

  open fun getWareDtoList(lieferungId: Long, filterStatus: ((Ware) -> Boolean)?):
      List<WareDtoAnFrontend>? {
    return wareRepository
        .findByLieferung_Id(lieferungId)
        ?.filter { filterStatus?.invoke(it)!! }
        ?.map { it.zuWareDtoAnFrontend() }
  }
}