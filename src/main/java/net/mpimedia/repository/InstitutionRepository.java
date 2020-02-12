package net.mpimedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mpimedia.entity.Institution;

public interface InstitutionRepository  extends JpaRepository<Institution, Long>, RepositoryCustom<Institution>{

}
