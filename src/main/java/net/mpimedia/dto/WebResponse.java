package net.mpimedia.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mpimedia.annotation.Dto;
import net.mpimedia.entity.BaseEntity;
import net.mpimedia.entity.Division;
import net.mpimedia.entity.SessionData;
import net.mpimedia.entity.User;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor 
public class WebResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8345271799535134609L;
	@Builder.Default
	private Date date = new Date();
	@Builder.Default
	private String code = "00";
	private String message;
	private User user;
	private BaseEntity entity;
	private List<BaseEntity> entities;
	private List<Division> divisions; 

	private int totalData;
	private SessionData sessionData;
	private boolean loggedIn;

	private Filter filter;

	//realtime loading
	private String requestId;
	private double percentage;
	
	public WebResponse(String code, String message) {
		this.code = code;
		this.message = message;
		this.date = new Date();
	}
	public static WebResponse failed() {
		return   failed("INVALID REQUEST");
	}
	
	public static WebResponse failed(String msg) {
		return new WebResponse("01", msg);
	}
	public static WebResponse failedResponse() {
		return new WebResponse("01", "INVALID REQUEST");
	}

	public static WebResponse success() {
		return new WebResponse("00", "SUCCESS");
	}
	public static WebResponse invalidSession() { 
		return new WebResponse("02","Invalid Session");
	}
}
