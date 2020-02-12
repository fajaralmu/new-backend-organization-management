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
//------------------------------------------------------------------------------
@Entity
@Table(name = "member")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9064242000875756709L;
	@Column
	public String name;
	@Column
	public String description;
	@Column
	public int position_id;

	@JoinColumn(name = "position_id")
	@FormField (entityReferenceName="position",optionItemName="name",type="dynamiclist") 
	public Position position;
	@JoinColumn(name = "section_id")
	@FormField (entityReferenceName="section",optionItemName="name",type="dynamiclist")
	public Section section;

}
