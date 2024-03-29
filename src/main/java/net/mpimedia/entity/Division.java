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
import net.mpimedia.annotation.FormField;

@Entity
@Table(name = "division")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Division extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3882451302950284229L;
	@Column
	@FormField
	private String name;
	@Column
	@FormField
	private String description;
	@FormField (entityReferenceName="institution",optionItemName="name",type="dynamiclist")
	@JoinColumn(name = "institution_id")
	@ManyToOne
	private Institution institution;

}
