package com.example.hcmus_quickhelper.core.utils

import android.util.Log
import com.example.hcmus_quickhelper.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailUtils {

    suspend fun sendEmail(
        recipientEmail: String,
        subject: String,
        body: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val senderEmail = BuildConfig.BUSINESS_EMAIL
        val senderPassword = BuildConfig.BUSINESS_APP_PASS

        val props = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.socketFactory.port", "465")
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            put("mail.smtp.auth", "true")
            put("mail.smtp.port", "465")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, senderPassword)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(senderEmail))
                addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
                this.subject = subject
                setText(body)
            }
            Transport.send(message)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("EmailUtils", "Failed to send email", e)
            Result.failure(e)
        }
    }
}
