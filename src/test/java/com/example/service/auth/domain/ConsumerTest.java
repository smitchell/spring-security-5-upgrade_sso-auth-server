package com.example.service.auth.domain;

import com.medzero.test.util.AbstractJavaBeanTest;
import org.junit.Test;

public class ConsumerTest extends AbstractJavaBeanTest<Consumer> {

  @Override
  protected Consumer getBeanInstance() {
    return new Consumer();
  }

  @Override
  @Test
  public void equalsAndHashCodeContract() {
  }
}