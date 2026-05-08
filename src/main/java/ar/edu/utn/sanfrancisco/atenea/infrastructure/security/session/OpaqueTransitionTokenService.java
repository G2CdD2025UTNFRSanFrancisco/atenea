package ar.edu.utn.sanfrancisco.atenea.infrastructure.security.session;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.token.TokenPurpose;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.session.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.session.HashedRefreshToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.PlainRefreshToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.RefreshTokenGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.session.RefreshTokenHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.session.TransitionTokenService;
import ar.edu.utn.sanfrancisco.atenea.domain.session.TransitionToken;
import ar.edu.utn.sanfrancisco.atenea.domain.session.TransitionTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Service
public class OpaqueTransitionTokenService implements TransitionTokenService {

    private static final Duration DEFAULT_DURATION = Duration.ofMinutes(2);

    private final TransitionTokenRepository transitionTokenRepository;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final RefreshTokenHashService refreshTokenHashService;
    private final IdentityGenerator identityGenerator;
    private final Clock clock;

    public OpaqueTransitionTokenService(
            final TransitionTokenRepository transitionTokenRepository,
            final RefreshTokenGenerator refreshTokenGenerator,
            final RefreshTokenHashService refreshTokenHashService,
            final IdentityGenerator identityGenerator,
            final Clock clock
    ) {
        this.transitionTokenRepository = transitionTokenRepository;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.refreshTokenHashService = refreshTokenHashService;
        this.identityGenerator = identityGenerator;
        this.clock = clock;
    }

    @Transactional
    public String issue(final AccountId accountId, final DeviceId deviceId, final TokenPurpose purpose) {
        final PlainRefreshToken plain = refreshTokenGenerator.generate();
        final HashedRefreshToken hash = refreshTokenHashService.hash(plain);
        final String tokenValue = new String(plain.value());
        try {
            final TransitionToken token = TransitionToken.create(
                    accountId,
                    deviceId,
                    purpose,
                    hash,
                    DEFAULT_DURATION,
                    identityGenerator,
                    clock
            );
            transitionTokenRepository.create(token);
            return tokenValue;
        } finally {
            Arrays.fill(plain.value(), '\0');
        }
    }

    @Transactional
    public Optional<AccountId> lookupAccountId(
            final String token,
            final TokenPurpose purpose,
            final DeviceId deviceId
    ) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        final PlainRefreshToken plain = new PlainRefreshToken(token.toCharArray());
        try {
            final HashedRefreshToken hash = refreshTokenHashService.hash(plain);
            final Instant now = Instant.now(clock);
            return transitionTokenRepository
                    .findActiveByHashAndPurposeAndDeviceId(hash, purpose, deviceId, now)
                    .map(TransitionToken::getAccountId);
        } finally {
            Arrays.fill(plain.value(), '\0');
        }
    }

    @Transactional
    public void consumeToken(
            final String token,
            final TokenPurpose purpose,
            final DeviceId deviceId
    ) {
        if (token == null || token.isBlank()) {
            return;
        }

        final PlainRefreshToken plain = new PlainRefreshToken(token.toCharArray());
        try {
            final HashedRefreshToken hash = refreshTokenHashService.hash(plain);
            final Instant now = Instant.now(clock);
            transitionTokenRepository
                    .findActiveByHashAndPurposeAndDeviceId(hash, purpose, deviceId, now)
                    .ifPresent(stored -> {
                        stored.consume(clock);
                        transitionTokenRepository.update(stored);
                    });
        } finally {
            Arrays.fill(plain.value(), '\0');
        }
    }
}



