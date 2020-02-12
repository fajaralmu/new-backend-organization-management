package net.mpimedia.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mpimedia.annotation.Dto;

@Dto
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealtimeResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4025058368937979008L; 
	 
	private String responseCode;
	private String responseMessage;
	private Map<String, Object> info;
	 
	
	private List<String> infos = new ArrayList<>();
 
	public RealtimeResponse(String rc,String rm) {
		this.responseCode = rc;
		this.responseMessage = rm;
	}
	
	 
	 
	
	
	

}
