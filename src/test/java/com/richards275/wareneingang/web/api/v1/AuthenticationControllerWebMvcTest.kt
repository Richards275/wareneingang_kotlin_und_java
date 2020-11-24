package com.richards275.wareneingang.web.api.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.richards275.wareneingang.security.*
import com.richards275.wareneingang.security.payload.request.SignupRequest
import com.richards275.wareneingang.service.security.UserService
import com.richards275.wareneingang.utils.TestUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*

@WebMvcTest(AuthenticationController::class)
@Import(WebSecurityConfig::class, AuthTokenFilter::class, AuthEntryPointJwt::class)
class AuthenticationControllerWebMvcTest {
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
  var userService: UserService? = null
  var signUpRequest: SignupRequest? = null

  @BeforeEach
  fun setUp() {
    mvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
        .build()
    signUpRequest = SignupRequest("name", "die Email", HashSet(), 1L)
  }

  @Test
  fun registerUser_shouldNotAllowUnauthorizedAccess() {
    mvc!!.perform(MockMvcRequestBuilders.post(TestUtils.BASE_URL + "/auth/signup",
        signUpRequest))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
  }

  @Test
  @WithMockUser(username = "user", roles = ["MITARBEITERIN"])
  fun registerUser_shouldNotAllowMitarbeitereinAccess() {
    mvc!!.perform(MockMvcRequestBuilders.post(TestUtils.BASE_URL + "/auth/signup",
        signUpRequest))
        .andExpect(MockMvcResultMatchers.status().isForbidden)
  }

  @Test
  @WithMockUser(username = "user", roles = ["LIEFERANT"])
  fun registerUser_shouldNotAllowLieferantAccess() {
    mvc!!.perform(MockMvcRequestBuilders.post(TestUtils.BASE_URL + "/auth/signup",
        signUpRequest))
        .andExpect(MockMvcResultMatchers.status().isForbidden)
  }

  @Test
  @WithMockUser(username = "admin", roles = ["ADMIN"])
  fun registerUser_shouldAllowAdminAccess() {
    mvc!!.perform(MockMvcRequestBuilders.post(TestUtils.BASE_URL + "/auth/signup")
        .accept(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper!!.writeValueAsString(signUpRequest)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest)
  }

  @Test
  fun getAll_shouldNotAllowUnauthorizedAccess() {
    mvc!!.perform(MockMvcRequestBuilders.get(TestUtils.BASE_URL + "/auth/user",
        signUpRequest))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
  }

  @Test
  @WithMockUser(username = "user", roles = ["MITARBEITERIN"])
  fun getAll_shouldNotAllowMitarbeitereinAccess() {
    mvc!!.perform(MockMvcRequestBuilders.get(TestUtils.BASE_URL + "/auth/user"))
        .andExpect(MockMvcResultMatchers.status().isForbidden)
  }

  @Test
  @WithMockUser(username = "user", roles = ["LIEFERANT"])
  fun getAll_shouldNotAllowLieferantAccess() {
    mvc!!.perform(MockMvcRequestBuilders.get(TestUtils.BASE_URL + "/auth/user"))
        .andExpect(MockMvcResultMatchers.status().isForbidden)
  }

  @Test
  @WithMockUser(username = "admin", roles = ["ADMIN"])
  fun getAll_shouldAllowAdminAccess() {
    mvc!!.perform(MockMvcRequestBuilders.get(TestUtils.BASE_URL + "/auth/user"))
        .andExpect(MockMvcResultMatchers.status().isOk)
  }
}