package net.mpimedia.service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
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
import net.mpimedia.repository.RepositoryCustomImpl;
import net.mpimedia.util.CollectionUtil;
import net.mpimedia.util.EntityUtil;
import net.mpimedia.util.LogProxyFactory;
import net.mpimedia.util.QueryUtil;

@Service
@Slf4j
public class EntityService { 

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
				
				return saveEvent(request.getEvent(), newRecord, sessionData);

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

	private WebResponse saveEvent(Event entity, boolean isNewRecord, SessionData sessionData) {
		log.info("save event entity: {}", entity);
		
		entity = copyNewElement(entity, isNewRecord);
		entity.setUser(sessionData.getUser());
		
		BaseEntity savedEntity = mainRepository.save(entity);
		
		accountService.updateEvent(sessionData);
		return WebResponse.builder().entity(savedEntity).build();
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

			/**
			 * Generate query string
			 */
			String[] sqlListAndCount = QueryUtil.generateSqlByFilter(filter, entityClass, selectedDivision);

			String sql = sqlListAndCount[0];
			String sqlCount = sqlListAndCount[1];

			List<BaseEntity> entities = repositoryCustom.filterAndSort(sql, entityClass);

			Integer count = 0;
			Object countResult = repositoryCustom.getSingleResult(sqlCount);

			if (countResult != null) {
				count = ((BigInteger) countResult).intValue();
			}
			
			return WebResponse.builder().
					entities(EntityUtil.validateDefaultValue(entities)).
					totalData(count).
					filter(filter).
					build();
			
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
	
	/**
	 * delete record
	 * @param request
	 * @return
	 */
	public WebResponse delete(WebRequest request) {
		Map<String, Object> filter = request.getFilter().getFieldsFilter();

		SessionData sessionData = sessionService.GetSessionData(request);

		if (request.getEntity() == null || sessionData.getUser() == null) {
			return WebResponse.failed("Unauthorized");

		}

		if (sessionData.getDivision() == null) {
			return WebResponse.failed("Invalid Division");

		}
		
		try {

			Long id 				= Long.parseLong(filter.get("id").toString());
			Class entityToDelete 	= BaseEntity.class;
			
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

	
	 

}
