package com.richards275.wareneingang.csv

import com.richards275.wareneingang.domain.Lieferant
import com.richards275.wareneingang.domain.Lieferung
import com.richards275.wareneingang.domain.dto.CSVRequestBodyDto
import com.richards275.wareneingang.domain.dto.ResponseMessageDto
import com.richards275.wareneingang.repositories.CSVFehlerRepository
import com.richards275.wareneingang.repositories.LieferantRepository
import com.richards275.wareneingang.repositories.LieferungRepository
import com.richards275.wareneingang.repositories.WareRepository
import com.richards275.wareneingang.security.UserDetailsServiceImpl
import com.richards275.wareneingang.utils.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockMultipartFile
import java.io.FileInputStream
import java.io.IOException
import java.util.*

@ExtendWith(MockitoExtension::class)
class CSVServiceTest {
  private val directory = "src/test/java/com/richards275/wareneingang/csv/"

  @Mock
  var lieferungRepository: LieferungRepository? = null

  @Mock
  var csvFehlerRepository: CSVFehlerRepository? = null

  @Mock
  var lieferantRepository: LieferantRepository? = null

  @Mock
  var wareRepository: WareRepository? = null

  @Mock
  var userDetailsService: UserDetailsServiceImpl? = null

  var csvService: CSVService? = null
  var csvRequestBody: CSVRequestBodyDto? = null
  var multipartFile: MockMultipartFile? = null

  @BeforeEach
  fun setUp() {
    csvService = CSVService(lieferungRepository,
        csvFehlerRepository, lieferantRepository,
        wareRepository, userDetailsService)
    csvRequestBody = CSVRequestBodyDto(2L, 2L, Date(), "prima")
    multipartFile = MockMultipartFile(
        "file", "Datei.csv", "text/plain", null as ByteArray?
    )
  }

  @Test
  fun uploadFileWareEingang_Success() {
    val filename = "wareeingang_mitPreis.csv"
    val directory = "src/test/java/com/richards275/wareneingang/csv/"
    val dateiOrt = directory + filename
    val inputStream = FileInputStream(dateiOrt)
    val multipartFile = MockMultipartFile(
        "file", filename, "text/plain", inputStream
    )
    val csvRequestBodyAsString = TestUtils.asJsonString(csvRequestBody)
    `when`(lieferungRepository!!.save(any(Lieferung::class.java))).thenReturn(null)
    `when`(lieferantRepository!!.findById(any()))
        .thenReturn(
            Optional.of(Lieferant("gepa", true, 2L))
        )
    doReturn(true)
        .`when`(userDetailsService)?.checkCredentials(ArgumentMatchers.anyLong())

    val response: ResponseMessageDto = csvService!!.uploadFileWareEingang(csvRequestBodyAsString, multipartFile)
    val message = response.message

    assertEquals("Die Datei wareeingang_mitPreis.csv wurde erfolgreich hochgeladen.", message)
    verify(lieferungRepository, times(1))?.save(any(Lieferung::class.java))
  }

  @Test
  fun uploadFileWareEingang_Failure_WrongCsvRequestBody() {
    val csvRequestBodyAsString = "defekt"

    val response: ResponseMessageDto = csvService!!.uploadFileWareEingang(csvRequestBodyAsString, multipartFile)
    val message = response.message

    assertEquals("Die Datei konnte wegen falscher Parameter nicht hochgeladen werden.", message)
  }

  @Test
  fun uploadFileWareEingang_Failure_WrongCredentails() {
    val csvRequestBodyAsString = TestUtils.asJsonString(csvRequestBody)
    doReturn(false)
        .`when`(userDetailsService)?.checkCredentials(ArgumentMatchers.anyLong())

    val response: ResponseMessageDto = csvService!!.uploadFileWareEingang(csvRequestBodyAsString, multipartFile)
    val message = response.message

    assertEquals("Falsche Credentials.", message)
  }

  @Test
  fun uploadFileWareEingang_Failure_NotHasCsvFormat() {
    val multipartFile = MockMultipartFile(
        "file", "Datei.xls", "text/plain", null as ByteArray?
    )
    val csvRequestBodyAsString = TestUtils.asJsonString(csvRequestBody)
    doReturn(true)
        .`when`(userDetailsService)?.checkCredentials(ArgumentMatchers.anyLong())

    val response: ResponseMessageDto = csvService!!.uploadFileWareEingang(csvRequestBodyAsString, multipartFile)
    val message = response.message

    assertEquals("Bitte laden Sie eine csv-Datei hoch.", message)
  }

  @Test
  fun saveWareEingang_Success() {
    val filename = "wareeingang_mitPreis.csv"
    val dateiOrt = directory + filename
    val inputStream = FileInputStream(dateiOrt)
    val multipartFile = MockMultipartFile(
        "file", filename, "text/plain", inputStream
    )
    val csvRequestBody = CSVRequestBodyDto(2L, 2L, Date(), "prima")
    `when`(lieferantRepository!!.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Optional.of(Lieferant()))
    `when`(lieferungRepository!!.save(ArgumentMatchers.any(Lieferung::class.java)))
        .thenReturn(null)

    val response = csvService!!.saveWareEingang(multipartFile, csvRequestBody)

    assertEquals("Die Datei wareeingang_mitPreis.csv wurde erfolgreich hochgeladen.", response)
  }

  @Test
  @Throws(IOException::class)
  fun saveWareEingang_Failure_WrongLieferant() {
    `when`(lieferantRepository!!.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Optional.empty())

    val response = csvService!!.saveWareEingang(multipartFile, csvRequestBody)

    assertEquals("Der Lieferant konnte nicht gefunden werden.", response)
  }

  @Test
  @Throws(IOException::class)
  fun saveWareEingang_Failure_CsvFehler() {
    val filename = "wareeingang_mitPreisFehler.csv"
    val dateiOrt = directory + filename
    val inputStream = FileInputStream(dateiOrt)
    val multipartFile = MockMultipartFile(
        "file", filename, "text/plain", inputStream
    )
    val csvRequestBody = CSVRequestBodyDto(2L, 2L, Date(), "prima")

    `when`(lieferantRepository!!.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Optional.of(Lieferant()))
    `when`(lieferungRepository!!.save(ArgumentMatchers.any(Lieferung::class.java)))
        .thenReturn(null)

    val response = csvService!!.saveWareEingang(multipartFile, csvRequestBody)

    assertEquals("Es liegen Validierungsfehler vor in der Datei wareeingang_mitPreisFehler.csv", response)
  }
}