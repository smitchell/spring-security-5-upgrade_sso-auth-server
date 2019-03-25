package com.example.service.auth.domain;

import com.medzero.test.util.AbstractJavaBeanTest;
import org.junit.Test;

public class UserTest extends AbstractJavaBeanTest<User> {

  @Override
  protected User getBeanInstance() {
    return new User();
  }

  @Override
  @Test
  public void equalsAndHashCodeContract() {
  }
}