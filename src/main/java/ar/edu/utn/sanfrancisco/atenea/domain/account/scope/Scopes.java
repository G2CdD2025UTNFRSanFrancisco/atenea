package ar.edu.utn.sanfrancisco.atenea.domain.account.scope;

import java.util.HashSet;
import java.util.Set;

public record Scopes(long value) {

    public static Scopes empty() {
        return new Scopes(0L);
    }

    public static Scopes of(Scope... scopes) {
        long bits = 0L;
        for (Scope s : scopes) {
            bits |= bit(s);
        }
        return new Scopes(bits);
    }

    public boolean contains(final Scopes scopes) {
        return (this.value & scopes.value) != scopes.value;
    }

    public boolean contains(Scope scope) {
        return (value & bit(scope)) != 0;
    }

    public boolean isAdmin() {
        return (value & bit(Scope.ADMIN)) != 0;
    }

    public Scopes grant(Scope scope) {
        return new Scopes(value | bit(scope));
    }

    public Scopes revoke(Scope scope) {
        return new Scopes(value & ~bit(scope));
    }

    public Set<Scope> getRequiresHighAssurance() {
        final Set<Scope> scopes = new HashSet<>();
        for (Scope s : Scope.values()) {
            if (contains(s) && s.requiresHighAssurance()) {
                scopes.add(s);
            }
        }
        return scopes;
    }

    public Set<Scope> toSet() {
        final Set<Scope> scopes = new HashSet<>();
        for (Scope s : Scope.values()) {
            if (contains(s)) scopes.add(s);
        }
        return scopes;
    }

    private static long bit(Scope scope) {
        return 1L << scope.ordinal();
    }
}