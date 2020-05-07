package se.arkalix.dto;

import se.arkalix.dto.json.JsonReader;
import se.arkalix.dto.json.JsonWriter;
import se.arkalix.dto.xml.XmlReader;
import se.arkalix.dto.xml.XmlWriter;

import java.util.Objects;

/**
 * Enumerates the encodings that can be read and written by the Kalix
 * {@link se.arkalix.dto DTO} package.
 */
public enum DtoEncoding {
    /**
     * JavaScript Object Notation (JSON).
     *
     * @see <a href="https://tools.ietf.org/html/rfc8259">RFC 8259</a>
     */
    JSON(JsonReader.instance(), JsonWriter.instance()),

    /**
     * Extensible Markup Language (XML).
     *
     * @see <a href="https://www.w3.org/TR/xml11/">Extensible Markup Language (XML) 1.1 (Second Edition)</a>
     */
    XML(XmlReader.instance(), XmlWriter.instance()),
    ;

    private final DtoReader reader;
    private final DtoWriter writer;

    DtoEncoding(final DtoReader reader, final DtoWriter writer) {
        this.reader = Objects.requireNonNull(reader, "Expected reader");
        this.writer = Objects.requireNonNull(writer, "Expected writer");
    }

    /**
     * @return Reader useful for decoding objects encoded with this encoding.
     */
    public DtoReader reader() {
        return reader;
    }

    /**
     * @return Writer useful for encoding objects encoded with this encoding.
     */
    public DtoWriter writer() {
        return writer;
    }
}
