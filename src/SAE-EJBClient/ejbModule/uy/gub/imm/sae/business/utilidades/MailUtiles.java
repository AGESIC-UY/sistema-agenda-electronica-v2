package uy.gub.imm.sae.business.utilidades;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;

import uy.gub.imm.sae.common.SAEProfile;

public class MailUtiles {

	private static final String PROP_MAIL_FROM = "uy.gub.imm.sae.acciones.notificar.mail.from";
	
	public static final String CONTENT_TYPE_PLAIN = "text/plain; charset=UTF-8";
	public static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
	
	public static void enviarMail(final String to, final String subject, final String content, final String type) throws MessagingException {
		(new Thread() {
			public void run() {
				try {
					Properties saeProperties = SAEProfile.getInstance().getProperties();
					
					InitialContext ic = new InitialContext();
					Session mailSession = (Session) ic.lookup("java:/sae/mail");
          ic.close();
          
          String sFrom = mailSession.getProperty("mail.smtp.user");
          if(sFrom == null || sFrom.trim().isEmpty()) {
          	sFrom = saeProperties.getProperty(PROP_MAIL_FROM);
          }
					
	        MimeMessage m = new MimeMessage(mailSession);
	        Address from = new InternetAddress(sFrom);
	        Address[] toList = new InternetAddress[] {new InternetAddress(to) };
	
	        m.setFrom(from);
	        m.setRecipients(Message.RecipientType.TO, toList);
	        m.setSubject(subject);
	        m.setSentDate(new java.util.Date());
	        m.setContent(content.toString(),type);
	        
	        Transport.send(m);
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}
	
}
