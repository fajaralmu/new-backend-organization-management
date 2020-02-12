package net.mpimedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.mpimedia.entity.Post;

public interface PostRepository  extends JpaRepository<Post, Long>, RepositoryCustom<Post>{

}
