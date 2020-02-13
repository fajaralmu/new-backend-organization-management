package net.mpimedia.repository;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Criteria;

 

public interface RepositoryCustom<T> {

	 
	List<T> filterAndSort(String q, Class<?> objectClass);
	
	List<T> filterAndSort(CriteriaQuery<?> q, Class<?> objectClass);
	
	Object getSingleResult(String q); 
	
	Query createNativeQuery(String sql);

	public Object getCustomedObjectFromNativeQuery(String sql, Class<?> objectClass);
}
