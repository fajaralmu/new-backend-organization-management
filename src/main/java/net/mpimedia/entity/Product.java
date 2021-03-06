//package net.mpimedia.entity;
//
//import java.io.Serializable;
//import java.util.List;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//import javax.persistence.Transient;
//
//import net.mpimedia.annotation.Dto;
//import net.mpimedia.annotation.FormField;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Dto
//@Entity
//@Table(name="product")
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class Product extends BaseEntity implements Serializable {
//
//	/**
//	* 
//	*/
//	private static final long serialVersionUID = 3494963248002164943L;
//	@Column(unique = true)
//	@FormField 
//	private String code;
//	@Column(unique = true)
//	@FormField 
//	private String name;
//	@Column
//	@FormField 
//	private String description;
//	@Column
//	@FormField(type="currency")
//	private long price;
//	@Column
//	@FormField 
//	private String type;
//	@Column(name="image_url", unique = true)
//	@FormField (type="img", required = false,multiple = true, defaultValue="Default.BMP")
//	private String imageUrl;
//	@JoinColumn(name = "unit_id")
//	@ManyToOne
//	@FormField (entityReferenceName="unit",optionItemName="name",type="dynamiclist")
//	private Unit unit;
//	@JoinColumn(name = "category_id", nullable = true)
//	@ManyToOne
//	@FormField (entityReferenceName="category",optionItemName="name",type="dynamiclist")
//	private Category category;
//	
//	@Transient
//	private boolean newProduct;	
//	@Transient
//	private int count;
//	@Transient
//	private List<Supplier> suppliers;
//
//}
