package ar.edu.utn.sanfrancisco.atenea.domain.session;

public interface RefreshTokenHashService {
    HashedRefreshToken hash(PlainRefreshToken plain);
}
