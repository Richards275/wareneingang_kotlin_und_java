package com.richards275.wareneingang.web.api.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.richards275.wareneingang.security.*
import com.richards275.wareneingang.service.LieferantService
import com.richards275.wareneingang.utils.TestUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@WebMvcTest(LieferantController::class)
@Import(WebSecurityConfig::class, AuthTokenFilter::class, AuthEntryPointJwt::class)
class LieferantControllerWebMvcTest {
  private var mvc: MockMvc? = null

  @Autowired
  private val webApplicationContext: WebApplicationContext? = null

  @MockBean
  var userDetailsService: UserDetailsServiceImpl? = null

  @MockBean
  var passwordEncoder: PasswordEncoder? = null

  @MockBean
  var jwtUtils: JwtUtils? = null

  @Autowired
  var objectMapper: ObjectMapper? = null

  @MockBean
  var lieferantService: LieferantService? = null

  @BeforeEach
  fun setUp() {
    mvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
        .build()
  }

  @Test
  fun findAll_shouldNotAllowUnauthorizedAccess() {
    mvc!!.perform(MockMvcRequestBuilders.get(TestUtils.BASE_URL + "/lieferant"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
  }

  @Test
  @WithMockUser(username = "user", roles = ["LIEFERANT"])
  fun findAll_shouldNotAllowLieferantAccess() {
    mvc!!.perform(MockMvcRequestBuilders.get(TestUtils.BASE_URL + "/lieferant"))
        .andExpect(MockMvcResultMatchers.status().isForbidden)
  }

  @Test
  @WithMockUser(username = "user", roles = ["MITARBEITERIN"])
  fun findAll_shouldAllowMitarbeiterinAccess() {
    mvc!!.perform(MockMvcRequestBuilders.get(TestUtils.BASE_URL + "/lieferant"))
        .andExpect(MockMvcResultMatchers.status().isOk)
  }

  @Test
  @WithMockUser(username = "admin", roles = ["ADMIN"])
  fun findAll_shouldNotAllowAdminAccess() {
    mvc!!.perform(MockMvcRequestBuilders.get(TestUtils.BASE_URL + "/lieferant"))
        .andExpect(MockMvcResultMatchers.status().isOk)
  }

}