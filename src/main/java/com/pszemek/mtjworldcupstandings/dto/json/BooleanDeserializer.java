package com.pszemek.mtjworldcupstandings.dto.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class BooleanDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        if("TRUE".equalsIgnoreCase(p.getText())) {
            return Boolean.TRUE;
        } else return Boolean.FALSE;
    }
}
