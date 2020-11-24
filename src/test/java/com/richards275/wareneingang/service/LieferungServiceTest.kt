package com.richards275.wareneingang.service

import com.richards275.wareneingang.domain.CSVFehler
import com.richards275.wareneingang.domain.Lieferung
import com.richards275.wareneingang.domain.LieferungsStatus
import com.richards275.wareneingang.domain.Ware
import com.richards275.wareneingang.domain.dto.WareDtoAnFrontend
import com.richards275.wareneingang.repositories.LieferungRepository
import com.richards275.wareneingang.repositories.WareRepository
import com.richards275.wareneingang.security.UserDetailsImpl
import com.richards275.wareneingang.security.domain.ERole
import com.richards275.wareneingang.utils.TestAuthentication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.GrantedAuthority
import java.util.*

@ExtendWith(MockitoExtension::class)
class LieferungServiceTest {
  @Mock
  var lieferungRepository: LieferungRepository? = null

  @Mock
  var wareRepository: WareRepository? = null

  @Mock
  var wareService: WareService? = null
  var lieferungService: LieferungService? = null
  var lieferungMitStatusNEU = Lieferung(Date(), null, "", LieferungsStatus.NEU)
  var lieferungList: List<Lieferung?>? = null

  @BeforeEach
  fun setUp() {
    lieferungService = LieferungService(lieferungRepository!!, wareRepository!!, wareService!!)

    val lieferungMitStatusFEHLER = Lieferung(Date(), null, "", LieferungsStatus.FEHLER)
    val lieferungMitStatusBESTAETIGT = Lieferung(Date(), null, "", LieferungsStatus.BESTAETIGT)
    val lieferungMitStatusINBEARBEITUNG = Lieferung(Date(), null, "", LieferungsStatus.INBEARBEITUNG)
    val lieferungMitStatusVERARBEITET = Lieferung(Date(), null, "", LieferungsStatus.VERARBEITET)
    lieferungList = listOf(lieferungMitStatusNEU, lieferungMitStatusFEHLER, lieferungMitStatusBESTAETIGT,
        lieferungMitStatusINBEARBEITUNG, lieferungMitStatusVERARBEITET)
  }

  @Test
  fun getAlleLieferungen_Mitarbeiterin() {
    val auth: MutableCollection<out GrantedAuthority> =
        listOf(GrantedAuthority { ERole.ROLE_MITARBEITERIN.toString() }) as MutableCollection<out GrantedAuthority>
    val authentication = TestAuthentication(auth, UserDetailsImpl())

    `when`(lieferungRepository?.findAll())
        .thenReturn(lieferungList)

    assertEquals(3, lieferungService?.getAlleLieferungen(authentication)?.size)
  }

  @Test
  fun getAlleLieferungen_Lieferant() {

    val auth: MutableCollection<out GrantedAuthority> =
        listOf(GrantedAuthority { ERole.ROLE_LIEFERANT.toString() }) as MutableCollection<out GrantedAuthority>
    val authentication = TestAuthentication(auth, UserDetailsImpl())

    `when`(lieferungRepository?.findByLieferantId(anyLong()))
        .thenReturn(lieferungList as List<Lieferung>?)

    assertEquals(5, lieferungService?.getAlleLieferungen(authentication)?.size)
  }

  @Test
  fun getWareBzwCsvFehler_CsvFehler() {
    val wareDtoAnFrontendEingang = WareDtoAnFrontend("Name Ware Eingang", "", 0, 0, "")
    val csvFehler = CSVFehler(3L, "Die Fehlermeldung", "Das Feld", null)
    `when`(lieferungRepository?.findById(anyLong()))
        .thenReturn(Optional.of(Lieferung(null, null, "", null)))
    `when`(wareService?.getWareDtoList(anyLong(),
        (any(Function1::class.java) as ((Ware) -> Boolean)?)))
        .thenReturn(listOf(wareDtoAnFrontendEingang))

    val resultMap: MutableMap<String, Any?> = lieferungService?.getWareBzwCsvFehler(
        1L,
        "csvFehlerList"
    ) { listOf(csvFehler) }!!

    assertEquals(2, resultMap.keys.size)
    val resultListEingang = resultMap["wareEingangList"] as List<WareDtoAnFrontend>?
    assertEquals(1, resultListEingang!!.size)
    assertEquals("Name Ware Eingang", resultListEingang[0].name)
    val resultListCsvFehler = resultMap["csvFehlerList"] as List<CSVFehler>?
    assertEquals(1, resultListCsvFehler!!.size)
    assertEquals("Die Fehlermeldung", resultListCsvFehler[0].fehlermeldung)
  }

  @Test
  fun getWareBzwCsvFehler_Ware() {
    val wareDtoAnFrontendEingang = WareDtoAnFrontend("Name Ware Eingang", "", 0, 0, "")
    val wareDtoAnFrontendBearbeitet = WareDtoAnFrontend("Name Ware Bearbeitet", "", 0, 0, "")
    `when`(lieferungRepository?.findById(anyLong()))
        .thenReturn(Optional.of(Lieferung(null, null, "", null)))
    `when`(wareService?.getWareDtoList(anyLong(),
        any(Function1::class.java) as ((Ware) -> Boolean)?))
        .thenReturn(listOf(wareDtoAnFrontendEingang))


    val resultMap: MutableMap<String, Any?>? = lieferungService?.getWareBzwCsvFehler(
        1L,
        "wareBearbeitetList"
    ) { listOf(wareDtoAnFrontendBearbeitet) }

    assertEquals(2, resultMap?.keys?.size)
    val resultListEingang = resultMap?.get("wareEingangList") as List<WareDtoAnFrontend>?
    assertEquals(1, resultListEingang!!.size)
    assertEquals("Name Ware Eingang", resultListEingang[0].name)
    val resultListBearbeitet = resultMap?.get("wareBearbeitetList") as List<WareDtoAnFrontend>?
    assertEquals(1, resultListBearbeitet!!.size)
    assertEquals("Name Ware Bearbeitet", resultListBearbeitet[0].name)
  }

  @Test
  fun verarbeite_should_filter() {
    `when`(lieferungRepository?.findById(anyLong()))
        .thenReturn(Optional.of(lieferungMitStatusNEU))

    lieferungService?.verarbeite(1L, LieferungsStatus.INBEARBEITUNG) { obj: Lieferung -> obj.darfBearbeitetWerden() }

    verify(lieferungRepository, times(1))?.findById(anyLong())
    verify(lieferungRepository, times(0))?.save(any(Lieferung::class.java))
  }

  @Test
  fun delete_should_filter() {
    `when`(lieferungRepository?.findById(anyLong()))
        .thenReturn(Optional.of(lieferungMitStatusNEU))

    lieferungService?.delete(1L)

    verify(lieferungRepository, times(1))?.findById(anyLong())
    verify(wareRepository, times(0))?.findByLieferung_Id(anyLong())
    verify(lieferungRepository, times(0))?.deleteById(anyLong())
  }
}