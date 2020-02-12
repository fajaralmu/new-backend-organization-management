package net.mpimedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mpimedia.entity.Message;

public interface MessageRepository extends JpaRepository<Message		, Long>{
 

}
