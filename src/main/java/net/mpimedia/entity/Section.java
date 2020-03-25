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
 

// Bivisi BPD
//------------------------------------------------------------------------------
@Entity
@Table(name = "section")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor 
@CustomEntity(rootFilter = {  "division"})  
public class Section extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4402925267475327805L;
	@Column
	@FormField
	public String name;
	@Column
	@FormField
	private String description;
	@Column(name="parent_section_id")
	public int parentSectionId;

	@JoinColumn(name = "division_id")
	@ManyToOne
	@FormField (entityReferenceName="division",optionItemName="name",type="dynamiclist") 
	public Division division;

}
