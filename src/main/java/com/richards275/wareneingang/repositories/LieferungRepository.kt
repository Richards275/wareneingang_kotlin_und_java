package com.richards275.wareneingang.repositories

import com.richards275.wareneingang.domain.Lieferung
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LieferungRepository : JpaRepository<Lieferung, Long> {
  fun findByLieferantId(id: Long): List<Lieferung>?
}