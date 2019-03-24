package com.medzero.service.auth.controller;

import java.util.Optional;

@FunctionalInterface
public interface AuthServiceMemberController {

  Optional<String> getUsernameByEmailMobile(String username);
}
