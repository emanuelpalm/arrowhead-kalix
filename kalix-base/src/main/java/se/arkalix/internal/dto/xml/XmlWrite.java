package se.arkalix.internal.dto.xml;

import se.arkalix.dto.DtoEncoding;
import se.arkalix.dto.DtoWriteException;
import se.arkalix.dto.binary.BinaryWriter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.*;

@SuppressWarnings("unused")
public final class XmlWrite {
    private XmlWrite() {}

    private static final byte[] TRUE = new byte[]{'t', 'r', 'u', 'e'};
    private static final byte[] FALSE = new byte[]{'f', 'a', 'l', 's', 'e'};

    private static final byte[] AMP = new byte[]{'&', 'a', 'm', 'p', ';'};
    private static final byte[] APOS = new byte[]{'&', 'a', 'p', 'o', 's', ';'};
    private static final byte[] GT = new byte[]{'&', 'g', 't', ';'};
    private static final byte[] LT = new byte[]{'&', 'l', 't', ';'};
    private static final byte[] QUOT = new byte[]{'&', 'q', 'u', 'o', 't', ';'};

    private static final byte[] NAME = new byte[]{
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 0, 0,
        0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 2,
        0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0,
    };

    public static void write(final BigDecimal bigDecimal, final BinaryWriter target) {
        target.write(bigDecimal.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final BigInteger bigInteger, final BinaryWriter target) {
        target.write(bigInteger.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final boolean bool, final BinaryWriter target) {
        target.write(bool ? TRUE : FALSE);
    }

    public static void write(final Duration duration, final BinaryWriter target) {
        target.write(duration.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final Instant instant, final BinaryWriter target) {
        target.write(instant.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final MonthDay monthDay, final BinaryWriter target) {
        target.write(monthDay.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final long number, final BinaryWriter target) {
        target.write(Long.toString(number)
            .getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final double number, final BinaryWriter target) {
        target.write(Double.toString(number)
            .getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final OffsetDateTime offsetDateTime, final BinaryWriter target) {
        target.write(offsetDateTime.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final OffsetTime offsetTime, final BinaryWriter target) {
        target.write(offsetTime.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final Period period, final BinaryWriter target) {
        target.write(period.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final String string, final BinaryWriter target) {
        byte[] escape;
        for (final var b : string.getBytes(StandardCharsets.UTF_8)) {
            normal:
            {
                switch (b) {
                case '&': escape = AMP; break;
                case '<': escape = LT; break;
                case '>': escape = GT; break;
                default:
                    break normal;
                }
                target.write(escape);
                continue;
            }
            target.write(b);
        }
    }

    public static void writeAttribute(final String string, final BinaryWriter target) {
        byte[] escape;
        for (final var b : string.getBytes(StandardCharsets.UTF_8)) {
            normal:
            {
                switch (b) {
                case '"': escape = QUOT; break;
                case '&': escape = AMP; break;
                case '\'': escape = APOS; break;
                case '<': escape = LT; break;
                case '>': escape = GT; break;
                default:
                    break normal;
                }
                target.write(escape);
                continue;
            }
            target.write(b);
        }
    }

    public static void writeName(final String string, final BinaryWriter target) throws DtoWriteException {
        if (string.length() == 0) {
            throw new DtoWriteException(DtoEncoding.XML, "Empty XML names not allowed");
        }
        final var chars = string.codePoints().toArray();
        validateNameStartCharacter(chars[0]);
        for (var i = 1; i < chars.length; ++i) {
            validateNameCharacter(chars[i]);
        }
        target.write(string.getBytes(StandardCharsets.UTF_8));
    }

    private static void validateNameStartCharacter(final int c) throws DtoWriteException {
        if ((c < NAME.length && NAME[c] - 2 < 0) || !((c >= 0xC0 && c <= 0xD6) || (c >= 0xD8 && c <= 0xF6) ||
            (c >= 0xF8 && c <= 0x2FF) || (c >= 0x370 && c <= 0x37D) || (c >= 0x37F && c <= 0x1FFF) ||
            (c >= 0x200C && c <= 0x200D) || (c >= 0x2070 && c <= 0x218F) || (c >= 0x2C00 && c <= 0x2FEF) ||
            (c >= 0x3001 && c <= 0xD7FF) || (c >= 0xF900 && c <= 0xFDCF) || (c >= 0xFDF0 && c <= 0xFFFD) ||
            (c >= 0x10000 && c <= 0xEFFFF)))
        {
            throw new DtoWriteException(DtoEncoding.XML, "Illegal XML name start character: '" + c + "'");
        }
    }

    private static void validateNameCharacter(final int c) throws DtoWriteException {
        if ((c < NAME.length && NAME[c] - 1 < 0) || !(c == 0xB7 || (c >= 0xC0 && c <= 0xD6) ||
            (c >= 0xD8 && c <= 0xF6) || (c >= 0xF8 && c <= 0x37D) || (c >= 0x37F && c <= 0x1FFF) ||
            (c >= 0x200C && c <= 0x200D) || c == 0x203F || c == 0x2040 || (c >= 0x2070 && c <= 0x218F) ||
            (c >= 0x2C00 && c <= 0x2FEF) || (c >= 0x3001 && c <= 0xD7FF) || (c >= 0xF900 && c <= 0xFDCF) ||
            (c >= 0xFDF0 && c <= 0xFFFD) || (c >= 0x10000 && c <= 0xEFFFF)))
        {
            throw new DtoWriteException(DtoEncoding.XML, "Illegal XML name character: '" + c + "'");
        }
    }

    public static void write(final Year year, final BinaryWriter target) {
        target.write(("" + year.getValue()).getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final YearMonth yearMonth, final BinaryWriter target) {
        target.write(yearMonth.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final ZonedDateTime zonedDateTime, final BinaryWriter target) {
        target.write(zonedDateTime.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final ZoneId zoneId, final BinaryWriter target) {
        target.write(zoneId.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void write(final ZoneOffset zoneOffset, final BinaryWriter target) {
        target.write(zoneOffset.toString().getBytes(StandardCharsets.ISO_8859_1));
    }
}
