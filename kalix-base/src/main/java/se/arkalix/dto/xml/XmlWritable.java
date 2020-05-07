package se.arkalix.dto.xml;

import se.arkalix.dto.DtoWritable;
import se.arkalix.dto.DtoWriteException;
import se.arkalix.dto.binary.BinaryWriter;

/**
 * Represents a type that can be written as XML.
 * <p>
 * <i>Do not implement this interface directly.</i> It is implemented
 * automatically by generated {@link se.arkalix.dto DTO classes} where XML is
 * specified as a writable encoding.
 */
public interface XmlWritable extends DtoWritable {
    void writeXml(BinaryWriter writer) throws DtoWriteException;
}
