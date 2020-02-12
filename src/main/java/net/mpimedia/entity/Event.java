package net.mpimedia.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mpimedia.annotation.FormField;

@Entity
@Table(name = "event")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event extends BaseEntity implements Serializable {

	@Column
	private String name;
	@Column
	private String location;
	@Column
	private String info;
	@Column
	private int done;
	@Column
	private int participant;  
	@Column
	private Date date;
	 

	@JoinColumn(name = "program_id")
	@FormField (entityReferenceName="program",optionItemName="name",type="dynamiclist")
	private Program program;
	@JoinColumn(name = "user_id")
	private User user;

}
