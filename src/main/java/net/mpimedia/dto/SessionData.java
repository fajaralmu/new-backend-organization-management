package net.mpimedia.dto;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mpimedia.entity.Division;
import net.mpimedia.entity.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionData implements Remote, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1210492423406561769L;
	public User user;
	public Division division;
	public String message;
	public Date requestDate;

}
