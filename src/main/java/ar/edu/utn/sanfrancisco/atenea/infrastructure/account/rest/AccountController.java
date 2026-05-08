package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.rest;

import ar.edu.utn.sanfrancisco.atenea.application.account.command.ChangePasswordUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.CreateAccountUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.DeleteAccountUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.SetRoleUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.ChangePasswordCommand;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.CreateAccountCommand;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.DeleteAccountCommand;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.SetRoleCommand;
import ar.edu.utn.sanfrancisco.atenea.application.account.query.GetAccountDetailsUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.query.GetAllAccountDetailsUseCase;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidPasswordResetTokenException;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PagedResult;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PaginationQuery;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.account.rest.dto.*;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.security.account.PasswordResetTokenDecoder;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private static final String PASSWORD_RESET_TOKEN_COOKIE = "password_reset_token";

    private final GetAccountDetailsUseCase getAccountDetailsUseCase;
    private final GetAllAccountDetailsUseCase getAllAccountDetailsUseCase;

    private final CreateAccountUseCase createAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final SetRoleUseCase setRoleUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    private final PasswordResetTokenDecoder passwordResetTokenDecoder;

    public AccountController(
            final GetAccountDetailsUseCase getAccountDetailsUseCase,
            final GetAllAccountDetailsUseCase getAllAccountDetailsUseCase,
            final CreateAccountUseCase createAccountUseCase,
            final DeleteAccountUseCase deleteAccountUseCase,
            final SetRoleUseCase setRoleUseCase,
            final ChangePasswordUseCase changePasswordUseCase,
            final PasswordResetTokenDecoder passwordResetTokenDecoder
    ) {
        this.getAccountDetailsUseCase = getAccountDetailsUseCase;
        this.getAllAccountDetailsUseCase = getAllAccountDetailsUseCase;
        this.createAccountUseCase = createAccountUseCase;
        this.deleteAccountUseCase = deleteAccountUseCase;
        this.setRoleUseCase = setRoleUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.passwordResetTokenDecoder = passwordResetTokenDecoder;
    }

    @GetMapping("/@me")
    public AccountDetailsResponse getCurrentAccount(
            final JwtAuthenticationToken token
    ) {
        return AccountDetailsResponse.fromSnapshot(this.getAccountDetailsUseCase.execute(actorIdFrom(token)));
    }

    @GetMapping("/{targetAccountId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_OWNER')")
    public AccountDetailsResponse getAccountById(
            @PathVariable final Long targetAccountId
    ) {
        return AccountDetailsResponse.fromSnapshot(this.getAccountDetailsUseCase.execute(new AccountId(targetAccountId)));
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    public PagedResult<AccountSummaryResponse> getAllAccounts(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) {
        final var query = PaginationQuery.of(page, size);
        final var result = this.getAllAccountDetailsUseCase.execute(query);
        return PagedResult.of(
                result.items().stream()
                        .map(AccountSummaryResponse::fromSnapshot)
                        .collect(Collectors.toList()),
                result.totalItems(),
                query
        );
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    public CreatedAccountResponse create(
            @Valid @RequestBody final CreateAccountRequest request
    ) {
        final AccountId createdId = createAccountUseCase.execute(
                new CreateAccountCommand(request.username(), request.password().toCharArray())
        );
        return new CreatedAccountResponse(createdId.value());
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("permitAll()")
    public void changePassword(
            @RequestHeader("X-Device-Id") final String deviceId,
            @CookieValue(value = PASSWORD_RESET_TOKEN_COOKIE, required = false) final String passwordResetToken,
            @Valid @RequestBody final ChangePasswordRequest request,
            final HttpServletResponse response
    ) {
        final String transitionToken = requireTransitionToken(passwordResetToken);
        final char[] newPassword = request.newPassword().toCharArray();
        try {
            final AccountId accountId = passwordResetTokenDecoder.accountIdFrom(
                    transitionToken,
                    new DeviceId(deviceId)
            );
            changePasswordUseCase.execute(new ChangePasswordCommand(accountId, newPassword));
            passwordResetTokenDecoder.consume(transitionToken, new DeviceId(deviceId));
        } finally {
            Arrays.fill(newPassword, '\0');
            clearPasswordResetCookie(response);
        }
    }

    @DeleteMapping("/{targetAccountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    public void delete(
            @PathVariable final Long targetAccountId,
            final JwtAuthenticationToken principal
    ) {
        deleteAccountUseCase.execute(
                new DeleteAccountCommand(actorIdFrom(principal), new AccountId(targetAccountId))
        );
    }

    @PutMapping("/{targetAccountId}/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    public void setRole(
            @PathVariable final Long targetAccountId,
            @Valid @RequestBody final SetRoleRequest request,
            final JwtAuthenticationToken principal
    ) {
        setRoleUseCase.execute(
                new SetRoleCommand(actorIdFrom(principal), new AccountId(targetAccountId), request.role())
        );
    }

    private AccountId actorIdFrom(final JwtAuthenticationToken principal) {
        return new AccountId(Long.parseLong(principal.getToken().getSubject()));
    }

    private String requireTransitionToken(final String transitionToken) {
        if (transitionToken == null || transitionToken.isBlank()) {
            log.info("Transition token: `{}`", transitionToken);
            throw new InvalidPasswordResetTokenException();
        }

        return transitionToken;
    }

    private void clearPasswordResetCookie(final HttpServletResponse response) {
        final ResponseCookie cookie = ResponseCookie.from(PASSWORD_RESET_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/accounts/password")
                .sameSite("Strict")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}

