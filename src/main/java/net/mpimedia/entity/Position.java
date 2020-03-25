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
@Table(name = "position")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Position extends BaseEntity implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -6915469135513082491L;
	@Column
	@FormField
	public String name;
	@Column
	@FormField
	public String description;

}
