package se.arkalix.dto.xml.value;

import java.util.Objects;

/**
 * An XML attribute.
 *
 * @see <a href="https://www.w3.org/TR/xml11/">Extensible Markup Language (XML) 1.1 (Second Edition)</a>
 */
@SuppressWarnings("unused")
public class XmlAttribute {
    private final String name;
    private final String value;

    /**
     * Creates new XML attribute from given name and value.
     *
     * @param name  Attribute name.
     * @param value Attribute value.
     */
    public XmlAttribute(final String name, final String value) {
        this.name = Objects.requireNonNull(name, "Expected name");
        this.value = Objects.requireNonNull(value, "Expected value");
    }

    /**
     * @return Attribute name.
     */
    public String name() {
        return name;
    }

    /**
     * @return Attribute value.
     */
    public String value() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) { return true; }
        if (other == null || getClass() != other.getClass()) { return false; }
        final XmlAttribute attribute = (XmlAttribute) other;
        return name.equals(attribute.name) &&
            value.equals(attribute.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }
}
