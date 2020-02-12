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

@Entity
@Table(name = "post")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseEntity implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1425372727157160215L;
	@Column
	public String name;
	@Column
	public String body;
	@Column(name = "post_id")
	public int postId;
	@Column
	public int type;
	@Column
	public Date date;

	public String title;
	@JoinColumn(name = "user_id")
	@ManyToOne
	public User user;

}
