package se.arkalix.io.buf._internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.arkalix.util.annotation.Internal;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

@Internal
public final class ByteBuffers {
    private ByteBuffers() {}

    private static final Logger logger = LoggerFactory.getLogger(ByteBuffers.class);
    private static final Object unsafe;
    private static final Method unsafeInvokeCleaner;

    /**
     * Releases memory held by provided {@code byteBuffer} if it is a direct
     * buffer and support exists on the running platform.
     *
     * Any use of the provided {@code byteBuffer} after being provided to this
     * method may result in undefined behavior.
     *
     * @param byteBuffer Byte buffer to free memory of.
     */
    public static void free(final ByteBuffer byteBuffer) {
        if (unsafeInvokeCleaner == null || !byteBuffer.isDirect()) {
            return;
        }
        try {
            unsafeInvokeCleaner.invoke(unsafe, byteBuffer);
        }
        catch (final Throwable throwable) {
            logger.warn("failed to invoke sun.misc.Unsafe#invokeCleaner(ByteBuffer); " +
                "cannot explicitly free byte buffers", throwable);
        }
    }

    static {
        unsafe = getUnsafeOrNull();
        unsafeInvokeCleaner = getUnsafeInvokeCleanerOrNull();
    }

    private static Method getUnsafeInvokeCleanerOrNull() {
        if (ByteBuffers.unsafe == null) {
            return null;
        }
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged((PrivilegedAction<Method>) ByteBuffers::getUnsafeInvokeCleanerOrNull0);
        }
        return getUnsafeInvokeCleanerOrNull0();
    }

    private static Method getUnsafeInvokeCleanerOrNull0() {
        final Method method;

        try {
            method = ByteBuffers.unsafe.getClass()
                .getDeclaredMethod("invokeCleaner", ByteBuffer.class);
        }
        catch (final Throwable throwable) {
            logger.debug("failed to get sun.misc.Unsafe#invokeCleaner(ByteBuffer); " +
                "cannot explicitly free byte buffers", throwable);
            return null;
        }

        try {
            // Test method to make sure it will work later on.
            final var buffer = ByteBuffer.allocateDirect(1);
            method.invoke(ByteBuffers.unsafe, buffer);
        }
        catch (final Throwable throwable) {
            logger.debug("failed to invoke sun.misc.Unsafe#invokeCleaner(ByteBuffer); " +
                "cannot explicitly free byte buffers", throwable);
            return null;
        }

        return method;
    }

    private static Object getUnsafeOrNull() {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged((PrivilegedAction<Object>) ByteBuffers::getUnsafeOrNull0);
        }
        return getUnsafeOrNull0();
    }

    private static Object getUnsafeOrNull0() {
        try {
            final var unsafe = ByteBuffers.class.getClassLoader()
                .loadClass("sun.misc.Unsafe");

            final var instance = unsafe.getDeclaredField("theUnsafe");
            instance.setAccessible(true);
            return instance.get(null);
        }
        catch (final Throwable throwable) {
            logger.debug("failed to access sun.misc.Unsafe; cannot " +
                "explicitly free byte buffers", throwable);
            return null;
        }
    }
}
