package com.richards275.wareneingang.csv

import com.richards275.wareneingang.domain.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import java.io.FileInputStream
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.util.*

class CSVHelperTest {
  private val directory = "src/test/java/com/richards275/wareneingang/csv/"

  @Test
  fun hasCSVFormat_Success() {
    val multipartFile = MockMultipartFile(
        "file", "Datei.csv", "text/plain", null as ByteArray?
    )
    assertTrue(CSVHelper.hasCSVFormat(multipartFile))
  }

  @Test
  fun hasCSVFormat_Failure() {
    val multipartFile = MockMultipartFile(
        "file", "Datei.xls", "text/plain", null as ByteArray?
    )
    assertFalse(CSVHelper.hasCSVFormat(multipartFile))
  }

  @Test
  fun csvToWareSuccess() {
    val filename = "wareeingang_mitPreis.csv"
    val dateiOrt = directory + filename
    val inputStream = FileInputStream(dateiOrt)
    val multipartFile = MockMultipartFile(
        "file", filename, "text/plain", inputStream
    )
    val lieferung: Lieferung = Lieferung(Date(), Lieferant(), "gerne DWP", LieferungsStatus.FEHLER)
    CSVHelper.csvToWare(multipartFile.inputStream, lieferung)
    assertEquals(0, lieferung.csvFehlerList?.size)
    assertEquals(6, lieferung.wareSet.size)
    assertEquals(1, lieferung.wareSet
        .filter { ware: Ware -> ware.name == "Orangensaft fair" }
        .filter { ware: Ware -> ware.nummer == "54321" }
        .count()
    )
  }

  @Test
  fun csvToWareFailure() {
    val filename = "wareeingang_mitPreisFehler.csv"
    val dateiOrt = directory + filename
    val inputStream = FileInputStream(dateiOrt)
    val multipartFile = MockMultipartFile(
        "file", filename, "text/plain", inputStream
    )
    val lieferung: Lieferung = Lieferung(Date(), Lieferant(), "gerne DWP", LieferungsStatus.FEHLER)
    CSVHelper.csvToWare(multipartFile.inputStream, lieferung)
    assertEquals(3, lieferung.wareSet.size)
    assertEquals(1, lieferung.wareSet
        .filter { ware: Ware -> ware.name == "Orangensaft fair" }
        .filter { ware: Ware -> ware.nummer == "54321" }
        .count()
    )
    assertEquals(4, lieferung.csvFehlerList!!.size)
    assertEquals(4, lieferung.csvFehlerList!![0].zeile)
    assertEquals("Der Preis konnte nicht eingelesen werden.", lieferung.csvFehlerList!![0].fehlermeldung)
    assertEquals("Preis", lieferung.csvFehlerList!![0].feld)
    assertEquals(5, lieferung.csvFehlerList!![1].zeile)
    assertEquals("Der Preis konnte nicht eingelesen werden.", lieferung.csvFehlerList!![1].fehlermeldung)
    assertEquals("Preis", lieferung.csvFehlerList!![1].feld)
    assertEquals(6, lieferung.csvFehlerList!![2].zeile)
    assertEquals("In dieser Zeile konnte die Menge nicht eingelesen werden.", lieferung.csvFehlerList!![2].fehlermeldung)
    assertEquals("Menge", lieferung.csvFehlerList!![2].feld)
    assertEquals(7, lieferung.csvFehlerList!![3].zeile)
    assertEquals("Diese Zeile wurde schon in einer vorherigen Zeile gemeldet", lieferung.csvFehlerList!![3].fehlermeldung)
  }

  @Test
  fun toCSV_CSVFehler() {
    val csvFehler_1 = CSVFehler(0, "Preis falsch", "Preis", null)
    val csvFehler_2 = CSVFehler(1, "Menge falsch", "Menge", null)
    val csvFehlerList = mutableListOf(csvFehler_1, csvFehler_2)
    val byteArrayInputStream = CSVHelper.toCSV(csvFehlerList)
    val result = String(byteArrayInputStream.readAllBytes(), StandardCharsets.UTF_8)
    val stringList: List<String> = result.lines()
    assertEquals("Zeilennummer,Fehlermeldung,Feld mit Fehler", stringList[0])
    assertEquals("0,Preis falsch,Preis", stringList[1])
    assertEquals("1,Menge falsch,Menge", stringList[2])
  }

  @Test
  fun toCSV_Ware() {
    val ware_1: Ware = Ware("Kakao", "4422", 42, BigDecimal("22.22"), "fair", mengeeditiert = 12)
    val ware_2: Ware = Ware("Kaffee", "123123", 123, BigDecimal("33.33"), "bio", mengeeditiert = 0)
    val wareList = listOf(ware_1, ware_2)
    val byteArrayInputStream = CSVHelper.toCSV(wareList)
    val result = String(byteArrayInputStream.readAllBytes(), StandardCharsets.UTF_8)
    val stringList: List<String> = result.lines()
    assertEquals("Name,gemeldete Menge,gelieferte Menge,Nummer,Bemerkung", stringList[0])
    assertEquals("Kakao,42,12,4422,fair", stringList[1])
    assertEquals("Kaffee,123,0,123123,bio", stringList[2])
  }
}