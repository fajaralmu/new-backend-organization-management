package net.mpimedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mpimedia.entity.User;

public interface UserRepository  extends JpaRepository<User, Long>, RepositoryCustom<User>{

	User findByUsernameAndPassword(String username, String password);

}
