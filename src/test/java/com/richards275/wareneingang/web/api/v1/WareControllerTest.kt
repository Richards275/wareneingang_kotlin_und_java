package com.richards275.wareneingang.web.api.v1

import com.richards275.wareneingang.domain.AnzeigeFrontend
import com.richards275.wareneingang.domain.Ware
import com.richards275.wareneingang.domain.Zustand
import com.richards275.wareneingang.domain.dto.LieferungDto
import com.richards275.wareneingang.domain.dto.WareDtoAnFrontend
import com.richards275.wareneingang.domain.dto.WareDtoVonFrontend
import com.richards275.wareneingang.service.LieferungService
import com.richards275.wareneingang.service.WareService
import com.richards275.wareneingang.utils.TestUtils
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.`when`
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*
import java.util.function.Function

@ExtendWith(MockitoExtension::class)
open class WareControllerTest {
  @Mock
  var lieferungService: LieferungService? = null

  @Mock
  var wareService: WareService? = null

  @InjectMocks
  var wareController: WareController? = null
  var mockMvc: MockMvc? = null
  var lieferungDto: LieferungDto? = null
  var wareDtoAnFrontendZustandNEU: WareDtoAnFrontend? = null
  var wareDtoAnFrontendZustandOK: WareDtoAnFrontend? = null
  var wareDtoAnFrontendZustandTEILWEISE: WareDtoAnFrontend? = null
  var wareDtoVonFrontendZustandNEU: WareDtoVonFrontend? = null
  var wareDtoVonFrontendZustandOK: WareDtoVonFrontend? = null

  @BeforeEach
  open fun setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(wareController)
        .build()
    lieferungDto = LieferungDto(2L, 2L)
    wareDtoVonFrontendZustandOK = WareDtoVonFrontend("Kaffee", "3", 0, "",
        Zustand.OK, 1L, 1L)
    wareDtoVonFrontendZustandNEU = WareDtoVonFrontend("Orangensaft", "123", 5, "",
        Zustand.NEU, 1L, 1L)
    wareDtoAnFrontendZustandOK = WareDtoAnFrontend("Kaffee", "3", 22,
        0, "", Zustand.OK)
    wareDtoAnFrontendZustandNEU = WareDtoAnFrontend("Orangensaft", "123", 10,
        0, "", Zustand.NEU)
    wareDtoAnFrontendZustandTEILWEISE = WareDtoAnFrontend("Cashews", "1234", 100,
        0, "", Zustand.TEILWEISE_GELIEFERT)
  }

  @Test
  open fun <T> getAllZuLieferungId() where T : AnzeigeFrontend {
    val wareDtoEingangList: List<WareDtoAnFrontend?> = listOf(wareDtoAnFrontendZustandNEU)
    val wareDtoBearbeitetList: List<WareDtoAnFrontend?> = listOf(wareDtoAnFrontendZustandOK, wareDtoAnFrontendZustandTEILWEISE)
    val result: MutableMap<String, Any> = HashMap()
    result["wareEingangList"] = wareDtoEingangList
    result["wareBearbeitetList"] = wareDtoBearbeitetList

    `when`<Map<*, *>>(lieferungService?.getWareBzwCsvFehler(anyLong(), anyString(),
        any(Function::class.java) as Function<Long, List<AnzeigeFrontend>?>?))
        .thenReturn(result)

    mockMvc!!.perform(MockMvcRequestBuilders.post(TestUtils.BASE_URL + "/ware/allezulieferung")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.asJsonString(lieferungDto)))
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.jsonPath("$.wareEingangList", Matchers.hasSize<Any>(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.wareBearbeitetList", Matchers.hasSize<Any>(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.wareBearbeitetList[1].name", Matchers.equalTo("Cashews")))
  }

  @Test
  open fun verschiebeAusEingangInBearbeitet() {
    `when`(wareService?.verschiebe(any(WareDtoVonFrontend::class.java),
        any(Function::class.java) as Function<Ware, Ware>?))
        .thenReturn(wareDtoAnFrontendZustandOK)

    mockMvc!!.perform(MockMvcRequestBuilders.post(TestUtils.BASE_URL + "/ware/geliefert")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.asJsonString(wareDtoVonFrontendZustandOK)))
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo("Kaffee")))
  }

  @Test
  open fun verschiebeAusBearbeitetInEingang() {
    `when`(wareService?.verschiebe(any(WareDtoVonFrontend::class.java), any(Function::class.java) as Function<Ware, Ware>?))
        .thenReturn(wareDtoAnFrontendZustandNEU)

    mockMvc!!.perform(MockMvcRequestBuilders.post(TestUtils.BASE_URL + "/ware/zubearbeiten")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.asJsonString(wareDtoVonFrontendZustandNEU)))
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo("Orangensaft")))
  }
}