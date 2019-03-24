package com.medzero.service.auth.repository;

import com.medzero.service.auth.domain.AuthenticationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationEventRepository extends JpaRepository<AuthenticationEvent, String> {

}
