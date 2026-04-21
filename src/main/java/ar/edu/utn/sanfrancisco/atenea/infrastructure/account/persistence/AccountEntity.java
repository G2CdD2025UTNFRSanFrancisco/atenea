package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.persistence;

import ar.edu.utn.sanfrancisco.atenea.infrastructure.account.persistence.credential.PasswordEmbeddable;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "accounts")
@Getter
public class AccountEntity {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private Long version;
    @Version
    private Long persistenceVersion;

    @Embedded
    private PasswordEmbeddable password;
    private boolean mfaRequired;
    private int hierarchyLevel;

    @Column(nullable = false)
    private long scopes;

    private int failedLoginAttempts;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Instant lockedUntil;

    protected AccountEntity() {}

    public AccountEntity(
            Long id,
            String username,
            Long version,
            Long persistenceVersion,
            PasswordEmbeddable password,
            boolean mfaRequired,
            int hierarchyLevel,
            long scopes,
            int failedLoginAttempts,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt,
            Instant lockedUntil
    ) {
        this.id = id;
        this.username = username;
        this.version = version;
        this.persistenceVersion = persistenceVersion;
        this.password = password;
        this.mfaRequired = mfaRequired;
        this.hierarchyLevel = hierarchyLevel;
        this.scopes = scopes;
        this.failedLoginAttempts = failedLoginAttempts;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.lockedUntil = lockedUntil;
    }
}
