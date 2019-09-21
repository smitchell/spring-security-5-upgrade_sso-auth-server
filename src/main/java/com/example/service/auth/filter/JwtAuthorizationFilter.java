package com.example.service.auth.filter;

import com.example.service.auth.constants.SecurityConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private final String privateKey;

  public JwtAuthorizationFilter(final String privateKey, AuthenticationManager authenticationManager) {
    super(authenticationManager);
    this.privateKey = privateKey;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {
    log.debug("doFilterInternal()");
    var authentication = getAuthentication(request);
    var header = request.getHeader(SecurityConstants.TOKEN_HEADER);

    if (StringUtils.isEmpty(header) || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    log.debug("UsernamePasswordAuthenticationToken()");
    var token = request.getHeader(SecurityConstants.TOKEN_HEADER);
    if (StringUtils.isNotEmpty(token)) {
      try {
        var signingKey = privateKey.getBytes();

        var parsedToken = Jwts.parser()
            .setSigningKey(signingKey)
            .parseClaimsJws(token.replace("Bearer ", ""));

        var username = parsedToken
            .getBody()
            .getSubject();

        var authorities = ((List<?>) parsedToken.getBody()
            .get("roles")).stream()
            .map(authority -> new SimpleGrantedAuthority((String) authority))
            .collect(Collectors.toList());

        if (StringUtils.isNotEmpty(username)) {
          UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
          log.debug("UsernamePasswordAuthenticationToken() ----> " + new ObjectMapper().writeValueAsString(usernamePasswordAuthenticationToken));
          return usernamePasswordAuthenticationToken;
        }
      } catch (ExpiredJwtException exception) {
        log.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
      } catch (UnsupportedJwtException exception) {
        log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
      } catch (MalformedJwtException exception) {
        log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
      } catch (SignatureException exception) {
        log.warn("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
      } catch (IllegalArgumentException exception) {
        log.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
      }catch (Exception exception) {
        log.error(exception.getMessage(), exception);
      }
    }

    return null;
  }
}
