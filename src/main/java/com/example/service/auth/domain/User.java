package com.example.service.auth.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;


@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable, UserDetails {

  @Id
  private String username;

  @Column
  private String firstName;

  @Column
  private String lastName;

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
      for (String role : roles.split(",")) {
        grantedAuthorityList.add(new SimpleGrantedAuthority(role));
      }
      return grantedAuthorityList;
    }
    return Collections.emptyList();
  }
}
