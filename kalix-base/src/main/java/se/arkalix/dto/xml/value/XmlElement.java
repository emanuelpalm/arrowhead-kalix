package se.arkalix.dto.xml.value;

import se.arkalix.dto.DtoWriteException;
import se.arkalix.dto.binary.BinaryWriter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An XML element.
 *
 * @see <a href="https://www.w3.org/TR/xml11/">Extensible Markup Language (XML) 1.1 (Second Edition)</a>
 */
@SuppressWarnings("unused")
public class XmlElement implements XmlNode {
    private final String name;
    private final List<XmlAttribute> attributes;
    private final List<XmlNode> children;

    /**
     * Creates new empty XML element.
     *
     * @param tag Tag name.
     * @return New XML element.
     */
    public static XmlElement empty(final String tag) {
        return new XmlElement(tag, null, null);
    }

    /**
     * Creates new XML element with given attributes but no child nodes.
     *
     * @param tag        Tag name.
     * @param attributes XML attributes to associate with created element.
     * @return New XML element.
     */
    public static XmlElement withAttributes(final String tag, final List<XmlAttribute> attributes) {
        return new XmlElement(tag, attributes, null);
    }

    /**
     * Creates new XML element with given child nodes but no attributes.
     *
     * @param tag      Tag name.
     * @param children XML child nodes.
     * @return New XML element.
     */
    public static XmlElement withChildren(final String tag, final List<XmlNode> children) {
        return new XmlElement(tag, null, children);
    }

    /**
     * Creates new XML element.
     *
     * @param name       Tag name.
     * @param attributes XML attributes, if any.
     * @param children   XML child nodes, if any.
     */
    public XmlElement(final String name, final List<XmlAttribute> attributes, final List<XmlNode> children) {
        this.name = Objects.requireNonNull(name, "Expected name");
        this.attributes = Objects.requireNonNullElse(attributes, Collections.emptyList());
        this.children = Objects.requireNonNullElse(children, Collections.emptyList());
    }

    @Override
    public XmlType type() {
        return XmlType.ELEMENT;
    }

    public String name() {
        return name;
    }

    public List<XmlAttribute> attributes() {
        return attributes;
    }

    public List<XmlNode> children() {
        return children;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) { return true; }
        if (other == null || getClass() != other.getClass()) { return false; }
        final XmlElement that = (XmlElement) other;
        return name.equals(that.name) &&
            Objects.equals(attributes, that.attributes) &&
            Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, attributes, children);
    }

    @Override
    public String toString() {
        return "<" + name +
            (attributes.isEmpty() ? "" : attributes.stream()
                .map(XmlAttribute::toString).collect(Collectors.joining(" ", " ", ""))) + '>' +
            (children.isEmpty() ? "" : children.stream()
                .map(XmlNode::toString).collect(Collectors.joining())) +
            "</" + name + '>';
    }

    @Override
    public void writeXml(final BinaryWriter writer) throws DtoWriteException {

    }
}
