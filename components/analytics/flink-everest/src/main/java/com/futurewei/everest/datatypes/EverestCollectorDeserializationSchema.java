package com.futurewei.everest.datatypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;


public class EverestCollectorDeserializationSchema implements DeserializationSchema<EverestCollectorData> {
    private static final long serialVersionUID = 6154188370181669759L;
    static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public EverestCollectorData deserialize(byte[] message) throws IOException {
        return objectMapper.readValue(message, EverestCollectorData.class);
    }

    @Override
    public boolean isEndOfStream(EverestCollectorData nextElement) {
        return false;
    }

    @Override
    public TypeInformation<EverestCollectorData> getProducedType() {
        return TypeInformation.of(new TypeHint<EverestCollectorData>() {
        });
    }
}