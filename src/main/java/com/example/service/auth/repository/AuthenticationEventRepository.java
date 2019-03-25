package com.example.service.auth.repository;

import com.example.service.auth.domain.AuthenticationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationEventRepository extends JpaRepository<AuthenticationEvent, String> {

}
