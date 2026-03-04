package net.bjmsw.uvm.util;

import net.bjmsw.uvm.VisitorManager;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.io.File;
import java.nio.file.Files;

public class MailUtil {

    public static void sendVisitorInvite(String emailAddress, String firstName, String lastName, String eventName, byte[] pkpassBytes, File qrImage, String htmlBody) {

        String host = VisitorManager.getSettings().get("smtpHost");
        String portStr = VisitorManager.getSettings().get("smtpPort");
        String user = VisitorManager.getSettings().get("smtpUser");
        String pass = VisitorManager.getSettings().get("smtpPass");
        String fromName = VisitorManager.getSettings().get("smtpFromName");

        if (host == null || user == null || pass == null || host.isBlank()) {
            System.err.println("[MailService] SMTP settings are missing. Skipping email to " + emailAddress);
            return;
        }

        int port = portStr != null && !portStr.isBlank() ? Integer.parseInt(portStr) : 587;

        Email email = EmailBuilder.startingBlank()
                .from(fromName != null && !fromName.isBlank() ? fromName : "Visitor Manager", user)
                .to(firstName + " " + lastName, emailAddress)
                .withSubject("Your Access Pass: " + eventName)

                .withHTMLText(htmlBody)


                .withEmbeddedImage("qr_image", fileToByteArray(qrImage), "image/png")

                .withAttachment("AccessPass.pkpass", pkpassBytes, "application/vnd.apple.pkpass")
                .buildEmail();

        TransportStrategy strategy = (port == 465) ? TransportStrategy.SMTPS : TransportStrategy.SMTP_TLS;

        try (Mailer mailer = MailerBuilder
                .withSMTPServer(host, port, user, pass)
                .withTransportStrategy(strategy)
                .buildMailer()) {

            mailer.sendMail(email, true);
            System.out.println("[MailService] Sending invite to: " + emailAddress);
        } catch (Exception e) {
            System.err.println("[MailService] Failed to send invite to: " + emailAddress + " - " + e.getMessage());
        }


    }

    private static byte[] fileToByteArray(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            System.err.println("[MailService] Failed to read file: " + file.getName() + " - " + e.getMessage());
            return new byte[0];
        }
    }

}
