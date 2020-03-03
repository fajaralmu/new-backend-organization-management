package net.mpimedia.repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import lombok.extern.slf4j.Slf4j;
import net.mpimedia.annotation.CustomEntity;
import net.mpimedia.dto.Filter;
import net.mpimedia.entity.BaseEntity;

@Slf4j

public class RepositoryCustomImpl<T> implements RepositoryCustom<T> {
  
	private final EntityManager entityManager;

	public RepositoryCustomImpl(EntityManager entityManager) { 
	 
		this.entityManager = entityManager;
	}

	@Override
	public List<T> filterAndSort(String sql, Class<?> clazz) {

		log.info("Native Query : " + sql);
		List<T> resultList = entityManager.createNativeQuery(sql, clazz).getResultList();
		if (resultList == null) {
			resultList = new ArrayList<>();
		}
		log.info("Result Size : {}", resultList.size());
		return resultList;

	}

	@Override
	public Object getSingleResult(String sql) {
		log.info("=============GETTING SINGLE RESULT SQL: {}", sql);
		Object result = entityManager.createNativeQuery(sql).getSingleResult();
		log.info("=============RESULT SQL: {}, type: {}", result,
				result != null ? result.getClass().getCanonicalName() : null);
		return result;
	}

	@Override
	public Query createNativeQuery(String sql) {
		Query q = entityManager.createNativeQuery(sql);
		return q;
	}

	@Override
	public Object getCustomedObjectFromNativeQuery(String sql, Class<?> objectClass) {
		log.info("SQL for result object: {}", sql);
		try {
			Object singleObject = objectClass.getDeclaredConstructor().newInstance();

			Query result = entityManager.createNativeQuery(sql);
			Object resultObject = result.getSingleResult();
			log.info("object ,{}", resultObject);
			/**
			 * check if object has custom entity annotation
			 */
			if (null == resultObject || objectClass.getAnnotation(CustomEntity.class) == null) {
				return null;
			}

			/**
			 * mapping result list to object fields based on information from the
			 * CustomEntity annotation
			 */
			CustomEntity customEntitySetting = (CustomEntity) objectClass.getAnnotation(CustomEntity.class);
			Object[] propertiesArray = (Object[]) resultObject;
			String[] propertyOrder = customEntitySetting.propOrder();

			singleObject = fillObject(singleObject, propertiesArray, propertyOrder);
			log.info("RESULT OBJECT: {}", singleObject);
			return singleObject;
		} catch (Exception e) {
			log.error("ERROR GET RECORD: " + e.getMessage());
			return null;
		}

	}

