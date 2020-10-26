package se.arkalix.core.plugin;

import se.arkalix.ServiceInterface;
import se.arkalix.dto.DtoEqualsHashCode;
import se.arkalix.dto.DtoReadableAs;
import se.arkalix.dto.DtoToString;
import se.arkalix.dto.json.JsonName;

import static se.arkalix.dto.DtoEncoding.JSON;

/**
 * The name of an {@link ServiceInterface interface triplet}.
 */
@DtoReadableAs(JSON)
@DtoEqualsHashCode
@DtoToString
public interface InterfaceName {
    /**
     * Interface triplet.
     */
    @JsonName("interfaceName")
    ServiceInterface name();
}
