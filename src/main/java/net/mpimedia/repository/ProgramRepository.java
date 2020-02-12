package net.mpimedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mpimedia.entity.Program;

public interface ProgramRepository  extends JpaRepository<Program, Long>, RepositoryCustom<Program>{

}
