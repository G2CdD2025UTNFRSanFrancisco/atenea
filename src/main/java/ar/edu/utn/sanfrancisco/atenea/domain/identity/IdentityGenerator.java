package ar.edu.utn.sanfrancisco.atenea.domain.identity;

import java.util.function.Function;

public interface IdentityGenerator {

    <V, T extends Identity<V>> T nextLong(Function<Long, T> constructor);
    <V, T extends Identity<V>> T nextString(Function<String, T> constructor);

}
