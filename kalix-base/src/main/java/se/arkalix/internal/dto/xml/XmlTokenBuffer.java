package se.arkalix.internal.dto.xml;

import se.arkalix.dto.binary.BinaryReader;
import se.arkalix.util.annotation.Internal;

import java.util.List;

@Internal
@SuppressWarnings("unused")
public class XmlTokenBuffer {
    private final List<XmlToken> tokens;
    private final BinaryReader source;
    private int offset;

    XmlTokenBuffer(final List<XmlToken> tokens, final BinaryReader source) {
        this.tokens = tokens;
        this.source = source;

        offset = 0;
    }

    public boolean atEnd() {
        return offset == tokens.size();
    }

    public XmlToken next() {
        return tokens.get(offset++);
    }

    public XmlToken peek() {
        return tokens.get(offset);
    }

    public void skipOne() {
        offset += 1;
    }

    public void skipElement() {
        var token = next();
        if (token.nChildren() == 0) {
            return;
        }
        if (token.type() == XmlTokenType.ELEMENT) {
            offset += token.nAttributes() * 2;
            for (var n = token.nChildren(); n-- != 0; ) {
                skipElement();
            }
        }
    }

    public BinaryReader source() {
        return source;
    }
}

