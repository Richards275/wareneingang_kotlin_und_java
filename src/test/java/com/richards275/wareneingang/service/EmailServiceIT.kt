package com.richards275.wareneingang.service

import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import org.apache.commons.mail.util.MimeMessageParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class EmailServiceIT {
  @Autowired
  var emailService: EmailService? = null

  @JvmField
  @RegisterExtension
  val greenMail = GreenMailExtension(ServerSetupTest.SMTP_IMAP)

  @BeforeEach
  fun setUp() {
    greenMail.setUser("username", "secret")
  }

  @Test
  fun sendEmailWithHtml() {
    emailService?.sendEmailWithHtml("elpuente@fair.es", "Bessere Welt", "für die Zukunft")
    val receivedMessages = greenMail.receivedMessages

    assertEquals(1, receivedMessages.size)

    val current = receivedMessages[0]
    assertEquals("Bessere Welt", current.subject)
    assertEquals("testaddress", current.allRecipients[0].toString())

    val parser = MimeMessageParser(current)
    parser.parse()
    val htmlContent = parser.htmlContent
    assertEquals("für die Zukunft", htmlContent)
  }
}