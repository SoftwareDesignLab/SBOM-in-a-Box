<<<<<<<< HEAD:core/src/main/java/org/svip/conversion/toSchema/ToSchema.java
package org.svip.conversion.toSchema;
========
package org.svip.manipulation;
>>>>>>>> 982bc6daa (Conversion package is now known as the Manipulation package):core/src/main/java/org/svip/manipulation/Convert.java

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;

public interface ToSchema {

    /**
     * Builds a new SBOM of a specific schema using data from
     * a passed in SVIPSBOM. The new SBOM should map in as much
     * data as possible.
     *
     * @param sbom The SVIPSBOM with the data that needs to be mapped.
     * @return The new SBOM in the requested schema with the mapped data.
     */
    SBOM convert(SVIPSBOM sbom);

}
