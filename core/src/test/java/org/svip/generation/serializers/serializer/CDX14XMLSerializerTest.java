package org.svip.generation.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.serializer.CDX14JSONSerializer;
import org.svip.serializers.serializer.Serializer;
import org.svip.utils.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CDX14XMLSerializerTest extends SerializerTest {

    public CDX14XMLSerializerTest(Serializer serializer) {
        super(serializer);
    }
}