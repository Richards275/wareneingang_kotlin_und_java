package com.richards275.wareneingang.service

import com.richards275.wareneingang.domain.*
import com.richards275.wareneingang.domain.dto.WareDtoVonFrontend
import com.richards275.wareneingang.repositories.WareRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockitoExtension::class)
class WareServiceTest {

  @Mock
  var wareRepository: WareRepository? = null

  var wareService: WareService? = null

  @BeforeEach
  fun setUp() {
    wareService = WareService(wareRepository!!)
  }

  @Test
  fun verschiebe_should_filter() {
    val lieferung = Lieferung(Date(), Lieferant(), "gerne DWP", LieferungsStatus.NEU)
    val ware = Ware("Kakao", "4422", 42, BigDecimal("22.22"), "fair", lieferung)
    val wareDtoVonFrontend =
        WareDtoVonFrontend("", "", 0, "", Zustand.NEU, 0, 0)

    `when`(wareRepository?.findByNameAndNummerAndLieferung_Id(anyString(), anyString(), anyLong()))
        .thenReturn(Optional.of(ware))

    wareService!!.verschiebe(wareDtoVonFrontend) { it }

    verify(wareRepository, times(1))
        ?.findByNameAndNummerAndLieferung_Id(anyString(), anyString(), anyLong())
    verify(wareRepository, times(0))
        ?.save(any(Ware::class.java))
  }
}