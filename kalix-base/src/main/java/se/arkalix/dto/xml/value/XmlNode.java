package se.arkalix.dto.xml.value;

import se.arkalix.dto.xml.XmlReadable;
import se.arkalix.dto.xml.XmlWritable;

@SuppressWarnings("unused")
public interface XmlNode extends XmlReadable, XmlWritable {
    XmlType type();
}
