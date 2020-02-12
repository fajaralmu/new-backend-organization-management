package net.mpimedia.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mpimedia.annotation.FormField;

//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>

// Bivisi BPD
//------------------------------------------------------------------------------
@Entity
@Table(name = "section")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Section extends BaseEntity implements Serializable {

	@Column
	public String name;
	@Column(name="parent_section_id")
	public int parentSectionId;

	@JoinColumn(name = "division_id")
	@FormField (entityReferenceName="division",optionItemName="name",type="dynamiclist") 
	public Division division;

}
