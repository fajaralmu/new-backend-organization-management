package net.mpimedia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.mpimedia.entity.Program;

public interface ProgramRepository extends JpaRepository<Program, Long>, RepositoryCustom<Program> {

	@Query(value = "select  * from    `program`  "
			+ " LEFT JOIN `section` ON  `section`.`id` = `program`.`section_id` "
			+ " LEFT JOIN `division` ON  `division`.`id` = `section`.`division_id`"
			+ " WHERE   `division` . `id` =?1", nativeQuery = true)
	public List<Program> getProgramsByDivisionId(long divisionId);
}
