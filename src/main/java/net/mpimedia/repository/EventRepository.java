package net.mpimedia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.mpimedia.entity.Event;

public interface EventRepository  extends JpaRepository<Event, Long>, RepositoryCustom<Event>{

	@Query(nativeQuery = true,
			value = "select * from event left join program on event.program_id = program.id "
					+ " left join  section  ON program.section_id =  section.id "
					+ " left join division on division.id =  section.division_id "
					+ " where MONTH(event.date)=?1 AND YEAR(event.date) = ?2 "
					+ " AND division.id=?3"
			)
	List<Event> getByMonthAndYear(int month, int year, long divisionId);
	
	@Query(nativeQuery = true,
			value = "select * from event left join program on event.program_id = program.id "
					+ " left join  section  ON program.section_id =  section.id "
					+ " left join division on division.id =  section.division_id "
					+ " where division.id=?1"
			)
	List<Event> getByDivisionId( long divisionId);

}
