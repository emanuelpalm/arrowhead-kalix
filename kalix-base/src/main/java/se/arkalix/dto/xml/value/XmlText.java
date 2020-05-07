package se.arkalix.dto.xml.value;

import se.arkalix.dto.DtoWriteException;
import se.arkalix.dto.binary.BinaryWriter;

import java.util.Objects;

@SuppressWarnings("unused")
public class XmlText implements XmlNode {
    private final String text;

    public XmlText(final String text) {
        this.text = Objects.requireNonNull(text, "Expected text");
    }

    @Override
    public XmlType type() {
        return XmlType.TEXT;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) { return true; }
        if (other == null || getClass() != other.getClass()) { return false; }
        final XmlText xmlText = (XmlText) other;
        return text.equals(xmlText.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public void writeXml(final BinaryWriter writer) throws DtoWriteException {

    }
}