	private Object fillObject(Object object, Object[] propertiesArray, String[] propertyOrder)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		for (int j = 0; j < propertiesArray.length; j++) {
			String propertyName = propertyOrder[j];
			Object propertyValue = propertiesArray[j];
			Field field = object.getClass().getDeclaredField(propertyName);
			field.setAccessible(true);
			if (field != null && propertyValue != null) {
				final Class<?> fieldType = field.getType();
				if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
					log.info("type integer ==========================> : {}", propertyValue);
					propertyValue = Integer.parseInt(propertyValue.toString());
				} else if (field.getType().equals(Long.class) || fieldType.equals(long.class)) {
					log.info("type long ==========================> : {}", propertyValue);
					propertyValue = Long.parseLong(propertyValue.toString());
				} else if (field.getType().equals(Double.class)  || fieldType.equals(double.class)) {
					log.info("type double ==========================> : {}", propertyValue);
					propertyValue = Double.parseDouble(propertyValue.toString());
				}

				field.set(object, propertyValue);
			}

		}
		return object;
	}

	@Override
	public List<T> filterAndSort(CriteriaQuery<?> criteriaQuery, Class<?> objectClass) { 
		Query query = entityManager.createQuery(criteriaQuery);
		List<T> resultList = query.getResultList();
		return resultList;
	}
	
	public List<T> filterEntity(Filter filter, Class<?> entityClass, BaseEntity rootFilterEntity) {
		return null;
//
//			log.info("CRITERIA-FILTER: {}", filter);
//			CriteriaBuilderImpl builder = new CriteriaBuilderImpl((EntityManagerFactoryImpl) entityManager);
//
//			
//			Integer offset = filter.getPage() * filter.getLimit();
//			boolean withLimit = filter.getLimit() > 0;
//			boolean withOrder = filter.getOrderBy() != null && filter.getOrderType() != null
//					&& !filter.getOrderBy().equals("") && !filter.getOrderType().equals("");
//			boolean contains = filter.isContains();
//			boolean exacts = filter.isExacts();
//			boolean withFilteredField = filter.getFieldsFilter() != null;
//
//			String orderType = filter.getOrderType();
//			String orderBy = filter.getOrderBy();
//			String tableName = getTableName(entityClass);
//			String orderSQL = withOrder ? orderSQL(entityClass, orderType, orderBy) : "";
//
//			String limitOffsetSQL = withLimit
//					? buildString("LIMIT", String.valueOf(filter.getLimit()), "OFFSET", String.valueOf(offset))
//					: "";
//
//			String filterSQL = withFilteredField
//					? createFilterSQL(entityClass, filter.getFieldsFilter(), contains, exacts, rootFilterEntity)
//					: "";
//
//			String joinSql = createLeftJoinSQL(entityClass);
//
//			String sql = buildString("select ", doubleQuoteMysql(tableName), ".* from ", doubleQuoteMysql(tableName),
//					joinSql, filterSQL, orderSQL, limitOffsetSQL);
//
//			String sqlCount = buildString("select COUNT(*) from  ", doubleQuoteMysql(tableName), joinSql, filterSQL);
//
//			
//			Query query = entityManager.createQuery(builder.createQuery());
//			
//			return new String[] { sql, sqlCount };
//		 
//
//	}
//	
//	private static String createFilterSQL(Class entityClass, Map<String, Object> filter, boolean contains,
//			boolean exacts, BaseEntity rootFilterEntity, CriteriaBuilder  builder) {
//
//	 
//		List<String> filters = new ArrayList<String>();
//		List<Field> fields = EntityUtil.getDeclaredFields(entityClass);
//
//		log.info("=======FILTER: {}", filter);
//
//		for (final String rawKey : filter.keySet()) {
//			log.info("................." + rawKey + ":" + filter.get(rawKey));
//
//			String key = rawKey;
//			if (filter.get(rawKey) == null)
//				continue;
//
//			boolean itemExacts = exacts;
//			boolean itemContains = contains;
//
//			if (rawKey.endsWith("[EXACTS]")) {
//				itemExacts = true;
//				itemContains = false;
//				key = rawKey.split("\\[EXACTS\\]")[0];
//			}
//
//			log.info("-------KEY:" + key);
//
//			String[] multiKey = key.split(",");
//			boolean isMultiKey = multiKey.length > 1;
//
//			if (isMultiKey) {
//				key = multiKey[0];
//			}
//
//			String columnName = key;
//			// check if date
//			boolean dayFilter = key.endsWith("-day");
//			boolean monthFilter = key.endsWith("-month");
//			boolean yearFilter = key.endsWith("-year");
//
//			if (dayFilter || monthFilter || yearFilter) {
//
////				String fieldName = key;
////				String mode = "DAY";
////				String sqlItem = " $MODE(`${TABLE_NAME}`.`${COLUMN_NAME}`) = ${VALUE} ";
////				if (dayFilter) {
////					fieldName = key.replace("-day", "");
////					mode = "DAY";
////
////				} else if (monthFilter) {
////					fieldName = key.replace("-month", "");
////					mode = "MONTH";
////
////				} else if (yearFilter) {
////					fieldName = key.replace("-year", "");
////					mode = "YEAR";
////
////				}
////
////				Field field = getFieldByName(fieldName, fields);
////
////				if (field == null) {
////					log.warn("!!!!!!!!!!! FIELD NOT FOUND: " + fieldName);
////					continue;
////
////				}
////
////				columnName = getColumnName(field);
////				sqlItem = sqlItem.replace("${TABLE_NAME}", tableName).replace("${MODE}", mode)
////						.replace("${COLUMN_NAME}", columnName).replace("${VALUE}", filter.get(key).toString());
////				filters.add(sqlItem);
//				continue;
//			}
//
//			Field field = EntityService.getFieldByName(key, fields);
//
//			if (field == null) {
//				log.warn("!!!!!!!Field Not Found :" + key);
//				continue;
//
//			}
//			if (field.getAnnotation(Column.class) != null) {
//				columnName = EntityService.getColumnName(field);
//
//			}
//
//		//	StringBuilder sqlItem = new StringBuilder(doubleQuoteMysql(tableName).concat(".").concat(columnName));
//			Predicate restrictions;
//			 
//			if (field.getAnnotation(JoinColumn.class) != null || isMultiKey) {
//
//				Class fieldClass = field.getType();
//				String joinTableName = getTableName(fieldClass);
//				FormField formField = field.getAnnotation(FormField.class);
//
//				try {
//					String referenceFieldName = formField.optionItemName();
//
//					if (isMultiKey) {
//						referenceFieldName = multiKey[1];
//					}
//
//					Field fieldField = EntityUtil.getDeclaredField(fieldClass, referenceFieldName);
//					String fieldColumnName = getColumnName(fieldField);
//
//					if (fieldColumnName == null || fieldColumnName.equals("")) {
//						fieldColumnName = key;
//					}
//
//					sqlItem = sqlItem.append(
//							doubleQuoteMysql(joinTableName).concat(".").concat(doubleQuoteMysql(fieldColumnName)));
//
//				} catch (SecurityException e) {
//					e.printStackTrace();
//					log.warn("!!!!!" + e.getClass() + " " + e.getMessage() + " " + fieldClass);
//					continue;
//				}
//
//			}
//			// rollback key to original key
//			/*
//			 * if (isMultiKey) { key = String.join(",", multiKey); if
//			 * (rawKey.endsWith("[EXACTS]")) { key+="[EXACTS]"; } }
//			 */
//		 
//			if (itemContains) {
//				Object entityType = new Object();
//				Expression<String> exp = new Expression
//				restrictions = new LikePredicate(builder, exp, "%"+filter.get(rawKey)+"%");
//				sqlItem = sqlItem.append(" LIKE '%").append(filter.get(rawKey)).append("%' ");
//
//			} else if (itemExacts) {
//				sqlItem = sqlItem.append(" = '").append(filter.get(rawKey)).append("' ");
//			}
//
//			log.info("SQL ITEM: " + sqlItem + " contains :" + itemContains + ", exacts:" + itemExacts);
//
//			filters.add(sqlItem.toString());
//		}
//
//		String additionalFilter = "";
//
//		if (rootFilterEntity != null) {
//			additionalFilter = addFilterById(entityClass, rootFilterEntity.getClass(), rootFilterEntity.getId());
//		}
//
//		if (filters == null || filters.size() == 0) {
//
//			if (rootFilterEntity != null && additionalFilter.isEmpty() == false) {
//				return "WHERE ".concat(additionalFilter);
//			}
//
//			return "";
//
//		}
//
//		String whereClause = "";
//
//		if (filters.size() > 0) {
//			whereClause = String.join(" AND ", filters);
//		}
//
//		String result = " WHERE " + whereClause;
//
//		if (additionalFilter.isEmpty()) {
//
//			if (filters.size() == 0) {
//				return "";
//
//			}
//			return result;
//		}
//
//		return result.concat(filters.size() > 0 ? " AND " : " ").concat(additionalFilter);
		
	}

}
