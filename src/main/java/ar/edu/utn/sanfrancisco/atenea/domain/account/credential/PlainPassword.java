package ar.edu.utn.sanfrancisco.atenea.domain.account.credential;

import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.exception.InsecurePasswordException;

import java.util.Arrays;
import java.util.Objects;

public final class PlainPassword implements CharSequence, AutoCloseable {

    private char[] value;
    private boolean destroyed;

    public PlainPassword(char[] value) {
        Objects.requireNonNull(value);

        char[] normalized = normalize(value);

        if (normalized.length < 8) {
            Arrays.fill(normalized, '\0');
            throw new InsecurePasswordException();
        }

        this.value = normalized;
        this.destroyed = false;
    }

    private static char[] normalize(char[] input) {
        int start = 0;
        int end = input.length - 1;

        while (start <= end && Character.isWhitespace(input[start])) {
            start++;
        }
        while (end >= start && Character.isWhitespace(input[end])) {
            end--;
        }

        int newLength = end - start + 1;
        if (newLength <= 0) {
            Arrays.fill(input, '\0');
            return new char[0];
        }

        char[] result = new char[newLength];
        System.arraycopy(input, start, result, 0, newLength);
        Arrays.fill(input, '\0');
        return result;
    }

    private void ensureNotDestroyed() {
        if (destroyed || value == null) {
            throw new IllegalStateException("Password already destroyed");
        }
    }

    @Override
    public int length() {
        ensureNotDestroyed();
        return value.length;
    }

    @Override
    public char charAt(int index) {
        ensureNotDestroyed();
        return value[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        ensureNotDestroyed();
        return new String(value, start, end - start);
    }

    @Override
    public String toString() {
        ensureNotDestroyed();
        return new String(value);
    }

    public void destroy() {
        if (!destroyed && value != null) {
            Arrays.fill(value, '\0');
            value = null;
            destroyed = true;
        }
    }

    @Override
    public void close() {
        destroy();
    }
}
