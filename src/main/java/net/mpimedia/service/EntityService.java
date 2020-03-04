package net.mpimedia.service;

import static net.mpimedia.util.StringUtil.buildString;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.mpimedia.annotation.CustomEntity;
import net.mpimedia.annotation.FormField;
import net.mpimedia.dto.Filter;
import net.mpimedia.dto.WebRequest;
import net.mpimedia.dto.WebResponse;
import net.mpimedia.entity.BaseEntity;
import net.mpimedia.entity.Division;
import net.mpimedia.entity.Event;
import net.mpimedia.entity.Institution;
import net.mpimedia.entity.Member;
import net.mpimedia.entity.Message;
import net.mpimedia.entity.Position;
import net.mpimedia.entity.Post;
import net.mpimedia.entity.Program;
import net.mpimedia.entity.RegisteredRequest;
import net.mpimedia.entity.Section;
import net.mpimedia.entity.SessionData;
import net.mpimedia.entity.User;
import net.mpimedia.repository.EntityRepository;
import net.mpimedia.repository.InstitutionRepository;
import net.mpimedia.repository.ProgramRepository;
import net.mpimedia.repository.RepositoryCustomImpl;
import net.mpimedia.util.CollectionUtil;
import net.mpimedia.util.EntityUtil;
import net.mpimedia.util.LogProxyFactory;
import net.mpimedia.util.StringUtil;

@Service
@Slf4j
public class EntityService {

	private static final String DAY_SUFFIX = "-day";
	private static final String MONTH_SUFFIX = "-month";
	private static final String YEAR_SUFFIX = "-year";
	private static final String FILTER_DATE_DAY = "DAY";
	private static final String FILTER_DATE_MON1TH = "MONTH";
	private static final String FILTER_DATE_YEAR = "YEAR";
	
	//placeholders
	private static final String SQL_RAW_DATE_FILTER = " ${MODE}(`${TABLE_NAME}`.`${COLUMN_NAME}`) = ${VALUE} ";
	private static final String PLACEHOLDER_SQL_FOREIGN_ID = "${FOREIGN_ID}";
	private static final String PLACEHOLDER_SQL_JOIN_TABLE = "${JOIN_TABLE}";
	private static final String PLACEHOLDER_SQL_ENTITY_TABLE = "${ENTITY_TABLE}";
	private static final String PLACEHOLDER_SQL_JOIN_ID = "${JOIN_ID}";
	private static final String PLACEHOLDER_SQL_RAW_JOIN_STATEMENT = " LEFT JOIN `${JOIN_TABLE}` ON  `${JOIN_TABLE}`.`${JOIN_ID}` = `${ENTITY_TABLE}`.`${FOREIGN_ID}` ";
	private static final String PLACEHOLDER_SQL_TABLE_NAME = "${TABLE_NAME}";
	private static final String PLACEHOLDER_SQL_MODE = "${MODE}";
	private static final String PLACEHOLDER_SQL_COLUMN_NAME = "${COLUMN_NAME}";
	private static final String PLACEHOLDER_SQL_VALUE = "${VALUE}";
	
	private static final String SQL_KEYWORDSET_SELECT_COUNT = " SELECT COUNT(*) from  ";
	private static final String SQL_KEYWORD_SELECT = " SELECT "; 
	private static final String SQL_KEYWORD_LIMIT = " LIMIT ";
	private static final String SQL_KEYWORD_OFFSET = " OFFSET ";
	private static final String SQL_KEYWORD_ORDERBY = " ORDER BY ";
	private static final String SQL_KEYWORD_AND = " AND ";
	private static final String SQL_KEYWORD_WHERE = " WHERE ";
	private static final String SQL_KEYWORD_FROM = " from ";

