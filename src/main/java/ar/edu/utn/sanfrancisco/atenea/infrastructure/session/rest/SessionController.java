package ar.edu.utn.sanfrancisco.atenea.infrastructure.session.rest;

import ar.edu.utn.sanfrancisco.atenea.application.session.command.CompleteTotpMfaUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.CreateSessionUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.RefreshSessionUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.RevokeAllSessionsUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.RevokeCurrentSessionUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.RevokeSessionUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.LoginCommand;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.LoginResponse;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.RefreshSessionCommand;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.RefreshSessionResponse;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.RevokeAllSessionsCommand;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.RevokeCurrentSessionCommand;
import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.RevokeSessionCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Username;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpCode;
import ar.edu.utn.sanfrancisco.atenea.domain.session.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.PlainRefreshToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.SessionId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidMfaChallengeTokenException;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidRefreshTokenException;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.session.rest.dto.CompleteMfaSessionRequest;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.session.rest.dto.CreateSessionRequest;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.session.rest.dto.RefreshAccessTokenResponse;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.session.rest.dto.SessionAccessResponse;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.security.session.MfaChallengeTokenDecoder;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final CreateSessionUseCase createSessionUseCase;
    private final CompleteTotpMfaUseCase completeTotpMfaUseCase;
    private final RefreshSessionUseCase refreshSessionUseCase;
    private final RevokeCurrentSessionUseCase revokeCurrentSessionUseCase;
    private final RevokeSessionUseCase revokeSessionUseCase;
    private final RevokeAllSessionsUseCase revokeAllSessionsUseCase;
    private final MfaChallengeTokenDecoder mfaChallengeTokenDecoder;

    public SessionController(
            final CreateSessionUseCase createSessionUseCase,
            final CompleteTotpMfaUseCase completeTotpMfaUseCase,
            final RefreshSessionUseCase refreshSessionUseCase,
            final RevokeCurrentSessionUseCase revokeCurrentSessionUseCase,
            final RevokeSessionUseCase revokeSessionUseCase,
            final RevokeAllSessionsUseCase revokeAllSessionsUseCase,
            final MfaChallengeTokenDecoder mfaChallengeTokenDecoder
    ) {
        this.createSessionUseCase = createSessionUseCase;
        this.completeTotpMfaUseCase = completeTotpMfaUseCase;
        this.refreshSessionUseCase = refreshSessionUseCase;
        this.revokeCurrentSessionUseCase = revokeCurrentSessionUseCase;
        this.revokeSessionUseCase = revokeSessionUseCase;
        this.revokeAllSessionsUseCase = revokeAllSessionsUseCase;
        this.mfaChallengeTokenDecoder = mfaChallengeTokenDecoder;
    }

    @PostMapping("")
    public SessionAccessResponse create(
            @RequestHeader("X-Device-Id") final String deviceId,
            @Valid @RequestBody final CreateSessionRequest request,
            final HttpServletResponse response
    ) {
        final char[] password = request.password().toCharArray();
        try {
            final LoginResponse loginResponse = createSessionUseCase.execute(
                    new LoginCommand(new Username(request.username()), new DeviceId(deviceId), password)
            );
            return toSessionAccessResponseAndAttachCookie(loginResponse, response);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    @PostMapping("/mfa")
    public SessionAccessResponse createFromMfa(
            @RequestHeader("X-Device-Id") final String deviceId,
            @RequestHeader("X-MFA-Token") final String authorizationHeader,
            @Valid @RequestBody final CompleteMfaSessionRequest request,
            final HttpServletResponse response
    ) {
        final AccountId accountId = mfaChallengeTokenDecoder.accountIdFrom(extractTransitionToken(authorizationHeader));
        final LoginResponse loginResponse = completeTotpMfaUseCase.execute(
                accountId,
                new DeviceId(deviceId),
                new TotpCode(request.totpCode())
        );
        return toSessionAccessResponseAndAttachCookie(loginResponse, response);
    }

    @PostMapping("/refresh")
    public RefreshAccessTokenResponse refresh(
            @RequestHeader("X-Device-Id") final String deviceId,
            @CookieValue(value = REFRESH_TOKEN_COOKIE, required = false) final String refreshToken,
            final HttpServletResponse response
    ) {
        final String nonNullRefreshToken = requireRefreshToken(refreshToken);
        PlainRefreshToken token = new PlainRefreshToken(nonNullRefreshToken.toCharArray());
        final RefreshSessionResponse refreshResponse = refreshSessionUseCase.execute(
                new RefreshSessionCommand(new DeviceId(deviceId), token)
        );
        attachRefreshCookie(response, refreshResponse.refreshToken());
        return new RefreshAccessTokenResponse(refreshResponse.accessToken());
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
            @RequestHeader("X-Device-Id") final String deviceId,
            @CookieValue(value = REFRESH_TOKEN_COOKIE, required = false) final String refreshToken,
            final HttpServletResponse response
    ) {
        final String nonNullRefreshToken = requireRefreshToken(refreshToken);
        PlainRefreshToken token = new PlainRefreshToken(nonNullRefreshToken.toCharArray());
        revokeCurrentSessionUseCase.execute(new RevokeCurrentSessionCommand(
                new DeviceId(deviceId),
                token
        ));
        clearRefreshCookie(response);
    }

    @DeleteMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeSession(
            @PathVariable final Long sessionId,
            final JwtAuthenticationToken principal
    ) {
        revokeSessionUseCase.execute(
                new RevokeSessionCommand(operatorIdFrom(principal), new SessionId(sessionId))
        );
    }

    @DeleteMapping("/accounts/{targetAccountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeAllByAccount(
            @PathVariable final Long targetAccountId,
            final JwtAuthenticationToken principal
    ) {
        revokeAllSessionsUseCase.execute(
                new RevokeAllSessionsCommand(operatorIdFrom(principal), new AccountId(targetAccountId))
        );
    }

    private AccountId operatorIdFrom(final JwtAuthenticationToken principal) {
        return new AccountId(Long.parseLong(principal.getToken().getSubject()));
    }

    private SessionAccessResponse toSessionAccessResponseAndAttachCookie(
            final LoginResponse loginResponse,
            final HttpServletResponse response
    ) {
        if (loginResponse.refreshToken() != null) {
            attachRefreshCookie(response, loginResponse.refreshToken());
        }
        return new SessionAccessResponse(
                SessionAccessResponse.Status.valueOf(loginResponse.status().name()),
                loginResponse.accessToken(),
                loginResponse.accountId().value()
        );
    }

    private String requireRefreshToken(final String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException();
        }
        return refreshToken;
    }

    private String extractTransitionToken(final String authorizationHeader) {
        if (authorizationHeader == null) {
            throw new InvalidMfaChallengeTokenException();
        }

        if (!authorizationHeader.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            throw new InvalidMfaChallengeTokenException();
        }

        final String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            throw new InvalidMfaChallengeTokenException();
        }

        return token;
    }

    private void attachRefreshCookie(final HttpServletResponse response, final String refreshToken) {
        final ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/sessions")
                .sameSite("Strict")
                .maxAge(86400)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearRefreshCookie(final HttpServletResponse response) {
        final ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/sessions")
                .sameSite("Strict")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}

