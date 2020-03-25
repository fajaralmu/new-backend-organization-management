package net.mpimedia.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mpimedia.annotation.Dto;
import net.mpimedia.annotation.FormField;

@Dto
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "message")
public class Message extends BaseEntity {

	public Message(String reqId, String content, String receiver) {
		this.sender = reqId;
		this.text = content;
		LocalDateTime localDateTime = LocalDateTime.now();
		this.date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		this.requestId = reqId;
		this.receiver = receiver;

	}

	public static void main(String[] args) {
	//	System.out.println(ZoneId.systemDefault());
	}
	
	@Column
	@FormField
	private int admin;
	@Column
	@FormField
	private String sender;
	@Column(name = "user_agent")
	@FormField
	private String userAgent;
	@Column(name = "ip_address")
	@FormField
	private String ipAddress;
	@Column
	@FormField
	private String text;
	@JsonFormat(pattern = "E, dd MMM yyyy HH:mm:ss z")
	@Column
	@FormField
	private Date date;
	@Column
	@FormField
	private String alias;
	@Column(name = "request_id")
	@FormField
	private String requestId;
	@Column
	@FormField
	private String receiver;

}
