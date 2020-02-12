package net.mpimedia.dto;

import java.io.Serializable;

import net.mpimedia.annotation.Dto;
import net.mpimedia.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTempRequest implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 3786301144797644660L;
	 
	private User user;
	private Long userId;
	private String requestURI;
	

}
