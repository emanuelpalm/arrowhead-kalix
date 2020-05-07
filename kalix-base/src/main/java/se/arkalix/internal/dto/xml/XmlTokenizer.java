package se.arkalix.internal.dto.xml;

import se.arkalix.dto.DtoEncoding;
import se.arkalix.dto.DtoReadException;
import se.arkalix.dto.binary.BinaryReader;
import se.arkalix.util.annotation.Internal;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Internal
@SuppressWarnings("unused")
public class XmlTokenizer {
    private final BinaryReader source;
    private final ArrayList<XmlToken> tokens;

    private int p0;
    private DtoReadException error = null;

    private XmlTokenizer(final BinaryReader source) {
        this.source = source;
        this.tokens = new ArrayList<>(source.readableBytes() / 16);
        this.p0 = source.readOffset();
    }

    public static XmlTokenBuffer tokenize(final BinaryReader source) throws DtoReadException {
        final var tokenizer = new XmlTokenizer(source);
        if (tokenizer.tokenizeRoot()) {
            return new XmlTokenBuffer(tokenizer.tokens, source);
        }
        throw tokenizer.error;
    }

    private XmlToken collectCandidate(final XmlTokenType type) {
        final var token = new XmlToken(type, p0, source.readOffset(), 0, 0);
        tokens.add(token);
        discardCandidate();
        return token;
    }

    private void discardCandidate() {
        p0 = source.readOffset();
    }

    private void saveCandidateAsError(final String message) {
        final var buffer = new byte[source.readOffset() - p0];
        source.getBytes(p0, buffer);
        error = new DtoReadException(DtoEncoding.JSON, message, new String(buffer, StandardCharsets.UTF_8), p0);
    }

    private void discardWhitespace() {
        for (byte b; source.readableBytes() > 0; ) {
            b = source.peekByte();
            if (b != '\t' && b != '\r' && b != '\n' && b != ' ') {
                break;
            }
            source.skipByte();
        }
        discardCandidate();
    }

    private boolean tokenizeRoot() {
        discardWhitespace();

        if (source.readByteOrZero() == '<') {
            return tokenizeElement();
        }

        saveCandidateAsError("Root not an element");
        return false;
    }

    private boolean tokenizeElement() {
        discardCandidate();

        byte b;

        // Start tag name.
        name:
        while (true) {
            b = source.readByteOrZero();
            switch (b) {
            case 0:
                saveCandidateAsError("Element start tag ended unexpectedly");
                return false;
            case '\t':
            case '\n':
            case '\r':
            case ' ':
            case '>':
                break name;
            }
        }

        final var element = collectCandidate(XmlTokenType.ELEMENT);
        element.end -= 1;

        // Attributes.
        while (true) {
            discardWhitespace();

            if (source.readableBytes() == 0) {
                saveCandidateAsError("Element start tag ended unexpectedly");
                return false;
            }

            b = source.peekByte();
            if (b == '>') {
                source.skipByte();
                break;
            }

            if (!tokenizeAttribute()) {
                return false;
            }

            element.nAttributes += 1;
        }

        // Empty element tag end.
        {
            discardWhitespace();

            if (source.readableBytes() == 0) {
                saveCandidateAsError("Element start tag ended unexpectedly");
                return false;
            }

            b = source.peekByte();
            if (b == '/') {
                source.skipByte();
                b = source.readByteOrZero();
                if (b != '>') {
                    saveCandidateAsError("Empty element tag must end with forward slang and right angle bracket (/>)");
                    return false;
                }
                discardCandidate();
                return true;
            }
        }

        // Content.
        while (true) {
            discardWhitespace();

            if (source.readableBytes() == 0) {
                saveCandidateAsError("Element has no end tag");
                return false;
            }

            b = source.readByte();
            if (b == '<') {
                break;
            }

            if (!tokenizeContent()) {
                return false;
            }

            element.nChildren += 1;
        }

        b = source.readByteOrZero();
        if (b != '/') {
            saveCandidateAsError("Element end tag must start with left angle bracket and forward slash (</)");
            return false;
        }

        // End tag name.
        name:
        while (true) {
            b = source.readByteOrZero();
            switch (b) {
            case 0:
                saveCandidateAsError("Element start tag ended unexpectedly");
                return false;
            case '>':
                break name;
            }
        }

        return true;
    }

    private boolean tokenizeAttribute() {
        byte b;
        do {
            if (source.readableBytes() == 0) {
                saveCandidateAsError("Attribute name ended unexpectedly");
                return false;
            }
            b = source.readByte();
        } while (b != '=');

        final var name = collectCandidate(XmlTokenType.ATTRIBUTE_NAME);
        name.end -= 1;

        b = source.readByteOrZero();
        if (b != '"') {
            saveCandidateAsError("Attribute value must start with double quote (\")");
            return false;
        }

        do {
            if (source.readableBytes() == 0) {
                saveCandidateAsError("Attribute value ended unexpectedly");
                return false;
            }
            b = source.readByte();
        } while (b != '"');

        final var value = collectCandidate(XmlTokenType.ATTRIBUTE_VALUE);
        value.begin += 1;
        value.end -= 1;

        return true;
    }

    private boolean tokenizeContent() {
        discardWhitespace();

        return false;
        /*
        var b = source.readByteOrZero();
        switch (b) {
        case 0:
            saveCandidateAsError("Element content ended unexpectedly");
            return false;
        case '<':
            break name;
        }

        if (source.readByteOrZero() == '<') {
            return tokenizeElement();
        }
        else {
            return tokenizeText();
        }*/
    }

    private boolean tokenizeText() {
        discardCandidate();

        return false;
    }
}
