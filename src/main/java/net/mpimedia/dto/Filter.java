package net.mpimedia.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.mpimedia.annotation.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Filter implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -5151185528546046666L;
	@Builder.Default
	private int limit = 0;
	@Builder.Default
	private int page = 0;
	private String orderType;
	private String orderBy;
	@Builder.Default
	private boolean contains = true;
	@Builder.Default
	private boolean beginsWith = true;
	@Builder.Default
	private boolean exacts = false;
	@Builder.Default
	private Integer year = 0;
	@Builder.Default
	private Integer month = 0;
	@Builder.Default
	private String module = "IN";
	@Builder.Default
	private Map<String, Object> fieldsFilter = new HashMap<>();
	
	private Integer monthTo;
	private Integer yearTo; 
	
	private String entryPoint;

}