	@Autowired
	private EntityRepository mainRepository;
	@Autowired
	private SessionService sessionService;
	@Autowired
	private TemporaryDataService temporaryDataService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private InstitutionRepository institutionRepository;
	@Autowired
	private RepositoryCustomImpl repositoryCustom;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	/**
	 * persist entity
	 * @param request
	 * @param newRecord
	 * @return
	 */
	public WebResponse updateRecord(WebRequest request, boolean newRecord) {

		SessionData sessionData = sessionService.GetSessionData(request);

		if (request.getEntity() == null || sessionData.getUser() == null) {
			return WebResponse.failed("Unauthorized");

		}

		if (sessionData.getDivision() == null) {
			return WebResponse.failed("Invalid Division");

		}

		try {

			switch (request.getEntity().toLowerCase()) {

			case "institution":
				return saveEntity(request.getInstitution(), newRecord);

			case "division":
				return saveDivision(request.getDivision(), newRecord);

			case "post":
				return saveEntity(request.getPost(), newRecord);

			case "member":
				return saveEntity(request.getMember(), newRecord);

			case "user":
				return saveEntity(request.getUser(), newRecord);

			case "program":
				return saveProgram(request.getProgram(), newRecord, sessionData);

			case "section":
				return saveEntity(request.getSection(), newRecord);

			case "event":
				request.getEvent().setUser(sessionData.getUser());
				return saveEntity(request.getEvent(), newRecord);

			case "registeredrequest":
				return saveEntity(request.getRegisteredRequest(), newRecord);

			case "position":
				return saveEntity(request.getPosition(), newRecord);
			default:
				// return WebResponse.failed("Invalid Entity!");
				break;

			}

		} catch (Exception ex) {

			return WebResponse.builder().code("01").message(ex.getMessage()).build();
		}
		return WebResponse.failed();

	}

	/**
	 * save common entity
	 * @param entity
	 * @param isNewRecord
	 * @return
	 */
	private WebResponse saveEntity(BaseEntity entity, boolean isNewRecord) {
		log.info("save common entity: {}", entity);
		
		entity = copyNewElement(entity, isNewRecord);
		BaseEntity savedEntity = mainRepository.save(entity);
		return WebResponse.builder().entity(savedEntity).build();
	}

	/**
	 * save program
	 * 
	 * @param program
	 * @param newRecord
	 * @param sessionData
	 * @return
	 */
	private WebResponse saveProgram(Program program, boolean newRecord, SessionData sessionData) {

		program = copyNewElement(program, newRecord);
		Program newProgram = mainRepository.save(program);
		accountService.updateSelectedDivision(sessionData);
		return WebResponse.builder().entity(newProgram).build();
	}

	/**
	 * save division
	 * 
	 * @param division
	 * @param newRecord
	 * @return
	 */
	private WebResponse saveDivision(Division division, boolean newRecord) {

		division = copyNewElement(division, newRecord);
		Division newDivision = mainRepository.save(division);
		temporaryDataService.init();
		return WebResponse.builder().entity(newDivision).build();
	}

	public List<Institution> getAllInstitution() {

		return institutionRepository.findAll();
	}

	/**
	 * search entity
	 * @param request
	 * @return
	 */
	public WebResponse filter(WebRequest request) {
		log.info("filter :{}", request);

		SessionData sessionData = sessionService.GetSessionData(request);

		if (request.getEntity() == null || sessionData == null || sessionData.getUser() == null) {
			return WebResponse.failed("Unauthorized");

		}

		if (sessionData.getDivision() == null) {
			return WebResponse.failed("Invalid Division");

		}

		Division selectedDivision = sessionData.getDivision();

		Class<? extends BaseEntity> entityClass = null;

		Filter filter = request.getFilter();

		if (filter == null) {
			filter = new Filter();
		}
		if (filter.getFieldsFilter() == null) {
			filter.setFieldsFilter(new HashMap<>());
		}

		final boolean inputFieldEntryPoint = "inputField".equals(filter.getEntryPoint());

		try {
			switch (request.getEntity().toLowerCase()) {

			case "institution":
				entityClass = Institution.class;
				break;

			case "division":
				entityClass = Division.class;
				break;

			case "post":
				entityClass = Post.class;
				break;

			case "member":
				entityClass = Member.class;
				break;

			case "user":
				entityClass = User.class;
				break;

			case "program":
				if (inputFieldEntryPoint) {
					return filterProgramFromInputField(filter.getFieldsFilter(), sessionData);
				}
				entityClass = Program.class;
				break;

			case "section":
				if (inputFieldEntryPoint) {
					return filterSectionFromInputField(filter.getFieldsFilter(), sessionData);
				}
				entityClass = Section.class;
				break;

			case "event":
				entityClass = Event.class;
				break;

			case "message":
				entityClass = Message.class;
				break;

			case "position":
				entityClass = Position.class;
				break;
			default:
				return WebResponse.failed();
			}

			String[] sqlListAndCount = generateSqlByFilter(filter, entityClass, selectedDivision);

			String sql = sqlListAndCount[0];
			String sqlCount = sqlListAndCount[1];

			List<BaseEntity> entities = doFilterEntity(sql, entityClass);

			Integer count = 0;
			Object countResult = repositoryCustom.getSingleResult(sqlCount);

			if (countResult != null) {
				count = ((BigInteger) countResult).intValue();
			}
			return WebResponse.builder().entities(entities).totalData(count).filter(filter).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return WebResponse.failed();
		}
	}
	
