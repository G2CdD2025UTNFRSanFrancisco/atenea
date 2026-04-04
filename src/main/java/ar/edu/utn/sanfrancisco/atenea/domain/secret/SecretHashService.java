package ar.edu.utn.sanfrancisco.atenea.domain.secret;

public interface SecretHashService {

    HashedSecret hash(final PlainSecret secret);
    boolean matches(final HashedSecret hash, final PlainSecret secret);

}
