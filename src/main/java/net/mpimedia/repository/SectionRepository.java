package net.mpimedia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mpimedia.entity.Division;
import net.mpimedia.entity.Section;

public interface SectionRepository  extends JpaRepository<Section, Long>, RepositoryCustom<Section>{

	List<Section> findByDivision(Division division);

}
