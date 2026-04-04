package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.rest;

import ar.edu.utn.sanfrancisco.atenea.application.account.command.ChangePasswordUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.CreateAccountUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.DeleteAccountUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.GrantScopeUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.RevokeScopeUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.SetScopesUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.ChangePasswordCommand;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.CreateAccountCommand;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.DeleteAccountCommand;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.GrantScopeCommand;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.RevokeScopeCommand;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.SetScopesCommand;
import ar.edu.utn.sanfrancisco.atenea.application.account.query.GetAccountDetailsUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.query.GetAllAccountDetailsUseCase;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scope;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidPasswordResetTokenException;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PagedResult;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PaginationQuery;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.account.rest.dto.*;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.security.account.PasswordResetTokenDecoder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final GetAccountDetailsUseCase getAccountDetailsUseCase;
    private final GetAllAccountDetailsUseCase getAllAccountDetailsUseCase;


    private final CreateAccountUseCase createAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final SetScopesUseCase setScopesUseCase;
    private final GrantScopeUseCase grantScopeUseCase;
    private final RevokeScopeUseCase revokeScopeUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    private final PasswordResetTokenDecoder passwordResetTokenDecoder;

    public AccountController(
            final GetAccountDetailsUseCase getAccountDetailsUseCase,
            final GetAllAccountDetailsUseCase getAllAccountDetailsUseCase,
            final CreateAccountUseCase createAccountUseCase,
            final DeleteAccountUseCase deleteAccountUseCase,
            final SetScopesUseCase setScopesUseCase,
            final GrantScopeUseCase grantScopeUseCase,
            final RevokeScopeUseCase revokeScopeUseCase,
            final ChangePasswordUseCase changePasswordUseCase,
            final PasswordResetTokenDecoder passwordResetTokenDecoder
    ) {
        this.getAccountDetailsUseCase = getAccountDetailsUseCase;
        this.getAllAccountDetailsUseCase = getAllAccountDetailsUseCase;
        this.createAccountUseCase = createAccountUseCase;
        this.deleteAccountUseCase = deleteAccountUseCase;
        this.setScopesUseCase = setScopesUseCase;
        this.grantScopeUseCase = grantScopeUseCase;
        this.revokeScopeUseCase = revokeScopeUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.passwordResetTokenDecoder = passwordResetTokenDecoder;
    }

    @GetMapping("/@me")
    public AccountDetailsResponse getCurrentAccount(
            final JwtAuthenticationToken token
    ) {
        return AccountDetailsResponse.fromSnapshot(this.getAccountDetailsUseCase.execute(actorIdFrom(token)));
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('SCOPE_READ_ACCOUNTS')")
    public PagedResult<AccountDetailsResponse> getAllAccounts(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) {
        final var query = PaginationQuery.of(page, size);
        final var result = this.getAllAccountDetailsUseCase.execute(query);
        return PagedResult.of(
                result.items().stream()
                        .map(AccountDetailsResponse::fromSnapshot)
                        .collect(Collectors.toList()),
                result.totalItems(),
                query
        );
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCOPE_MANAGE_ACCOUNTS')")
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
            @RequestHeader("X-Password-Token") final String authorizationHeader,
            @Valid @RequestBody final ChangePasswordRequest request
    ) {
        final AccountId accountId = passwordResetTokenDecoder.accountIdFrom(extractTransitionToken(authorizationHeader));
        final char[] newPassword = request.newPassword().toCharArray();
        try {
            changePasswordUseCase.execute(new ChangePasswordCommand(accountId, newPassword));
        } finally {
            Arrays.fill(newPassword, '\0');
        }
    }

    @DeleteMapping("/{targetAccountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable final Long targetAccountId,
            final JwtAuthenticationToken principal
    ) {
        deleteAccountUseCase.execute(
                new DeleteAccountCommand(actorIdFrom(principal), new AccountId(targetAccountId))
        );
    }

    @PutMapping("/{targetAccountId}/scopes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SCOPE_MANAGE_ACCOUNTS')")
    public void setScopes(
            @PathVariable final Long targetAccountId,
            @Valid @RequestBody final SetScopesRequest request,
            final JwtAuthenticationToken principal
    ) {
        setScopesUseCase.execute(
                new SetScopesCommand(actorIdFrom(principal), new AccountId(targetAccountId), request.scopes())
        );
    }

    @PostMapping("/{targetAccountId}/scopes/{scope}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SCOPE_MANAGE_ACCOUNTS')")
    public void grantScope(
            @PathVariable final Long targetAccountId,
            @PathVariable final Scope scope,
            final JwtAuthenticationToken principal
    ) {
        grantScopeUseCase.execute(
                new GrantScopeCommand(actorIdFrom(principal), new AccountId(targetAccountId), scope)
        );
    }

    @DeleteMapping("/{targetAccountId}/scopes/{scope}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SCOPE_MANAGE_ACCOUNTS')")
    public void revokeScope(
            @PathVariable final Long targetAccountId,
            @PathVariable final Scope scope,
            final JwtAuthenticationToken principal
    ) {
        revokeScopeUseCase.execute(
                new RevokeScopeCommand(actorIdFrom(principal), new AccountId(targetAccountId), scope)
        );
    }

    private AccountId actorIdFrom(final JwtAuthenticationToken principal) {
        return new AccountId(Long.parseLong(principal.getToken().getSubject()));
    }

    private String extractTransitionToken(final String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new InvalidPasswordResetTokenException();
        }

        final String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            throw new InvalidPasswordResetTokenException();
        }
        return token;
    }
}

