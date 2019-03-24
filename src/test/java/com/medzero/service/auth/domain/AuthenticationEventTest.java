package com.medzero.service.auth.domain;

import com.medzero.test.util.AbstractJavaBeanTest;
import org.junit.Test;

public class AuthenticationEventTest extends AbstractJavaBeanTest<AuthenticationEvent> {

  @Override
  protected AuthenticationEvent getBeanInstance() {
    return new AuthenticationEvent();
  }

  @Override
  @Test
  public void equalsAndHashCodeContract() {
  }
}