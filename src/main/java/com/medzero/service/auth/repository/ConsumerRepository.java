package com.medzero.service.auth.repository;


import com.medzero.service.auth.domain.Consumer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ConsumerRepository extends JpaRepository<Consumer, String> {

  Optional<Consumer> findByClientId(String clientId);

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  <S extends Consumer> S save(S s);

}