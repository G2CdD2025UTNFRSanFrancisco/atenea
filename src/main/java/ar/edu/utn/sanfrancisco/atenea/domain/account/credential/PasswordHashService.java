package ar.edu.utn.sanfrancisco.atenea.domain.account.credential;

public interface PasswordHashService {

    HashedPassword hash(final PlainPassword password);
    boolean matches(final HashedPassword hash, final PlainPassword password);

}
