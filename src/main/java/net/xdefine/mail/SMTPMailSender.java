package net.xdefine.mail;

import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.velocity.VelocityEngineUtils;

import net.xdefine.XFContext;

public class SMTPMailSender {
	
	private JavaMailSender javaMailSender;
	private VelocityEngine velocityEngine;

	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	public void sendMailByVM(String mail, String title, String vmpath, Map<String, Object> map) {
		this.sendMailByVM(new String[]{ mail }, title, vmpath, map);
	}

	public void sendMailByVM(String[] mailes, String title, String vmpath, Map<String, Object> map) {
		String domain = XFContext.getProperty("webapp.site_ssl_url");
		if (domain == null || domain.isEmpty()) domain = XFContext.getProperty("webapp.site_web_url");
		map.put("domain", domain);

		for (String mail : mailes) {
			map.put("mailaddress", mail);
			String htmlString = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, vmpath, "UTF-8", map);
			this.sendMail(mail, title, htmlString);
		}
	}
	
	public void sendMail(String mail, String title, String contents) {
		this.sendMail(new String[]{ mail }, title, contents);
	}

	public void sendMail(String[] mailes, String title, String contents) {
		for (String mail : mailes) {
			MailThread thread = new MailThread(javaMailSender, title, contents, mail);
			thread.start();
		}
	}

}
