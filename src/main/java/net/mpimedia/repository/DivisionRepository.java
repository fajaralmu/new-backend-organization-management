package net.mpimedia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mpimedia.entity.Division;
import net.mpimedia.entity.Institution;

public interface DivisionRepository  extends JpaRepository<Division, Long>, RepositoryCustom<Division>{

	List<Division> findByInstitution(Institution institution);

}
