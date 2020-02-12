package net.mpimedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mpimedia.entity.RegisteredRequest;

public interface RegisteredRequestRepository extends JpaRepository<RegisteredRequest, Long>{

	RegisteredRequest findTop1ByRequestId(String requestId);

}
