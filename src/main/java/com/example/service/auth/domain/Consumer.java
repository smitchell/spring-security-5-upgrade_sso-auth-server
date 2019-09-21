package com.example.service.auth.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * This is a simplified ClientDetails implementation. There is one record for each micro-service needing to use
 * oauth2 credentials to access another service. For example, a RabbitMQ consumer does not have end-user credentials
 * when it runs, so if it needs to call the report-service it uses its client id and client secret to authenticate.
 * To simplify the model, there are several comma delimited properties that get split as needed.
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode
public class Consumer implements ClientDetails, Serializable {

  @Id
  private String clientId;
  @Column
  private String resourceIdsCsv;
  @Column
  private String clientSecret;
  @Column
  private String scopeCsv;
  @Column
  private String authorizedGrantTypesCsv;
  @Column
  private String registeredRedirectUrisCsv;
  @Column
  private String authorityCsv;
  @Column
  private Integer accessTokenValiditySeconds;
  @Column
  private Integer refreshTokenValiditySeconds;
  @Column
  private String additionalInfo;
  @Column
  private String autoApproveCsv;

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

  @Override
  @Transient
  public Set<String> getAuthorizedGrantTypes() {
    if (authorizedGrantTypesCsv != null) {
      return new HashSet<>(Arrays.asList(authorizedGrantTypesCsv.split(",")));
    }
    return Collections.emptySet();
  }

  @Override
  @Transient
  public Set<String> getResourceIds() {
    if (resourceIdsCsv != null) {
      return new HashSet<>(Arrays.asList(resourceIdsCsv.split(",")));
    }
    return Collections.emptySet();
  }

  @Override
  @Transient
  public Set<String> getScope() {
    if (scopeCsv != null) {
      return new HashSet<>(Arrays.asList(scopeCsv.split(",")));
    }
    return Collections.emptySet();
  }

  @Override
  @Transient
  public Set<String> getRegisteredRedirectUri() {
    if (registeredRedirectUrisCsv != null) {
      return new HashSet<>(Arrays.asList(registeredRedirectUrisCsv.split(",")));
    }
    return Collections.emptySet();
//    return Collections.emptySet();
  }

  @Override
  @Transient
  public boolean isSecretRequired() {
    return this.clientSecret != null;
  }

  @Override
  @Transient
  public boolean isScoped() {
    return this.scopeCsv != null && !Arrays.asList(scopeCsv.split(",")).isEmpty();
  }

  @Override
  @Transient
  public boolean isAutoApprove(String scope) {
    if (autoApproveCsv == null) {
      return false;
    }
    for (String auto : autoApproveCsv.split(",")) {
      if ("true".equals(auto) || scope.matches(auto)) {
        return true;
      }
    }
    return false;
  }

  @Override
  @Transient
  public Map<String, Object> getAdditionalInformation() {
    return Collections.emptyMap();
  }

  @Override
  @Transient
  public Collection<GrantedAuthority> getAuthorities() {
    if (authorityCsv != null) {
      Set<GrantedAuthority> grantedAuthorityList = new HashSet<>();
      for (String authority : authorityCsv.split(",")) {
        grantedAuthorityList.add(new SimpleGrantedAuthority(authority));
      }
      return grantedAuthorityList;
    }
    return Collections.emptySet();
  }
}
