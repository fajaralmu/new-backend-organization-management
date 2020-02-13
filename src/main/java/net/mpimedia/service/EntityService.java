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
import net.mpimedia.repository.DivisionRepository;
import net.mpimedia.repository.EventRepository;
import net.mpimedia.repository.InstitutionRepository;
import net.mpimedia.repository.MemberRepository;
import net.mpimedia.repository.PositionRepository;
import net.mpimedia.repository.PostRepository;
import net.mpimedia.repository.ProgramRepository;
import net.mpimedia.repository.RegisteredRequestRepository;
import net.mpimedia.repository.RepositoryCustomImpl;
import net.mpimedia.repository.SectionRepository;
import net.mpimedia.repository.UserRepository;
import net.mpimedia.util.EntityUtil;
import net.mpimedia.util.StringUtil;
@Service
@Slf4j
public class EntityService {

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private DivisionRepository divisionRepository;
	@Autowired
	private InstitutionRepository institutionRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private RepositoryCustomImpl repositoryCustom;
	@Autowired
	private ProgramRepository programRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SectionRepository sectionRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private RegisteredRequestRepository registeredRequestRepository;
	@Autowired
	private FileService fileService;
	@Autowired
	private PositionRepository positionRepository;
	@Autowired
	private SessionService sessionService;
	@Autowired
	private TemporaryDataService temporaryDataService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public WebResponse addEntity(WebRequest request, boolean newRecord) {

		try {
			
			switch (request.getEntity().toLowerCase()) {
			
				case "institution":
					return saveInstitution(request.getInstitution(), newRecord);
	
				case "division":
					return saveDivision(request.getDivision(), newRecord);
	
				case "post":
					return savePost(request.getPost(), newRecord);
	
				case "member":
					return saveMember(request.getMember(), newRecord);
	
				case "user":
					return saveUser(request.getUser(), newRecord);
	
				case "program":
					return saveProgram(request.getProgram(), newRecord);
	
				case "section":
					return saveSection(request.getSection(), newRecord);
	
				case "event":
					return saveEvent(request.getEvent(), newRecord);
					
				case "registeredrequest":
					return saveRegisteredRequest(request.getRegisteredRequest(), newRecord);
	
				case "position":
					return savePosition(request.getPosition(), newRecord);
				

			}
			
		} catch (Exception ex) {
			
			return WebResponse.builder().code("01").message(ex.getMessage()).build();
		}
		return WebResponse.failed();

	}

	private WebResponse saveRegisteredRequest(RegisteredRequest registeredRequest, boolean newRecord) {
		registeredRequest = (RegisteredRequest) copyNewElement(registeredRequest, newRecord);
		RegisteredRequest newRegisteredRequest = registeredRequestRepository.save(registeredRequest);
		return WebResponse.builder().entity(newRegisteredRequest).build();
	}

	private WebResponse savePosition(Position entity, boolean newRecord) {
		
		entity = (Position) copyNewElement(entity, newRecord);
		Position savedEntity = positionRepository.save(entity);
		return WebResponse.builder().entity(savedEntity).build();
	}

	private WebResponse saveEvent(Event entity, boolean newRecord) {
		
		entity = (Event) copyNewElement(entity, newRecord);
		Event savedEntity = eventRepository.save(entity);
		return WebResponse.builder().entity(savedEntity).build();
	}

	private WebResponse saveSection(Section section, boolean newRecord) {
		
		section = (Section) copyNewElement(section, newRecord);
		Section newSection = sectionRepository.save(section);
		return WebResponse.builder().entity(newSection).build();
	}

	private WebResponse saveUser(User user, boolean newRecord) {
		
		user = (User) copyNewElement(user, newRecord);
		User newUser = userRepository.save(user);
		return WebResponse.builder().entity(newUser).build();
	}

	private WebResponse saveProgram(Program program, boolean newRecord) {
		
		program = (Program) copyNewElement(program, newRecord); 
		Program newProgram = programRepository.save(program);
		return WebResponse.builder().entity(newProgram).build();
	}