	/**
	 * if entry point: dynamic drop down input field
	 * @param fieldsFilter
	 * @param sessionData
	 * @return
	 */
	private WebResponse filterSectionFromInputField(Map<String, Object> fieldsFilter, SessionData sessionData) { 
		log.info("Will get section from sessions");

		WebResponse response = new WebResponse();

		if (sessionData.getPrograms() != null && fieldsFilter != null && fieldsFilter.get("name") != null) {

			String filterName = (String) fieldsFilter.get("name");

			List<Program> filteredPrograms = CollectionUtil.filterList("name", filterName, sessionData.getSections());
			response.setEntities(CollectionUtil.convertList(filteredPrograms));
		}

		return response;
	}

	/**
	 * if entry point: dynamic drop down input field
	 * @param filterFields
	 * @param sessionData
	 * @return
	 */
	private WebResponse filterProgramFromInputField(Map filterFields, SessionData sessionData) { 
		log.info("Will get program from sessions");

		WebResponse response = new WebResponse();

		if (sessionData.getPrograms() != null && filterFields != null && filterFields.get("name") != null) {

			String filterName = (String) filterFields.get("name");

			List<Program> filteredPrograms = CollectionUtil.filterList("name", filterName, sessionData.getPrograms());
			response.setEntities(CollectionUtil.convertList(filteredPrograms));
		}

		return response;
	}

	public List<BaseEntity> doFilterEntity(final String sql, Class<? extends BaseEntity> entityClass) {
		log.info("Will perform querying to DB");
		
		List<BaseEntity> entities = repositoryCustom.filterAndSort(sql, entityClass);
		return EntityUtil.validateDefaultValue(entities);
	}

	public WebResponse delete(WebRequest request) {
		Map<String, Object> filter = request.getFilter().getFieldsFilter();

		try {

			Long id = Long.parseLong(filter.get("id").toString());
			Class entityToDelete = BaseEntity.class;
			switch (request.getEntity().toLowerCase()) {
			case "institution":
				entityToDelete = Institution.class;
				break;
			case "division":
				entityToDelete = (Division.class);
				break;
			case "post":
				entityToDelete = (Post.class);
				break;
			case "member":
				entityToDelete = (Member.class);
				break;
			case "user":
				entityToDelete = (User.class);
				break;
			case "program":
				entityToDelete = (Program.class);
				break;
			case "section":
				entityToDelete = (Section.class);
				break;
			case "event":
				entityToDelete = (Event.class);
				break;
			case "registeredrequest":
				entityToDelete = (RegisteredRequest.class);
				break;
			case "position":
				entityToDelete = (Position.class);
				break;
			default:
				return WebResponse.failed();

			}

			boolean deleted = mainRepository.deleteById(id, entityToDelete);

			log.info("Delete status: {}", deleted);

			return WebResponse.builder().code("00").message("deleted successfully").build();

		} catch (Exception ex) {
			ex.printStackTrace();
			return WebResponse.builder().code("01").message("failed").build();
		}
	}

	private static <T> T copyNewElement(Object source, boolean newRecord) {

		Object result = EntityUtil.copyFieldElementProperty(source, source.getClass(), !newRecord);
		return (T) result;
	}

	public static Field getFieldByName(String name, List<Field> fields) {
		return EntityUtil.getObjectFromListByFieldName("name", name, fields);
	}

