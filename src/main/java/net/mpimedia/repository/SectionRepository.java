package net.mpimedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mpimedia.entity.Section;

public interface SectionRepository  extends JpaRepository<Section, Long>, RepositoryCustom<Section>{

}
