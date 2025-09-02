package com.college.inventoryerp.utils;

import com.college.inventoryerp.model.IssueRecord;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class EmailService {
    // Email configuration - Update these with your SMTP server details
    private static final String SMTP_HOST = ConfigLoader.get("smtp.host");
    private static final String SMTP_PORT = ConfigLoader.get("smtp.port");
    private static final String EMAIL_USERNAME = ConfigLoader.get("email.username");
    private static final String EMAIL_PASSWORD = ConfigLoader.get("email.password");
    private static final String FROM_EMAIL = ConfigLoader.get("email.from");
    private static final String FROM_NAME = ConfigLoader.get("email.fromName");


    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Send equipment issue confirmation email
     * @param issueRecord Issue record with details
     * @param facultyEmail Faculty email address
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendIssueConfirmationEmail(IssueRecord issueRecord, String facultyEmail) {
        try {
            // Create email session
            Session session = createEmailSession();

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(facultyEmail));
            message.setSubject("Equipment Issued - " + issueRecord.getEquipmentName());

            // Create email content
            String emailContent = createIssueConfirmationContent(issueRecord);
            message.setContent(emailContent, "text/html; charset=utf-8");

            // Send email
            Transport.send(message);

            System.out.println("Issue confirmation email sent to: " + facultyEmail);
            return true;

        } catch (Exception e) {
            System.err.println("Error sending issue confirmation email: " + e.getMessage());
            return false;
        }
    }

    private static String createIssueConfirmationContent(IssueRecord issueRecord) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body>");
        content.append("<h2>Equipment Issued Successfully</h2>");
        content.append("<p>Dear ").append(issueRecord.getFacultyName()).append(",</p>");
        content.append("<p>This email confirms that the following equipment has been issued to you:</p>");
        content.append("<div style='background-color: #f9f9f9; padding: 15px; margin: 10px 0;'>");
        content.append("<h3>Equipment Details:</h3>");
        content.append("<p><strong>Equipment Name:</strong> ").append(issueRecord.getEquipmentName()).append("</p>");
//        content.append("<p><strong>DSR Number:</strong> ").append(issueRecord.getDsrNumber()).append("</p>");
        content.append("<p><strong>Issue Date:</strong> ").append(issueRecord.getIssueDate().format(DATE_FORMATTER)).append("</p>");
        content.append("<p><strong>Issued By:</strong> ").append(issueRecord.getEmployeeName()).append("</p>");
        content.append("</div>");
        content.append("<p><strong>Important Notes:</strong></p>");
        content.append("<ul>");
        content.append("<li>Please take good care of the equipment</li>");
        content.append("<li>Return the equipment by the expected return date</li>");
        content.append("<li>Report any damage or issues immediately</li>");
        content.append("</ul>");
        content.append("<p><small>This is an automated message from College Inventory ERP System.</small></p>");
        content.append("</body></html>");
        return content.toString();
    }

    private static Session createEmailSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });
    }
}