	public static String getColumnName(Field field) {
		log.info("get column Name " + field.getDeclaringClass() + " from " + field.getName());

		if (field.getAnnotation(Column.class) == null)
			return field.getName();
		String columnName = ((Column) field.getAnnotation(Column.class)).name();
		if (columnName == null || columnName.equals("")) {
			columnName = field.getName();
		}
		return columnName;
	}

	
	/**
	 * create LEFT JOIN Statement for one field only
	 * @param entityClass
	 * @param field
	 * @return
	 */
	public static String createLeftJoinSql(Class entityClass, Field field) { 
		log.info("Create item sql left join: " + entityClass + ", field: " + field);

		JoinColumn joinColumn = EntityUtil.getFieldAnnotation(field, JoinColumn.class);

		if (null == joinColumn) {
			return "";
		}
 
		Class fieldClass 		= field.getType();
		Field idForeignField 	= EntityUtil.getIdField(fieldClass);

		String joinTableName 	= getTableName(fieldClass);
		String tableName 		= getTableName(entityClass);
		String foreignID 		= joinColumn.name();


		String sqlItem = PLACEHOLDER_SQL_RAW_JOIN_STATEMENT.
				replace(PLACEHOLDER_SQL_FOREIGN_ID, foreignID).
				replace(PLACEHOLDER_SQL_JOIN_TABLE, joinTableName).
				replace(PLACEHOLDER_SQL_ENTITY_TABLE, tableName).
				replace(PLACEHOLDER_SQL_JOIN_ID, getColumnName(idForeignField));

		return sqlItem;

	}

	/**
	 * create LEFT JOIN statement full object
	 * @param entityClass
	 * @return
	 */
	private static String createLeftJoinSQL(Class<? extends BaseEntity> entityClass) {

		StringBuilder sql = new StringBuilder("");

		CustomEntity customModel = EntityUtil.getClassAnnotation(entityClass, CustomEntity.class);

		List<Field> fields = EntityUtil.getDeclaredFields(entityClass);
		for (Field field : fields) {

			if (customModel != null
					&& EntityUtil.existInList(field.getName(), Arrays.asList(customModel.rootFilter()))) {
				continue;
			}

			JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
			if (joinColumn != null) {

				String sqlItem = createLeftJoinSql(entityClass, field);

				sql = sql.append(sqlItem);

			}
		}

		if (customModel != null && customModel.rootFilter().length > 0) {
			sql = sql.append(validateRootFilter(entityClass, customModel.rootFilter()));
		}

		return sql.toString();
	}

	/**
	 * add join clause if class has root filter
	 * @param entityClass
	 * @param rootFilter
	 * @return
	 */
	public static String validateRootFilter(Class entityClass, String[] rootFilter) {

		StringBuilder stringBuilder = new StringBuilder("");

		Class currentType = entityClass;
		Field currentField = null;

		for (String string : rootFilter) {

			try {
				currentField = currentType.getDeclaredField(string);

				String sqlJoinItem = createLeftJoinSql(currentType, currentField);
				stringBuilder = stringBuilder.append(sqlJoinItem);

				currentType = currentField.getType();

			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}

		}

		return stringBuilder.toString();
	}