	private WebResponse saveMember(Member member, boolean newRecord) {
		
		member = (Member) copyNewElement(member, newRecord); 
		Member newMember = memberRepository.save(member);
		return WebResponse.builder().entity(newMember).build();
	}

	private WebResponse savePost(Post post, boolean newRecord) {
		
		post = (Post) copyNewElement(post, newRecord);
		Post newPost = postRepository.save((post));
		return WebResponse.builder().entity(newPost).build();
	}

	private Object copyNewElement(Object source, boolean newRecord) {
		
		Object result = EntityUtil.copyFieldElementProperty(source, source.getClass(), !newRecord);  
		return result;
	}

	private WebResponse saveDivision(Division division, boolean newRecord) {
		
		division = (Division) copyNewElement(division, newRecord);
		Division newDivision = divisionRepository.save(division);
		
		temporaryDataService.init();
		return WebResponse.builder().entity(newDivision).build();
	} 

	private WebResponse saveInstitution(Institution institution, boolean newRecord) {
		
		institution = (Institution) copyNewElement(institution, newRecord);
		Institution newInstitution = institutionRepository.save(institution);
		return WebResponse.builder().entity(newInstitution).build();
	}

	public List<Institution> getAllInstitution() {

		return institutionRepository.findAll();
	}

	public WebResponse filter(WebRequest request) {
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		SessionData sessionData = sessionService.GetSessionData(request);
		
		if(request.getEntity() == null || sessionData.getUser() == null ) {
			return WebResponse.failed("Unauthorized");
		
		}
		
		if(sessionData.getDivision() == null) {
			return WebResponse.failed("Invalid Division");
			
		}
		
		Division selectedDivision = sessionData.getDivision();
		
		Class<? extends BaseEntity> entityClass = null;

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
					entityClass = Program.class;
					break;
	
				case "section":
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
			
			Filter filter = request.getFilter();
			
			if(filter == null) {
				filter= new Filter();
			}
			if(filter.getFieldsFilter() == null) {
				filter.setFieldsFilter(new HashMap<>());
			}
			
			String[] sqlListAndCount = generateSqlByFilter(filter, entityClass, selectedDivision);
			
			String sql = sqlListAndCount[0];
			String sqlCount = sqlListAndCount[1];
			
			List<BaseEntity> entities = getEntitiesBySql(sql, entityClass);
			
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

	

	public List<BaseEntity> getEntitiesBySql(String sql, Class<? extends BaseEntity> entityClass) {
		
		List<BaseEntity> entities = repositoryCustom.filterAndSort(sql, entityClass);
		return EntityUtil.validateDefaultValue(entities);
	}

	private static Field getFieldByName(String name, List<Field> fields) {
		for (Field field : fields) {
			if (field.getName().equals(name)) {
				return field;
			}
		}
		return null;
	}

	private static String getColumnName(Field field) {
		log.info(field.getDeclaringClass() + " from " + field.getName());

		if (field.getAnnotation(Column.class) == null)
			return field.getName();
		String columnName = ((Column) field.getAnnotation(Column.class)).name();
		if (columnName == null || columnName.equals("")) {
			columnName = field.getName();
		}
		return columnName;
	}

	public static <T> T getClassAnnotation(Class<?> entityClass, Class annotation) {
		try {
			return (T) entityClass.getAnnotation(annotation);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static <T> T getFieldAnnotation(Field field, Class annotation) {
		try {
			return (T) field.getAnnotation(annotation);
		} catch (Exception e) {
			return null;
		}
	}

	static boolean existInList(Object o, List l) {
		for (Object object : l) {
			if (object.equals(o)) {
				return true;
			}
		}
		return false;
	}

	public static String createLeftJoinSql(Class entityClass, Field field) {
		
		log.info("Create item sql left join: "+entityClass+", field: "+field);
		
		JoinColumn joinColumn = getFieldAnnotation(field, JoinColumn.class);
		 
		if(null == joinColumn) {
			return "";
		} 

		String tableName = getTableName(entityClass);
		Class fieldClass = field.getType();
		String foreignID = joinColumn.name();
		String joinTableName = getTableName(fieldClass);
		Field idForeignField = EntityUtil.getIdField(fieldClass);

		String sqlItem = " LEFT JOIN `${JOIN_TABLE}` ON  `${JOIN_TABLE}`.`${JOIN_ID}` = `${ENTITY_TABLE}`.`${FOREIGN_ID}` ";

		sqlItem = sqlItem
					.replace("${FOREIGN_ID}", foreignID)
					.replace("${JOIN_TABLE}", joinTableName)
					.replace("${ENTITY_TABLE}", tableName)
					.replace("${JOIN_ID}", getColumnName(idForeignField));
		
		return sqlItem;

	}

	private static String createLeftJoinSQL(Class entityClass) {
		String tableName = getTableName(entityClass);
		StringBuilder sql = new StringBuilder("");

		CustomEntity customModel = getClassAnnotation(entityClass, CustomEntity.class);

		List<Field> fields = EntityUtil.getDeclaredFields(entityClass);
		for (Field field : fields) {

			if (customModel != null && existInList(field.getName(), Arrays.asList(customModel.rootFilter()))) {
				continue;
			}

			JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
			if (joinColumn != null) { 

				String sqlItem = createLeftJoinSql(entityClass, field);

				sql = sql.append(sqlItem);

			}
		}
		
		if(customModel!=null && customModel.rootFilter().length>0) {
			sql = sql.append(validateRootFilter(entityClass, customModel.rootFilter()));
		}
		
		return sql.toString();
	}
	
	
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

		String tableName = getTableName(entityClass);
		List<String> filters = new ArrayList<String>();
		List<Field> fields = EntityUtil.getDeclaredFields(entityClass);
		
		log.info("=======FILTER: {}", filter);
		
		for (final String rawKey : filter.keySet()) {
			log.info("................." + rawKey + ":" + filter.get(rawKey));
			
			String key = rawKey;
			if (filter.get(rawKey) == null)
				continue;

			boolean itemExacts = exacts;
			boolean itemContains = contains;

			if (rawKey.endsWith("[EXACTS]")) {
				itemExacts = true;
				itemContains = false;
				key = rawKey.split("\\[EXACTS\\]")[0];
			}
			
			log.info("-------KEY:" + key);
			
			String[] multiKey = key.split(",");
			boolean isMultiKey = multiKey.length > 1;
			
			if (isMultiKey) {
				key = multiKey[0];
			}

			String columnName = key;
			// check if date
			boolean dayFilter = key.endsWith("-day");
			boolean monthFilter = key.endsWith("-month");
			boolean yearFilter = key.endsWith("-year");
			
			if (dayFilter || monthFilter || yearFilter) {
				
				String fieldName = key;
				String mode = "DAY";
				String sqlItem = " $MODE(`${TABLE_NAME}`.`${COLUMN_NAME}`) = ${VALUE} ";
				if (dayFilter) {
					fieldName = key.replace("-day", "");
					mode = "DAY";

				} else if (monthFilter) {
					fieldName = key.replace("-month", "");
					mode = "MONTH";
					
				} else if (yearFilter) {
					fieldName = key.replace("-year", "");
					mode = "YEAR";
					
				}
				
				Field field = getFieldByName(fieldName, fields);
				
				if (field == null) {
					log.warn("!!!!!!!!!!! FIELD NOT FOUND: " + fieldName);
					continue;
					
				}
				
				columnName = getColumnName(field);
				sqlItem = sqlItem.replace("${TABLE_NAME}", tableName).replace("${MODE}", mode)
						.replace("${COLUMN_NAME}", columnName).replace("${VALUE}", filter.get(key).toString());
				filters.add(sqlItem);
				continue;
			}

			Field field = getFieldByName(key, fields);

			if (field == null) {				
				log.warn("!!!!!!!Field Not Found :" + key);
				continue;
				
			}
			if (field.getAnnotation(Column.class) != null) {			
				columnName = getColumnName(field);
				
			}

			StringBuilder sqlItem = new StringBuilder(doubleQuoteMysql(tableName ).concat(".").concat( columnName));
			
			if (field.getAnnotation(JoinColumn.class) != null || isMultiKey) {
				
				Class fieldClass = field.getType();
				String joinTableName = getTableName(fieldClass);
				FormField formField = field.getAnnotation(FormField.class);
				
				try {
					String referenceFieldName = formField.optionItemName();
					
					if (isMultiKey) {
						referenceFieldName = multiKey[1];
					}
					
					Field fieldField = EntityUtil.getDeclaredField(fieldClass, referenceFieldName);
					String fieldColumnName = getColumnName(fieldField);
					
					if (fieldColumnName == null || fieldColumnName.equals("")) {
						fieldColumnName = key;
					}
					
					sqlItem = sqlItem.append(doubleQuoteMysql(joinTableName).concat(".").concat(doubleQuoteMysql(fieldColumnName)));
					
				} catch (SecurityException e) {
					e.printStackTrace();
					log.warn("!!!!!" + e.getClass() + " " + e.getMessage() + " " + fieldClass);
					continue;
				}

			}
			// rollback key to original key
			/*
			 * if (isMultiKey) { key = String.join(",", multiKey); if
			 * (rawKey.endsWith("[EXACTS]")) { key+="[EXACTS]"; } }
			 */
			
			if (itemContains) {
				sqlItem = sqlItem.append(" LIKE '%" ).append(filter.get(rawKey)).append("%' ");
				
			} else if (itemExacts) {				
				sqlItem  = sqlItem.append(" = '").append(filter.get(rawKey)).append("' ");
			}
			
			log.info("SQL ITEM: " + sqlItem + " contains :" + itemContains + ", exacts:" + itemExacts);
			
			filters.add(sqlItem.toString());
		}
		
		String additionalFilter = "";
		
		if(rootFilterEntity != null) {			 
			additionalFilter = addFilterById(entityClass, rootFilterEntity.getClass(),  rootFilterEntity.getId());
		}
		
		if (filters == null || filters.size() == 0) {			
			
			 if(rootFilterEntity != null && additionalFilter.isEmpty() == false) { 				 
				return "WHERE ".concat(additionalFilter);
			}
			
			return "";
			
		}
		
		String whereClause = "";
		
		if(filters.size() > 0) {
			whereClause = String.join(" AND ", filters);
		}
		
		String result = " WHERE " + whereClause;
		
		if(additionalFilter.isEmpty()) {
			
			if(filters.size() == 0) {
				return "";
				
			}
			return result;
		}
		
		return result.concat(filters.size() > 0? " AND " : " ").concat(additionalFilter);
	}

	public static String addFilterById(Class baseEntityClass , Class rootClass, Object id) {
		 
		CustomEntity customEntity = getClassAnnotation(baseEntityClass, CustomEntity.class);
		if(customEntity == null || customEntity.rootFilter().length == 0) {
			
			return "";
		}
		
		String rootFilterName = customEntity.rootFilter()[customEntity.rootFilter().length-1];
		 
		
		try { 
			
			String tableName = getTableName(rootClass);
			Field idField = EntityUtil.getIdField(rootClass);
			
			String idColumnName = getColumnName(idField);
			
			String filter = buildString(
					doubleQuoteMysql(tableName).
					concat(".").
					concat(doubleQuoteMysql(idColumnName)).
					concat("=").
					concat("'"+id+"'"));
			
			return filter;
		
		}catch (Exception e) {
			// TODO: handle exception
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
		String columnName = idField.getName();
		String tableName = getTableName(entityClass);
		
		if (orderByField.getAnnotation(JoinColumn.class) != null) {
			Class fieldClass = orderByField.getType();
			tableName = getTableName(fieldClass);
			FormField formField = orderByField.getAnnotation(FormField.class);
			
			try {
				Field fieldField = fieldClass.getDeclaredField(formField.optionItemName());
				columnName = getColumnName(fieldField);

			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			columnName = getColumnName(orderByField);
		}

		
		String orderField = doubleQuoteMysql(tableName).concat(".").concat(doubleQuoteMysql(columnName));
		
		return buildString(" ORDER BY ", orderField, orderType);
	}

	private static String getTableName(Class entityClass) {
		log.info("entity class: " + entityClass.getCanonicalName());
		Table table = (Table) entityClass.getAnnotation(Table.class);
		
		if (table != null) {
			
			if (table.name() != null && !table.name().equals("")) {
				return table.name();
			}
		}
		return entityClass.getSimpleName().toLowerCase();
	}

	public WebResponse delete(WebRequest request) {
		Map<String, Object> filter = request.getFilter().getFieldsFilter();
		
		try {
			
			Long id = Long.parseLong(filter.get("id").toString());
			switch (request.getEntity().toLowerCase()) {
			case "institution":
				institutionRepository.deleteById(id);
				break;
			case "division":
				divisionRepository.deleteById(id);
				break;
			case "post":
				postRepository.deleteById(id);
				break;
			case "member":
				memberRepository.deleteById(id);
				break;
			case "user":
				userRepository.deleteById(id);
				break;
			case "program":
				programRepository.deleteById(id);
				break;
			case "section":
				sectionRepository.deleteById(id);
				break;
			case "event":
				eventRepository.deleteById(id);
				break;
			case "registeredrequest":
				registeredRequestRepository.deleteById(id);
			case "position":
				positionRepository.deleteById(id);
			default:
				return WebResponse.failed();
				
			}
			
			return WebResponse.builder().code("00").message("deleted successfully").build();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return WebResponse.builder().code("01").message("failed").build();
		}
	}
	
   private static String[] generateSqlByFilter(Filter filter, Class<? extends BaseEntity> entityClass, BaseEntity rootFilterEntity) {
 
		log.info("CRITERIA-FILTER: {}",filter);
		
		Integer offset = filter.getPage() * filter.getLimit();
		boolean withLimit = filter.getLimit() > 0;
		boolean withOrder = filter.getOrderBy() != null && filter.getOrderType() != null
				&& !filter.getOrderBy().equals("") && !filter.getOrderType().equals("");
		boolean contains = filter.isContains();
		boolean exacts = filter.isExacts();
		boolean withFilteredField = filter.getFieldsFilter() != null;

		String orderType = filter.getOrderType();
		String orderBy = filter.getOrderBy();
		String tableName = getTableName(entityClass);
		String orderSQL = withOrder ? orderSQL(entityClass, orderType, orderBy) : "";
		
		String limitOffsetSQL = withLimit ? buildString(
				"LIMIT",  String.valueOf(filter.getLimit()), 
				"OFFSET", String.valueOf(offset)) : "";
		
		String filterSQL = withFilteredField ? 
				createFilterSQL(entityClass, filter.getFieldsFilter(), contains, exacts, rootFilterEntity)
				: "";
		
		String joinSql = createLeftJoinSQL(entityClass);

		String sql = buildString("select ", doubleQuoteMysql(tableName), ".* from ", 
				doubleQuoteMysql(tableName),  joinSql,   filterSQL,   orderSQL, limitOffsetSQL);
		
		String sqlCount = buildString("select COUNT(*) from  ", doubleQuoteMysql(tableName),   joinSql, filterSQL);

		return new String[] { sql, sqlCount };
	}
	
	static String doubleQuoteMysql(String str) {
		return StringUtil.doubleQuoteMysql(str);
	}
	
	public static void main(String[] sdfdf) {
		 
		Division division = new Division();
		division.setId(1L);
		System.out.println("SQL: "+generateSqlByFilter(Filter.builder().fieldsFilter(new HashMap<String, Object>(){
			{
				 
			}
		}).build(), Event.class, division)[0]);

	}

}
