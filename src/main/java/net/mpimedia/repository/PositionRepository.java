package net.mpimedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mpimedia.entity.Position;

public interface PositionRepository  extends JpaRepository<Position, Long>, RepositoryCustom<Position>{

}
