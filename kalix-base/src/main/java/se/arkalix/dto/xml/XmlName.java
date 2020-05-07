package se.arkalix.dto.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows overriding the name of a {@link se.arkalix.dto DTO interface} type or
 * field when encoding or decoding it to/from XML.
 * <p>
 * For example, given the following interface:
 * <pre>
 *      &#64;DtoReadableAs(XML)
 *      &#64;DtoWritableAs(XML)
 *      &#64;XmlName("rect")
 *      public interface Rectangle {
 *          &#64;XmlName("w")
 *          double width();
 *
 *          &#64;XmlName("h")
 *          double height();
 *      }
 * </pre>
 * Assuming it was instantiated with the width and height 100 and 400, it then
 * reads and writes as follows:
 * <pre>
 *     &lt;rect&gt;&lt;w&gt;100&lt;/w&gt;&lt;h&gt;400&lt;/h&gt;&lt;/rect&gt;
 * </pre>
 * Without the &#64;XmlName annotation, the same object would be represented as
 * <pre>
 *     &lt;Rectangle&gt;&lt;width&gt;100&lt;/width&gt;&lt;height&gt;400&lt;/height&gt;&lt;/Rectangle&gt;
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface XmlName {
    String value();
}
