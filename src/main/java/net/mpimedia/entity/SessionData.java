package net.mpimedia.entity;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="session_data")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionData extends BaseEntity implements Remote, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1210492423406561769L;
	
	@Column(name="session_key")
	private String key;
	@JoinColumn(name="user_id")
	@ManyToOne
	public User user;
	@JoinColumn(name="division_id")
	@ManyToOne
	public Division division;
	@Column
	public String message;
	@Column(name="request_day")
	public Date requestDate;
	
	@Transient
	@Builder.Default
	private Date modifiedDate = new Date();
	@Transient
	@JsonIgnore
	private List<Program> programs;
	@Transient
	@JsonIgnore
	private List<Section> sections;
	

}
