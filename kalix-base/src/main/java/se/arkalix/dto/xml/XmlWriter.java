package se.arkalix.dto.xml;

import se.arkalix.dto.DtoWritable;
import se.arkalix.dto.DtoWriteException;
import se.arkalix.dto.DtoWriter;
import se.arkalix.dto.binary.BinaryWriter;
import se.arkalix.dto.json.JsonWritable;

import java.util.List;

public class XmlWriter implements DtoWriter {
    private static final XmlWriter INSTANCE = new XmlWriter();

    private XmlWriter() {}

    /**
     * @return Reference to XML writer singleton instance.
     */
    public static XmlWriter instance() {
        return INSTANCE;
    }

    @Override
    public <U extends DtoWritable> void writeOne(final U value, final BinaryWriter target) throws DtoWriteException {
        if (!(value instanceof XmlWritable)) {
            throw xmlNotSupportedBy(value.getClass());
        }
        ((XmlWritable) value).writeXml(target);
    }

    @Override
    public <U extends DtoWritable> void writeMany(final List<U> values, final BinaryWriter target) throws DtoWriteException {
        if (values.isEmpty()) {
            return;
        }
        if (!(values.get(0) instanceof XmlWritable)) {
            throw xmlNotSupportedBy(values.get(0).getClass());
        }
        for (final var value : values) {
            ((XmlWritable) value).writeXml(target);
        }
    }

    private static RuntimeException xmlNotSupportedBy(final Class<?> class_) {
        return new UnsupportedOperationException("\"" + class_ + "\" does " +
            "not implement XmlWritable; if the class was produced by the " +
            "DTO code generator, this is likely caused by its input " +
            "interface not having DtoEncoding.XML as argument to its " +
            "@DtoWritableAs annotation");
    }
}
