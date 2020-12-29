package se.arkalix.util._internal;

import se.arkalix.util.annotation.Internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;

@Internal
public final class SystemProperties {
    private SystemProperties() {}

    public static boolean contains(final String key) {
        return getString(key).isPresent();
    }

    public static Optional<String> getString(final String key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (key.isBlank()) {
            throw new IllegalArgumentException("key.isBlank()");
        }

        return Optional.ofNullable(System.getSecurityManager() != null
            ? AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty(key))
            : System.getProperty(key));
    }

    public static Optional<Boolean> getBoolean(final String key) {
        var value = getString(key).orElse(null);

        empty:
        {
            if (value == null) {
                break empty;
            }

            value = value.trim().toLowerCase();
            if (value.isBlank()) {
                break empty;
            }

            if ("true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "1".equals(value)) {
                return Optional.of(true);
            }

            if ("false".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) || "0".equals(value)) {
                return Optional.of(false);
            }

            throw new IllegalStateException("boolean system property is invalid '" + key + "': " + value);
        }

        return Optional.empty();
    }

    public static Optional<Integer> getInteger(final String key) {
        var value = getString(key).orElse(null);

        empty:
        try {
            if (value == null) {
                break empty;
            }

            value = value.trim();
            if (value.isBlank()) {
                break empty;
            }

            return Optional.of(Integer.parseInt(value));
        }
        catch (final NumberFormatException exception) {
            throw new IllegalStateException("integer system property is invalid '" + key + "': " + value, exception);
        }

        return Optional.empty();
    }

    public static Optional<Long> getLong(final String key) {
        var value = getString(key).orElse(null);

        empty:
        try {
            if (value == null) {
                break empty;
            }

            value = value.trim();
            if (value.isBlank()) {
                break empty;
            }

            return Optional.of(Long.parseLong(value));
        }
        catch (final NumberFormatException exception) {
            throw new IllegalStateException("long system property is invalid '" + key + "': " + value, exception);
        }

        return Optional.empty();
    }
}
