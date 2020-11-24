package com.richards275.wareneingang.service

import com.richards275.wareneingang.domain.Lieferant
import com.richards275.wareneingang.repositories.LieferantRepository
import com.richards275.wareneingang.security.domain.User
import com.richards275.wareneingang.service.EmailService.NEW_LINE
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.env.Environment
import org.springframework.mail.javamail.JavaMailSender
import java.util.*

@ExtendWith(MockitoExtension::class)
class EmailServiceTest {
  @Mock
  var lieferantRepository: LieferantRepository? = null

  @Mock
  var mailSender: JavaMailSender? = null

  @Mock
  var env: Environment? = null
  var emailService: EmailService? = null

  @BeforeEach
  fun setUp() {
    emailService = EmailService(mailSender, env, lieferantRepository)
  }

  @Test
  fun generateEmailBody() {
    val user = User("username", "user@user.de", 1)
    val lieferant = Lieferant("Gepa", true)
    val specificText = "specificText"
    `when`(lieferantRepository?.findById(anyLong()))
        .thenReturn(Optional.of(lieferant))
    val erwartet = "<p>Sehr geehrte Benutzerin, sehr geehrter Benutzer,</p>" + NEW_LINE +
        "<p>Sie sind in unserem Portal registriert mit Benutzernamen username und Email user@user.de für den Lieferanten Gepa.</p>" + EmailService.NEW_LINE +
        "specificText" + NEW_LINE +
        "<p>Ihr Link zum Portal ist: <a href='https://localhost:8080/login'>Zum Portal</a></p>" + NEW_LINE +
        "<p>Mit freundlichen Grüßen,</p>" + NEW_LINE +
        "Ihr Team vom Weltladen"
    val generated = emailService?.generateEmailBody(user, specificText)
    Assertions.assertEquals(erwartet, generated)
  }

  @Test
  fun getTestEmailAddress() {
    val hinterlegteEmail = "hinterlegteEmail"
    `when`(env?.getProperty(anyString()))
        .thenReturn(hinterlegteEmail)
    assertEquals(hinterlegteEmail, emailService?.testEmailAddress)
  }
}