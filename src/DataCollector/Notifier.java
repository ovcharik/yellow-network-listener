package DataCollector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Config.Config;
import Data.DataNode;
import Data.DataUser;
import Database.Database;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

	
class Notifier {
    static final String	ENCODING = "UTF-8";
    static final String	SUBJECT_ALERT = "YeS: Alert!!"; //тема сообщения
    static final String	MESSAGE_ALERT = "Alert! Nodes overloaded:\n"; //сообщениt о перегрузке
    static final long		RESEND_TIME = 300000;
    
    private Authenticator	m_auth = null;
    private Properties		m_props = null;
    
    private String			m_from = null;
    private Address[]		m_contactList=null;
    
    
	/**
	 * Конструктор объекта уведомителя
	 * @param smtpServer адрес сервера SMTP
	 * @param smtpPort порт сервера SMTP
	 * @param username почтовый логин
	 * @param password пароль
	 */
	public Notifier() {
		
		Config conf = Config.getInstance();
		
		List<DataUser> userList = null;
		try {
			userList = Database.getInstance().getUsersList();
		} catch (SQLException e) {
			System.err.println("SQLException while getting users list");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Exception while getting users list");
			e.printStackTrace();
		} 
		m_contactList=new Address[userList.size()];
		int i=0;
		for(DataUser email : userList) {
			try {
				m_contactList[i] = new InternetAddress(email.getEmail());
			} catch (AddressException e) {
				System.err.println("AddressException while cpoying emails");
				e.printStackTrace();
			}	
			i++;
		}

		m_from = conf.getSMTPUsername();
        m_auth = new MailAuthenticator(conf.getSMTPUsername(), conf.getSMTPPassword());
        
        m_props = System.getProperties();
        m_props.put("mail.smtp.port", conf.getSMTPPort());
        m_props.put("mail.smtp.host", conf.getSMTPHost());
        m_props.put("mail.smtp.auth", "true");
        m_props.put("mail.mime.charset", ENCODING);
		
	}
	
	/**
	 * Отправка уведомления о перегрузке, если отправка не удалась, ждет время RESEND_TIME и повторяет отправку
	 * @param nodes список узлов с проблемами
	 */
	public void sendNotification (List<DataNode> nodes) {
		
		final List<DataNode> nodesClone = new ArrayList<DataNode>(nodes);
		
		new Thread() {
			public void run() {
				String content = "";
		        boolean sendingSuccessful=false;
				
				Session session = Session.getDefaultInstance(m_props, m_auth);
				
		        Message msg = new MimeMessage(session);
		        try {
					msg.setFrom(new InternetAddress(m_from));
					msg.setRecipients(Message.RecipientType.TO, m_contactList);
					msg.setSubject(SUBJECT_ALERT);
					for(DataNode entry : nodesClone) {
						content+=entry.getIpAddress()+"\n";
					}
					msg.setText(MESSAGE_ALERT+content); 
				} catch (AddressException e1) {
					System.err.println("Address error");
					e1.printStackTrace();
				} catch (MessagingException e1) {
					System.err.println("Message parameters error");
					e1.printStackTrace();
				}
				
		        do {
		        	try {
		        		Transport.send(msg);
		        		sendingSuccessful=true;
		        	} catch (MessagingException e) {
		        		System.err.println("Error sending message. Waiting to resend");
		        		e.printStackTrace(); 
		        		sendingSuccessful=false;
						try {
							Thread.sleep(RESEND_TIME);
						} catch(InterruptedException ex) {
							Thread.currentThread().interrupt();
						}
		        	}
		        } while (!sendingSuccessful);	
			}
		}.start();
	}
	
}

class MailAuthenticator extends Authenticator {
    private String username;
    private String password;

    MailAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public PasswordAuthentication getPasswordAuthentication() {
        String username = this.username;
        String password = this.password;
        return new PasswordAuthentication(username, password);
    }
}