	private static String createFilterSQL(Class entityClass, Map<String, Object> filter, boolean contains,
			boolean exacts, BaseEntity rootFilterEntity) {

		String tableName 		= getTableName(entityClass);
		List<String> filters 	= new ArrayList<String>();
		List<Field> fields 		= EntityUtil.getDeclaredFields(entityClass);

		log.info("=======FILTER: {}", filter);

		for (final String rawKey : filter.keySet()) {
			log.info("................." + rawKey + ":" + filter.get(rawKey));

			String key = rawKey;
			
			if (filter.get(rawKey) == null)
				continue;

			boolean itemExacts = exacts;
			boolean itemContains = contains;

			if (rawKey.endsWith("[EXACTS]")) {
				itemExacts 		= true;
				itemContains 	= false;
				key 			= rawKey.split("\\[EXACTS\\]")[0];
			}

			log.info("Now KEY:" + key);

			String[] multiKey 	= key.split(",");
			boolean isMultiKey 	= multiKey.length > 1;

			if (isMultiKey) {
				key = multiKey[0];
			}

			String columnName = key;
			// check if date
			boolean dayFilter 	= rawKey.endsWith(DAY_SUFFIX);
			boolean monthFilter = rawKey.endsWith(MONTH_SUFFIX);
			boolean yearFilter 	= rawKey.endsWith(YEAR_SUFFIX);

			if (dayFilter || monthFilter || yearFilter) {

				String fieldName	= key;
				String mode 		= FILTER_DATE_DAY;
				String sqlItem 		= SQL_RAW_DATE_FILTER;
				
				if (dayFilter) {
					fieldName 	= key.replace(DAY_SUFFIX, "");
					mode 		= FILTER_DATE_DAY;

				} else if (monthFilter) {
					fieldName 	= key.replace(MONTH_SUFFIX, "");
					mode 		= FILTER_DATE_MON1TH;

				} else if (yearFilter) {
					fieldName	= key.replace(YEAR_SUFFIX, "");
					mode 		= FILTER_DATE_YEAR;

				}

				Field field = getFieldByName(fieldName, fields);

				if (field == null) {
					log.warn("FIELD NOT FOUND: " + fieldName + " !");
					continue;

				}

				columnName = getColumnName(field);
				sqlItem = sqlItem
						.replace(PLACEHOLDER_SQL_TABLE_NAME, tableName)
						.replace(PLACEHOLDER_SQL_MODE, mode)
						.replace(PLACEHOLDER_SQL_COLUMN_NAME, columnName)
						.replace(PLACEHOLDER_SQL_VALUE, filter.get(key).toString()); 
				
				filters.add(sqlItem);
				continue;
			}

			Field field = getFieldByName(key, fields);

			if (field == null) {
				log.warn("Field Not Found :" + key + " !");
				continue;

			}
			if (field.getAnnotation(Column.class) != null) {
				columnName = getColumnName(field);

			}

			StringBuilder sqlItem = new StringBuilder(doubleQuoteMysql(tableName).concat(".").concat(columnName));

			if (field.getAnnotation(JoinColumn.class) != null || isMultiKey) {

				Class fieldClass 		= field.getType();
				String joinTableName 	= getTableName(fieldClass);
				FormField formField 	= field.getAnnotation(FormField.class);

				try {
					String referenceFieldName = formField.optionItemName();

					if (isMultiKey) {
						referenceFieldName = multiKey[1];
					}

					Field 	fieldField 		= EntityUtil.getDeclaredField(fieldClass, referenceFieldName);
					String 	fieldColumnName = getColumnName(fieldField);

					if (fieldColumnName == null || fieldColumnName.equals("")) {
						fieldColumnName = key;
					}

					sqlItem = sqlItem
							.append(doubleQuoteMysql(joinTableName))
							.append(".")
							.append(doubleQuoteMysql(fieldColumnName));

				} catch ( Exception e) {
					
					log.warn(e.getClass() + " " + e.getMessage() + " " + fieldClass);
					e.printStackTrace();
					
					continue;
				}

			}
			// rollback key to original key
			/*
			 * if (isMultiKey) { key = String.join(",", multiKey); if
			 * (rawKey.endsWith("[EXACTS]")) { key+="[EXACTS]"; } }
			 */

			if (itemContains) {
				sqlItem = sqlItem.append(" LIKE '%").append(filter.get(rawKey)).append("%' ");

			} else if (itemExacts) {
				sqlItem = sqlItem.append(" = '").append(filter.get(rawKey)).append("' ");
			}

			log.info("SQL ITEM: " + sqlItem + " contains :" + itemContains + ", exacts:" + itemExacts);

			filters.add(sqlItem.toString());
		}

		String additionalFilter = "";

		if (rootFilterEntity != null) {
			additionalFilter = addFilterById(entityClass, rootFilterEntity.getClass(), rootFilterEntity.getId());
		}

		if (filters == null || filters.size() == 0) {

			if (rootFilterEntity != null && additionalFilter.isEmpty() == false) {
				return SQL_KEYWORD_WHERE.concat(additionalFilter);
			}

			return "";

		}

		String whereClause = "";

		if (filters.size() > 0) {
			whereClause = String.join(SQL_KEYWORD_AND, filters);
		}

		String result = SQL_KEYWORD_WHERE.concat(whereClause);

		if (additionalFilter.isEmpty()) {

			if (filters.size() == 0) {
				return "";

			}
			return result;
		}

		return result.concat(filters.size() > 0 ? SQL_KEYWORD_AND : " ").concat(additionalFilter);
	}

	public static String addFilterById(Class baseEntityClass, Class rootClass, Object id) {

		CustomEntity customEntity = EntityUtil.getClassAnnotation(baseEntityClass, CustomEntity.class);
		if (customEntity == null || customEntity.rootFilter().length == 0) {

			return "";
		} 

		try {

			String tableName = getTableName(rootClass);
			Field idField = EntityUtil.getIdField(rootClass);

			String idColumnName = getColumnName(idField);

			String filter = doubleQuoteMysql(tableName)
								.concat(".")
								.concat(doubleQuoteMysql(idColumnName))
								.concat("=")
								.concat("'" + id + "'");

			return filter;

		} catch (Exception e) { 
			return "";
		}

	}

