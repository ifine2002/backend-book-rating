package vn.ifine.service.impl;

import com.nimbusds.jose.util.Base64;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import vn.ifine.dto.response.ResLoginDTO;
import vn.ifine.service.JwtService;

@Service
@Slf4j(topic = "JWT-SERVICE-IMPL")
public class JwtServiceImpl implements JwtService {

  public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

  @Value("${jwt.refresh-token}")
  private String jwtKeyRefresh;

  @Value("${jwt.access-token-validity-in-seconds}")
  private long accessTokenExpiration;

  @Value("${jwt.refresh-token-validity-in-seconds}")
  private long refreshTokenExpiration;

  private final JwtEncoder accessTokenEncoder;
  private final JwtEncoder refreshTokenEncoder;

  public JwtServiceImpl(@Qualifier("accessTokenEncoder") JwtEncoder accessTokenEncoder,
      @Qualifier("refreshTokenEncoder") JwtEncoder refreshTokenEncoder) {
    this.accessTokenEncoder = accessTokenEncoder;
    this.refreshTokenEncoder = refreshTokenEncoder;
  }

  @Override
  public String createAccessToken(String email, ResLoginDTO dto) {
    ResLoginDTO.UserInsideToken userToken = new ResLoginDTO.UserInsideToken();
    userToken.setId(dto.getUser().getId());
    userToken.setEmail(dto.getUser().getEmail());
    userToken.setFullName(dto.getUser().getFullName());

    Instant now = Instant.now();
    Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

    // hardcode permisson (for testing)
    List<String> listAuthority = new ArrayList<String>();

    listAuthority.add("ROLE_USER_CREATE");
    listAuthority.add("ROLE_USER_UPDATE");

    // @formatter:off
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuedAt(now)
        .expiresAt(validity)
        .subject(email)
        .claim("permission", listAuthority)
        .claim("user", userToken)
        .build();

    JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
    return this.accessTokenEncoder.encode(JwtEncoderParameters.from(jwsHeader,
        claims)).getTokenValue();
  }

  @Override
  public String createRefreshToken(String email, ResLoginDTO dto) {

    ResLoginDTO.UserInsideToken userToken = new ResLoginDTO.UserInsideToken();
    userToken.setId(dto.getUser().getId());
    userToken.setEmail(dto.getUser().getEmail());
    userToken.setFullName(dto.getUser().getFullName());

    Instant now = Instant.now();
    Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);
    // @formatter:off
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuedAt(now)
        .expiresAt(validity)
        .subject(email)
        .claim("user", userToken)
        .build();

    JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
    return this.refreshTokenEncoder.encode(JwtEncoderParameters.from(jwsHeader,
        claims)).getTokenValue();
  }

  @Override
  public Jwt checkValidRefreshToken(String token) {
    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
        getSecretKeyRefresh()).macAlgorithm(JWT_ALGORITHM).build(); // Lấy SecretKey để giải mã
    try {
      return jwtDecoder.decode(token);
    } catch (Exception e) {
      System.out.println(">>> Refresh Token error: " + e.getMessage());
      throw e;
    }
  }

  private SecretKey getSecretKeyRefresh() {
    byte[] keyBytes = Base64.from(jwtKeyRefresh).decode();
    return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
  }

  public static Optional<String> getCurrentUserLogin() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
  }

  private static String extractPrincipal(Authentication authentication) {
    if (authentication == null) {
      return null;
    } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
      return springSecurityUser.getUsername();
    } else if (authentication.getPrincipal() instanceof Jwt jwt) {
      return jwt.getSubject();
    } else if (authentication.getPrincipal() instanceof String s) {
      return s;
    }
    return null;
  }
}
