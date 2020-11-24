package com.richards275.wareneingang.repositories

import com.richards275.wareneingang.domain.Ware
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WareRepository : JpaRepository<Ware, Long> {
  fun findByNameAndNummerAndLieferung_Id(
      name: String, nummer: String, lieferungId: Long
  ): Optional<Ware>

  fun findByLieferung_Id(id: Long): List<Ware>?

}