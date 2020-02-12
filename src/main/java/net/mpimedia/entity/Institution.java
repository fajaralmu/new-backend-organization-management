package net.mpimedia.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mpimedia.annotation.FormField;

@Entity
@Table(name = "institution")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Institution extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 41729855654281104L;
	@Column
	@FormField
	private String name;
	@Column
	@FormField
	private String description;
	 

}
