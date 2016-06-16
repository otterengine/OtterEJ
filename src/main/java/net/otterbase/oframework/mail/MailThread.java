package net.otterbase.oframework.mail;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import net.otterbase.oframework.OFContext;

public class MailThread extends Thread {

	private JavaMailSender mailSender;
	private String title;
	private String htmlString;
	private String mailAddress;
	
	public MailThread(JavaMailSender mailSender, String title, String htmlString, String mailAddress) {
		this.mailSender = mailSender;
		this.title = title;
		this.htmlString = htmlString;
		this.mailAddress = mailAddress;
	}

	@Override
	public void run() {
		super.run();

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = null;
		
		try {
			mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			mimeMessageHelper.setFrom(new InternetAddress(OFContext.getProperty("webapp.mail.display.mail"), OFContext.getProperty("webapp.mail.display.name")));
			mimeMessageHelper.setTo(new InternetAddress(mailAddress));
			mimeMessageHelper.setSubject(title);
			mimeMessageHelper.setText(htmlString, true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (mimeMessageHelper != null) this.mailSender.send(mimeMessage);
	}
}
