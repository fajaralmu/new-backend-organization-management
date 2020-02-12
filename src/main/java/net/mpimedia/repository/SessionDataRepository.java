package net.mpimedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mpimedia.entity.SessionData;

public interface SessionDataRepository extends JpaRepository<SessionData, Long> {

	public SessionData findTop1ByKey(String key);
}
