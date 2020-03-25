package net.mpimedia.entity;

import java.io.Serializable;

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
@Table(name = "member")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor 
@CustomEntity( )
public class Member extends BaseEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9064242000875756709L;
	@Column
	@FormField
	public String name;
	@Column
	@FormField
	public String description; 

	@JoinColumn(name = "position_id")
	@ManyToOne
	@FormField (entityReferenceName="position",optionItemName="name",type="dynamiclist") 
	public Position position;
	@JoinColumn(name = "section_id")
	@ManyToOne
	@FormField (entityReferenceName="section",optionItemName="name",type="dynamiclist")
	public Section section;

}
