package com.richards275.wareneingang.repositories

import com.richards275.wareneingang.domain.CSVFehler
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CSVFehlerRepository : JpaRepository<CSVFehler, Long> {
  fun findByLieferung_Id(id: Long): List<CSVFehler>?
}