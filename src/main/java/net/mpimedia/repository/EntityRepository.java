package net.mpimedia.repository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.JoinColumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.mpimedia.entity.BaseEntity;

@Service
@Slf4j
@Data
public class EntityRepository {

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private DivisionRepository divisionRepository;
	@Autowired
	private InstitutionRepository institutionRepository;
	@Autowired
	private MemberRepository memberRepository;
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
	private PositionRepository positionRepository;

	public <T> T save(BaseEntity baseEntity) {
		
		boolean joinEntityExist = validateJoinColumn(baseEntity);
		
		if(!joinEntityExist) {
			
			throw new InvalidParameterException("JOIN COLUMN INVALID");
		}
		
		try {
			JpaRepository repository = findRepo(baseEntity.getClass());
			log.info("found repo: " + repository);
			return (T) repository.save(baseEntity);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	public boolean validateJoinColumn(BaseEntity baseEntity) {
		
		List<Field> joinColumns = getJoinColumn(baseEntity.getClass()); 
		
		if(joinColumns.size() == 0) {
			return true;
		}
		
		for (Field field : joinColumns) {
			
			try {
				Object value = field.get(baseEntity);
				if(value == null || (value  instanceof BaseEntity) == false) {
					continue;
				}
				
				BaseEntity entity = (BaseEntity) value;
				
				JpaRepository repository = findRepo(entity.getClass());
				
				Optional result = repository.findById(entity.getId());
				
				if(result.isPresent() == false) {
					return false;
				}
				
			} catch (IllegalArgumentException | IllegalAccessException e) { 
				e.printStackTrace();
			}
			
		}
		
		return true;
	}
	
	
	public List<Field> getJoinColumn(Class<? extends BaseEntity> clazz) {
		
		List<Field> joinColumns  	= new ArrayList<>();
		Field[] 	fields 			= clazz.getFields();
		
		for (Field field : fields) {
			if(field.getAnnotation(JoinColumn.class) != null) {
				joinColumns.add(field);
			}
		}
				
		return joinColumns;
	}

	public JpaRepository findRepo(Class<? extends BaseEntity> entityClass) {

		Class<?> clazz = this.getClass(); 
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {

			if (field.getAnnotation(Autowired.class) == null) {
				continue;
			}
			Class<?> fieldClass = field.getType();

			Class<?> originalEntityClass = getGenericClassIndexZero(fieldClass);
			if (originalEntityClass.equals(entityClass)) {
				try {
					return (JpaRepository) field.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					return null;
				}
			}
		}

		return null;
	}

	public static <T> T getGenericClassIndexZero(Class clazz) {
		Type[] interfaces = clazz.getGenericInterfaces();

		if (interfaces == null) {
			return null;
		}
		for (Type type : interfaces) {

			if (type.getTypeName().startsWith(JpaRepository.class.getCanonicalName())) {
				ParameterizedType parameterizedType = (ParameterizedType) type;

				if (parameterizedType.getActualTypeArguments() != null) {
					return (T) parameterizedType.getActualTypeArguments()[0];
				}
			}
		}

		return null;
	}

	public void deleteById(Long id, Class<? extends BaseEntity> class1) {
		log.info("Will delete entity: {}, id: {}", class1.getClass(), id);
		JpaRepository repository = findRepo(class1);
		repository.deleteById(id);
	}

}
