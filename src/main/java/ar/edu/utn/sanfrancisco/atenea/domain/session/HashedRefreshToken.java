package ar.edu.utn.sanfrancisco.atenea.domain.session;

public record HashedRefreshToken(String value) {
    public HashedRefreshToken {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Invalid refresh token hash");
        }
    }
}
