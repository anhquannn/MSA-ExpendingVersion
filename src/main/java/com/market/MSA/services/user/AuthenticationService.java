package com.market.MSA.services.user;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.models.user.InvalidatedToken;
import com.market.MSA.models.user.User;
import com.market.MSA.repositories.user.InvalidatedTokenRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.user.AuthenticationRequest;
import com.market.MSA.requests.user.IntrospectRequest;
import com.market.MSA.requests.user.LogoutRequest;
import com.market.MSA.requests.user.RefreshRequest;
import com.market.MSA.responses.user.AuthenticationResponse;
import com.market.MSA.responses.user.IntrospectResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
  final UserRepository userRepo;
  final InvalidatedTokenRepository invalidatedTokenRepository;

  @NonFinal
  @Value("${jwt.signerKey}")
  protected String SIGNER_KEY;

  @NonFinal
  @Value("${jwt.valid-duration}")
  protected long VALID_DURATION;

  @NonFinal
  @Value("${jwt.refreshable-duration}")
  protected long REFRESHABLE_DURATION;

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    var user =
        userRepo
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

    PasswordEncoder pw = new BCryptPasswordEncoder(10);
    boolean authenticated = pw.matches(request.getPassword(), user.getPassword());

    if (!authenticated) {
      throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
    }

    var token = generateToken(user);

    return AuthenticationResponse.builder().token(token).authenticated(true).build();
  }

  public IntrospectResponse introspect(IntrospectRequest request)
      throws JOSEException, ParseException {
    var token = request.getToken();
    boolean isValid = true;

    try {
      verifyToken(token, false);
    } catch (AppException e) {
      isValid = false;
    }

    return IntrospectResponse.builder().active(isValid).build();
  }

  public String generateToken(User user) {
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

    JWTClaimsSet jwtClaimSet =
        new JWTClaimsSet.Builder()
            .subject(user.getEmail())
            .issuer("com.msa")
            .issueTime(new Date())
            .expirationTime(
                new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.HOURS).toEpochMilli()))
            .jwtID(UUID.randomUUID().toString())
            .claim("scope", buildScope(user))
            .build();

    Payload payload = new Payload(jwtClaimSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(header, payload);

    try {
      jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
      return jwsObject.serialize();
    } catch (JOSEException e) {
      throw new AppException(ErrorCode.GENERATE_TOKEN_FALSE);
    }
  }

  public void logout(LogoutRequest request) throws JOSEException, ParseException {
    SignedJWT signToken;
    try {
      signToken = verifyToken(request.getToken(), true);
      String jit = signToken.getJWTClaimsSet().getJWTID();
      Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

      InvalidatedToken invalidatedToken =
          InvalidatedToken.builder().invalidatedTokenId(jit).expiryTime(expiryTime).build();
      invalidatedTokenRepository.save(invalidatedToken);
    } catch (AppException e) {
      throw new AppException(ErrorCode.LOGOUT_FALSE);
    }
  }

  @Transactional
  public AuthenticationResponse refreshToken(RefreshRequest request)
      throws JOSEException, ParseException {
    var signedJWT = verifyToken(request.getToken(), true);

    var jit = signedJWT.getJWTClaimsSet().getJWTID();
    var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

    InvalidatedToken invalidatedToken =
        InvalidatedToken.builder().invalidatedTokenId(jit).expiryTime(expiryTime).build();
    invalidatedTokenRepository.save(invalidatedToken);

    var email = signedJWT.getJWTClaimsSet().getSubject();
    var user =
        userRepo
            .findByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.USER_UNAUTHENTICATED));

    var token = generateToken(user);

    return AuthenticationResponse.builder().token(token).authenticated(true).build();
  }

  String buildScope(User user) {
    StringJoiner stringJoiner = new StringJoiner(" ");
    if (!CollectionUtils.isEmpty(user.getRoles())) {
      user.getRoles()
          .forEach(
              role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                  role.getPermissions()
                      .forEach(permission -> stringJoiner.add(permission.getName()));
                }
              });
    }
    return stringJoiner.toString();
  }

  SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
    JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

    SignedJWT signedJWT = SignedJWT.parse(token);

    Date expiryTime =
        (isRefresh)
            ? new Date(
                signedJWT
                    .getJWTClaimsSet()
                    .getIssueTime()
                    .toInstant()
                    .plus(REFRESHABLE_DURATION, ChronoUnit.HOURS)
                    .toEpochMilli())
            : signedJWT.getJWTClaimsSet().getExpirationTime();

    var verified = signedJWT.verify(verifier);

    if (!(verified && expiryTime.after(new Date()))) {
      throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
    }

    if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
      throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
    }

    return signedJWT;
  }
}
