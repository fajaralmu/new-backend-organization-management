package net.mpimedia.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
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
import net.mpimedia.entity.Post;
import net.mpimedia.entity.Program;
import net.mpimedia.entity.RegisteredRequest;
import net.mpimedia.entity.Section; 
import net.mpimedia.entity.User; 
import net.mpimedia.repository.DivisionRepository;
import net.mpimedia.repository.EventRepository;
import net.mpimedia.repository.InstitutionRepository;
import net.mpimedia.repository.MemberRepository;
import net.mpimedia.repository.PostRepository;
import net.mpimedia.repository.ProgramRepository;
import net.mpimedia.repository.RegisteredRequestRepository;
import net.mpimedia.repository.RepositoryCustomImpl;
import net.mpimedia.repository.SectionRepository;
import net.mpimedia.repository.UserRepository;
import net.mpimedia.util.CollectionUtil;
import net.mpimedia.util.EntityUtil;

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

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public WebResponse addEntity(WebRequest request, boolean newRecord) {

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

		}

		return WebResponse.builder().code("01").message("failed").build();
	}

	private WebResponse saveRegisteredRequest(RegisteredRequest registeredRequest, boolean newRecord) {
		registeredRequest = (RegisteredRequest) copyNewElement(registeredRequest, newRecord);
		RegisteredRequest newRegisteredRequest = registeredRequestRepository.save(registeredRequest);
		return WebResponse.builder().entity(newRegisteredRequest).build();
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
		return EntityUtil.copyFieldElementProperty(source, source.getClass(), !newRecord);
	}

	private WebResponse saveDivision(Division division, boolean newRecord) {

		division = (Division) copyNewElement(division, newRecord);

		 
		Division newDivision = divisionRepository.save(division);
		return WebResponse.builder().entity(newDivision).build();
	}
	
	
	
	private List removeNullItemFromArray(String[] array) {
		List<Object> result = new ArrayList<>();
		for (String string : array) {
			if(string!=null) {
				result.add(string);
			}
		}
		return result;
		
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
		Class<? extends BaseEntity> entityClass = null;

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

		}
		Filter filter = request.getFilter();
		String[] sqlListAndCount = generateSqlByFilter(filter, entityClass);
		String sql = sqlListAndCount[0];
		String sqlCount = sqlListAndCount[1];
		List<BaseEntity> entities = getEntitiesBySql(sql, entityClass);
		Integer count = 0;
		Object countResult = repositoryCustom.getSingleResult(sqlCount);
		if (countResult != null) {
			count = ((BigInteger) countResult).intValue();
		}
		return WebResponse.builder().entities(entities).totalData(count).filter(filter).build();
	}

	/**
	 * 
	 * @param filter
	 * @param entityClass
	 * @return sql & sqlCount
	 */
	private String[] generateSqlByFilter(Filter filter, Class<? extends BaseEntity> entityClass) {

//		String entityName = request.getEntity();
		Integer offset = filter.getPage() * filter.getLimit();
		boolean withLimit = filter.getLimit() > 0;
		boolean withOrder = filter.getOrderBy() != null && filter.getOrderType() != null
				&& !filter.getOrderBy().equals("") && !filter.getOrderType().equals("");
		boolean contains = filter.isContains();
		boolean exacts = filter.isExacts();
		boolean withFilteredField = filter.getFieldsFilter().isEmpty() == false;
		
		String orderType = filter.getOrderType();
		String orderBy = filter.getOrderBy();
		String tableName = getTableName(entityClass);
		String orderSQL = withOrder ? orderSQL(entityClass, orderType, orderBy) : "";
		String limitOffsetSQL = withLimit ? " LIMIT " + filter.getLimit() + " OFFSET " + offset : "";
		String filterSQL = withFilteredField ? createFilterSQL(entityClass, filter.getFieldsFilter(), contains, exacts)
				: "";
		String joinSql = createLeftJoinSQL(entityClass);
		String sql = "select  `" + tableName + "`.* from `" + tableName + "` " + joinSql + " " + filterSQL + orderSQL
				+ limitOffsetSQL;
		String sqlCount = "select COUNT(*) from `" + tableName + "` " + joinSql + " " + filterSQL;
		return new String[] { sql, sqlCount };
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
		String columnName = ((Column) field.getAnnotation(Column.class)).name();
		if (columnName == null || columnName.equals("")) {
			columnName = field.getName();
		}
		return columnName;
	}

	private static String createLeftJoinSQL(Class entityClass) {
		String tableName = getTableName(entityClass);
		String sql = "";
		List<Field> fields = EntityUtil.getDeclaredFields(entityClass);
		for (Field field : fields) {
			JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
			if (joinColumn != null) {
				Class fieldClass = field.getType();
				String foreignID = joinColumn.name();
				String joinTableName = getTableName(fieldClass);
				Field idForeignField = EntityUtil.getIdField(fieldClass);
				String sqlItem = " LEFT JOIN `$JOIN_TABLE` ON  `$JOIN_TABLE`.`$JOIN_ID` = `$ENTITY_TABLE`.`$FOREIGN_ID` ";
				sqlItem = sqlItem.replace("$FOREIGN_ID", foreignID).replace("$JOIN_TABLE", joinTableName)
						.replace("$ENTITY_TABLE", tableName).replace("$JOIN_ID", getColumnName(idForeignField));
				sql += sqlItem;

			}
		}
		return sql;
	}

	public static void main(String[] sdfdf) {
		String ss = "FAJAR [EXACTS]";
		System.out.println(ss.split("\\[EXACTS\\]")[0]);

	}

	private static String createFilterSQL(Class entityClass, Map<String, Object> filter, boolean contains,
			boolean exacts) {
		String tableName = getTableName(entityClass);
		List<String> filters = new ArrayList<String>();
		List<Field> fields = EntityUtil.getDeclaredFields(entityClass);
		log.info("=======FILTER: {}", filter);
		for (final String rawKey : filter.keySet()) {
			System.out.println("................." + rawKey + ":" + filter.get(rawKey));
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
			System.out.println("-------KEY:" + key);
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
				String sqlItem = " $MODE(`$TABLE_NAME`.`$COLUMN_NAME`) = $VALUE ";
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
					System.out.println("!!!!!!!!!!! FIELD NOT FOUND: " + fieldName);
					continue;
				}
				columnName = getColumnName(field);
				sqlItem = sqlItem.replace("$TABLE_NAME", tableName).replace("$MODE", mode)
						.replace("$COLUMN_NAME", columnName).replace("$VALUE", filter.get(key).toString());
				filters.add(sqlItem);
				continue;
			}

			Field field = getFieldByName(key, fields);

			if (field == null) {
				System.out.println("!!!!!!!Field Not Found :" + key);
				continue;
			}
			if (field.getAnnotation(Column.class) != null)
				columnName = getColumnName(field);

			String sqlItem = " `" + tableName + "`.`" + columnName + "` ";
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
					sqlItem = " `" + joinTableName + "`.`" + fieldColumnName + "` ";
				} catch (SecurityException e) {
					e.printStackTrace();
					System.out.println("!!!!!" + e.getClass() + " " + e.getMessage() + " " + fieldClass);
					continue;
				}

			}
			// rollback key to original key
			/*
			 * if (isMultiKey) { key = String.join(",", multiKey); if
			 * (rawKey.endsWith("[EXACTS]")) { key+="[EXACTS]"; } }
			 */
			if (itemContains) {
				sqlItem += " LIKE '%" + filter.get(rawKey) + "%' ";
			} else if (itemExacts) {
				sqlItem += " = '" + filter.get(rawKey) + "' ";
			}
			System.out.println("SQL ITEM: " + sqlItem + " contains :" + itemContains + ", exacts:" + itemExacts);
			filters.add(sqlItem);
		}
		if(filters == null || filters.size() == 0) {
			return "";
		}
		return " WHERE " + String.join(" AND ", filters);
	}

	private static String orderSQL(Class entityClass, String orderType, String orderBy) {
		// set order by
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

		String orderField = "`" + tableName + "`.`" + columnName + "`";
		return " ORDER BY " + orderField + " " + orderType;
	}

	private static String getTableName(Class entityClass) {
		System.out.println("entity class: " + entityClass.getCanonicalName());
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
			switch (request.getEntity()) {
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
			case "registeredRequest":
				registeredRequestRepository.deleteById(id);
			}
			return WebResponse.builder().code("00").message("deleted successfully").build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return WebResponse.builder().code("01").message("failed").build();
		}
	}
 
}
