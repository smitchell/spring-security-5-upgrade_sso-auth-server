package com.example.service.auth.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable, UserDetails {

  public static final String UUID_PATTERN = "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}$";

  @Id
  @Pattern(regexp = UUID_PATTERN)
  private String username;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date created;

  @CreatedBy
  private String createdBy;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @LastModifiedBy
  private String modifiedBy;

  @Column
  @NotNull
  private String password;

  @Column
  private boolean active;

  @Column
  private String roles;

  @Override
  @Transient
  public boolean isAccountNonExpired() {
    return active;
  }

  @Override
  @Transient
  public boolean isAccountNonLocked() {
    return active;
  }

  @Override
  @Transient
  public boolean isCredentialsNonExpired() {
    return active;
  }

  @Override
  @Transient
  public boolean isEnabled() {
    return active;
  }

  @Override
  @Transient
  public Collection<GrantedAuthority> getAuthorities() {
    if (null != roles && !roles.isEmpty()) {
      Set<GrantedAuthority> grantedAuthorityList = new HashSet<>();
      for (String role : Arrays.asList(roles.split(","))) {
        grantedAuthorityList.add(new SimpleGrantedAuthority(role));
      }
      return grantedAuthorityList;
    }
    return Collections.emptyList();
  }
}