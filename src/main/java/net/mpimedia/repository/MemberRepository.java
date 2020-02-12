package net.mpimedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mpimedia.entity.Member;

public interface MemberRepository  extends JpaRepository<Member, Long>, RepositoryCustom<Member>{

}
