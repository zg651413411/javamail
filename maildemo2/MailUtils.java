package javamail;

import java.io.IOException;


import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * 作用：发邮件
 * @author Administrator
 *
 */
public class MailUtils {

	public static Session createSession(String host,final String username,final String password){
		Properties prop = new Properties();	
		prop.setProperty("mail.host", host);		//指定主机
		prop.setProperty("mail.smtp.auth", "true");	//指定验证为true
		prop.setProperty("mail.smtp.starttls.enable", "true");//使用 STARTTLS安全连接
		//创建验证器
		Authenticator auth = new Authenticator(){
			public PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(username,password);
			}
		};
		
		//获取Session对象
		return Session.getInstance(prop, auth);
	}
		/**
		 * 发送指定的邮件
		 */
		public static void send(Session session,final Mail mail)throws MessagingException,IOException{
			
			MimeMessage msg = new MimeMessage(session);
			
			//设置发件人
			msg.setFrom(new InternetAddress(mail.getFrom()));	
			
			//设置收件人
			msg.addRecipients(RecipientType.TO,mail.getToAddress());
			
			//设置抄送
			String cc = mail.getCcAddress();
			if(!cc.isEmpty()){
				msg.addRecipients(RecipientType.CC, cc);
			}
			
			//设置暗送
			String bcc = mail.getBccAddress();
			if(!bcc.isEmpty()){
				msg.addRecipients(RecipientType.BCC, bcc);
			}
			
			//设置主题
			msg.setSubject(mail.getSubject());
			
			//创建部件集对象
			MimeMultipart parts = new MimeMultipart();
			
			//创建一个部件
			MimeBodyPart part = new MimeBodyPart();
			
			//设置邮件文本内容
			part.setContent(mail.getContent(),"text/html;charset=utf-8");
			
			//把部件添加到部件集中
			parts.addBodyPart(part);
			
			/*
			 * 添加附件
			 */
			//获取所有附件
			List<AttachBean> attachBeanList = mail.getAttachList();
			if(attachBeanList != null){
				for(AttachBean attach : attachBeanList){
					
					//创建一个部件
					MimeBodyPart attachPart = new MimeBodyPart();
					
					//设置附件文件
					attachPart.attachFile(attach.getFile());
					
					//设置附件文件名
					attachPart.setFileName(MimeUtility.encodeText(attach.getFileName()));
					String cid = attach.getCid();
					if(cid != null){
						attachPart.setContentID(cid);
					}
					parts.addBodyPart(attachPart);
				}
			}
			msg.setContent(parts);	//给邮件设置内容
			Transport.send(msg);	//发送邮件
		}
}