	private static String orderSQL(Class entityClass, String orderType, String orderBy) {

		/**
		 * order by field
		 */
		Field orderByField = EntityUtil.getDeclaredField(entityClass, orderBy);

		if (orderByField == null) {
			return null;
		}
		Field idField = EntityUtil.getIdField(entityClass);

		if (idField == null) {
			return null;
		}
		String columnName 	= idField.getName();
		String tableName 	= getTableName(entityClass);

		if (orderByField.getAnnotation(JoinColumn.class) != null) {
			
			Class fieldClass 	= orderByField.getType();
			FormField formField = orderByField.getAnnotation(FormField.class);
			tableName 			= getTableName(fieldClass);
			

			try {
				Field fieldField = fieldClass.getDeclaredField(formField.optionItemName());
				columnName = getColumnName(fieldField);

			} catch ( Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			columnName = getColumnName(orderByField);
		}

		String orderField = doubleQuoteMysql(tableName).concat(".").concat(doubleQuoteMysql(columnName));

		return buildString(SQL_KEYWORD_ORDERBY, orderField, orderType);
	}

	private static String getTableName(Class entityClass) {
		log.info("getTableName From entity class: " + entityClass.getCanonicalName());
		
		Table table = (Table) entityClass.getAnnotation(Table.class);

		if (table != null) {

			if (table.name() != null && !table.name().equals("")) {
				return table.name();
			}
		}
		return entityClass.getSimpleName().toLowerCase();
	}

	private static String[] generateSqlByFilter(Filter filter, Class<? extends BaseEntity> entityClass,
			BaseEntity rootFilterEntity) {

		log.info("CRITERIA-FILTER: {}", filter);
		log.info("entity class: {}", entityClass);
		log.info("root filter entity: {}", rootFilterEntity);

		int		offset 				= filter.getPage() * filter.getLimit();
		boolean withLimit 			= filter.getLimit() > 0;
		boolean withOrder 			= filter.getOrderBy() != null && filter.getOrderType() != null
				&& !filter.getOrderBy().equals("") && !filter.getOrderType().equals("");
		boolean contains 			= filter.isContains();
		boolean exacts 				= filter.isExacts();
		boolean withFilteredField 	= filter.getFieldsFilter() != null;

		String orderType 		= filter.getOrderType();
		String orderBy 			= filter.getOrderBy();
		String orderSQL 		= withOrder ? orderSQL(entityClass, orderType, orderBy) : "";

		String limitOffsetSQL 	= "";
		String filterSQL		= "";

		String tableName 		= getTableName(entityClass);
		String joinSql			= createLeftJoinSQL(entityClass);
		
		if(withLimit) {
			limitOffsetSQL = buildString(
					SQL_KEYWORD_LIMIT,
					String.valueOf(filter.getLimit()), 
					SQL_KEYWORD_OFFSET, 
					String.valueOf(offset));
		}
		
		if(withFilteredField) {
			filterSQL = createFilterSQL(
					entityClass, 
					filter.getFieldsFilter(), 
					contains, 
					exacts, 
					rootFilterEntity);
		}  

		String sql = buildString(
				SQL_KEYWORD_SELECT, 
				doubleQuoteMysql(tableName), 
				".*", 
				SQL_KEYWORD_FROM,
				doubleQuoteMysql(tableName),
				joinSql, 
				filterSQL, 
				orderSQL, 
				limitOffsetSQL);

		String sqlCount = buildString(
				SQL_KEYWORDSET_SELECT_COUNT, 
				doubleQuoteMysql(tableName), 
				joinSql, 
				filterSQL);

		log.info("query select: {}", sql);
		log.info("query count: {}", sqlCount);
		
		return new String[] { sql, sqlCount };
	}

	static String doubleQuoteMysql(String str) {
		return StringUtil.doubleQuoteMysql(str);
	}

	public static void main(String[] sdfdf) {

		Division division = new Division();
		division.setId(1L);
		System.out.println("SQL: " + generateSqlByFilter(Filter.builder().fieldsFilter(new HashMap<String, Object>() {
			{

			}
		}).build(), Event.class, division)[0]);

	}

}
