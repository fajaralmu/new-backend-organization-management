package net.mpimedia.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mpimedia.annotation.CustomEntity;
import net.mpimedia.annotation.FormField;

@Entity
@Table(name = "event")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

//
@CustomEntity(rootFilter = { "program","section","division"})
public class Event extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4248866956791841771L;
	@Column
	@FormField
	private String name;
	@Column
	@FormField
	private String location;
	@Column
	@FormField
	private String info;
	@Column
	@FormField
	private int done;
	@Column
	@FormField
	private int participant;  
	@Column
	@FormField
	private Date date;
	 

	@JoinColumn(name = "program_id")
	@ManyToOne
	@FormField (entityReferenceName="program",optionItemName="name",type="dynamiclist")
	private Program program;
	@JoinColumn(name = "user_id")
	@ManyToOne
	private User user;

}
