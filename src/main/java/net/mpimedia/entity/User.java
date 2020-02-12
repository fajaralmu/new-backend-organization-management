package net.mpimedia.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mpimedia.annotation.Dto;
import net.mpimedia.annotation.FormField;

//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------
@Dto
@Entity
@Table(name = "app_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor 
public class User extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7582045155923785034L;
	@Column
	@FormField
	public String username;
	@Column
	@FormField
	public String name;
	@Column
	@FormField
	public String password;
	@Column
	@FormField
	public String email;
	@Column
	@FormField
	public int admin;
	@JoinColumn(name = "institution_id")
	@ManyToOne
	@FormField (entityReferenceName="institution",optionItemName="name",type="dynamiclist")
	public Institution institution;


	@Transient
	private String loginKey;

}