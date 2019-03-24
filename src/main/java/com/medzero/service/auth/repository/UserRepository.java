package com.medzero.service.auth.repository;

import com.medzero.service.auth.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByUsername(String username);

}