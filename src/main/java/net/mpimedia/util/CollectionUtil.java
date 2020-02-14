package net.mpimedia.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.mpimedia.entity.BaseEntity;

@Slf4j
public class CollectionUtil {
	
	public static <T> List<T> arrayToList(T[] array) {
		List<T> list = new ArrayList<T>();
		for (T t : array) {
			list.add(t);
		}
		return list;

	}

	public static void main(String[] args) {

	}

	public static List< BaseEntity> mapToList(Map<? extends Object, ? extends BaseEntity> map) {
		List< BaseEntity> list = new ArrayList<>();
		for (Object key : map.keySet()) {
			list.add(map.get(key));
		}

		return list;
	}
 
	
	public static List filterList(String fieldName, String value, final List list) {
		
		List result = new ArrayList<>();
		
		if(list!=null && value !=null && fieldName!=null) {
			
			for (Object object : list) {
				Field field =  EntityUtil.getDeclaredField(object.getClass(), fieldName);
				
				if(null == field) {
					continue;
				}
				
				field.setAccessible(true);
				
				try {
					Object fieldValue = field.get(object);
					if(fieldValue != null && fieldValue.toString().toLowerCase().contains(value.toLowerCase())) {
						result.add(object);
					}
					
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.info("Error getting value from field: {}", e);
					continue;
				}
			}
		
		}
		return result;
	}

	public static <T> List<T> convertList(List list) {
		List<T> newList = new ArrayList<T>();
		for (Object object : list) {
			newList.add((T) object);
		}
		return newList;
	}

	public static String[] toArrayOfString(List validUrls) {
		if(validUrls == null) {
			return new String[] {};
		}
		String[] array = new String[validUrls.size()];
		for (int i = 0; i < validUrls.size(); i++) {
			array[i] = validUrls.get(i).toString();
		}
		return array;
	}

}
