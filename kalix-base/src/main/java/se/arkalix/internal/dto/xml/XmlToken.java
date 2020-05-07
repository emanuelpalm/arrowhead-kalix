package se.arkalix.internal.dto.xml;

import se.arkalix.dto.DtoEncoding;
import se.arkalix.dto.DtoReadException;
import se.arkalix.dto.binary.BinaryReader;
import se.arkalix.internal.util.charset.Unicode;
import se.arkalix.util.annotation.Internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.*;

@Internal
@SuppressWarnings("unused")
public class XmlToken {
    private final XmlTokenType type;

    int begin, end;
    int nAttributes;
    int nChildren;

    public XmlTokenType type() {
        return type;
    }

    public int begin() {
        return begin;
    }

    public int end() {
        return end;
    }

    public int length() {
        return end - begin;
    }

    public int nAttributes() {
        return nAttributes;
    }

    public int nChildren() {
        return nChildren;
    }

    XmlToken(
        final XmlTokenType type,
        final int begin,
        final int end,
        final int nAttributes,
        final int nChildren)
    {
        assert begin >= 0 && begin < end && nAttributes >= 0 && nChildren >= 0;

        this.type = type;
        this.begin = begin;
        this.end = end;
        this.nAttributes = nAttributes;
        this.nChildren = nChildren;
    }

    public BigDecimal readBigDecimal(final BinaryReader source) {
        return new BigDecimal(readStringRaw(source));
    }

    public BigInteger readBigInteger(final BinaryReader source) {
        return new BigInteger(readStringRaw(source));
    }

    public byte readByte(final BinaryReader source) {
        return Byte.parseByte(requireNotHex(readStringRaw(source)));
    }

    public String readCDATA(final BinaryReader source) {
        final var buffer = new byte[length()];
        source.getBytes(begin, buffer);
        return new String(buffer, StandardCharsets.UTF_8);
    }

    public double readDouble(final BinaryReader source) {
        return Double.parseDouble(requireNotHex(readStringRaw(source)));
    }

    public Duration readDuration(final BinaryReader source) {
        return Duration.parse(readStringRaw(source));
    }

    public float readFloat(final BinaryReader source) {
        return Float.parseFloat(requireNotHex(readStringRaw(source)));
    }

    public int readInteger(final BinaryReader source) {
        return Integer.parseInt(requireNotHex(readStringRaw(source)));
    }

    public Instant readInstant(final BinaryReader source) {
        return Instant.parse(readStringRaw(source));
    }

    public long readLong(final BinaryReader source) {
        return Long.parseLong(requireNotHex(readStringRaw(source)));
    }

    public MonthDay readMonthDay(final BinaryReader source) {
        return MonthDay.parse(readStringRaw(source));
    }

    public OffsetDateTime readOffsetDateTime(final BinaryReader source) {
        return OffsetDateTime.parse(readStringRaw(source));
    }

    public OffsetTime readOffsetTime(final BinaryReader source) {
        return OffsetTime.parse(readStringRaw(source));
    }

    public Period readPeriod(final BinaryReader source) {
        return Period.parse(readStringRaw(source));
    }

    public short readShort(final BinaryReader source) {
        return Short.parseShort(requireNotHex(readStringRaw(source)));
    }

    public String readString(final BinaryReader source) throws DtoReadException {
        var p0 = begin; // Index of first non-appended byte in source.
        var p1 = begin; // Current source offset.
        final var p2 = end; // End of string source region.

        final var buffer = new byte[p2 - p1];
        var b0 = 0; // Index of first unwritten byte in buffer.

        var badEscapeBuilder = new StringBuilder(0);
        error:
        {
            while (p1 < p2) {
                var b = source.getByte(p1++);
                if (b == '&') {
                    // Collect bytes before escape sequence into buffer.
                    {
                        final var length = p1 - p0;
                        source.getBytes(p0, buffer, b0, length);
                        b0 += length;
                        p0 = p1;
                        p1 += length;
                    }

                    // Find end of escape sequence.
                    var px = p1;
                    int bx;
                    do {
                        if (px == p2) {
                            final var bytes = new byte[p1 - px];
                            source.getBytes(p1, bytes);
                            badEscapeBuilder
                                .append('&')
                                .append(new String(bytes, StandardCharsets.UTF_8));
                            break error;
                        }
                        bx = source.getByte(px++);
                    } while (bx != ';');

                    final var bytes = new byte[p1 - (px - 1)];
                    if (bytes.length == 0) {
                        badEscapeBuilder.append("&;");
                        break error;
                    }
                    p1 = px;

                    source.getBytes(p1, bytes);
                    final var escape = new String(bytes, StandardCharsets.UTF_8);

                    if (escape.charAt(0) == '#') {
                        if (escape.length() < 3 || escape.charAt(1) != 'x') {
                            badEscapeBuilder
                                .append('&')
                                .append(escape)
                                .append(';');
                            break error;
                        }
                        final var cp = Integer.parseInt(escape, 2, escape.length(), 16);
                        b0 += Unicode.writeAsUtf8To(cp, buffer, b0);
                        continue;
                    }

                    switch (escape.toLowerCase()) {
                    case "amp": b = '&'; break;
                    case "apos": b = '\''; break;
                    case "gt": b = '>'; break;
                    case "lt": b = '<'; break;
                    case "quot": b = '"'; break;
                    default:
                        badEscapeBuilder
                            .append('&')
                            .append(escape)
                            .append(';');
                        break error;
                    }
                    buffer[b0++] = b;
                }
            }
            source.getBytes(p0, buffer, b0, p1 - p0);
            return new String(buffer, StandardCharsets.UTF_8);
        }
        throw new DtoReadException(DtoEncoding.XML, "Bad escape", badEscapeBuilder.toString(), p1);
    }

    public String readStringRaw(final BinaryReader source) {
        final var buffer = new byte[length()];
        source.getBytes(begin, buffer);
        return new String(buffer, StandardCharsets.ISO_8859_1);
    }

    public Year readYear(final BinaryReader source) {
        return Year.parse(readStringRaw(source));
    }

    public Year readYearNumber(final BinaryReader source) {
        final var number = Double.parseDouble(readStringRaw(source));
        return Year.of((int) number);
    }

    public YearMonth readYearMonth(final BinaryReader source) {
        return YearMonth.parse(readStringRaw(source));
    }

    public ZoneId readZoneId(final BinaryReader source) {
        return ZoneId.of(readStringRaw(source));
    }

    public ZoneOffset readZoneOffset(final BinaryReader source) {
        return ZoneOffset.of(readStringRaw(source));
    }

    private static String requireNotHex(final String string) {
        if (string.length() > 2 && string.charAt(0) == '0') {
            final var x = string.charAt(1);
            if (x == 'x' || x == 'X') {
                throw new NumberFormatException("Unexpected x");
            }
        }
        return string;
    }
}
