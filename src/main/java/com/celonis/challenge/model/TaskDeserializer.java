package com.celonis.challenge.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

// this is not clear if we can change/extend REST API for the new task types. assuming -> no.
// in this case we have to make a custom deserializer to map json to correct Task subclass.
// the better way would be to use a "type" field in the json to indicate the task type,
// but since it's not specified, we will use the presence of "x" field to determine if it's a CounterTask.
public class TaskDeserializer extends JsonDeserializer<Task> {

    @Override
    public Task deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        Task t;

        if (node.has("x")) {
            CounterTask counterTask = new CounterTask();
            counterTask.start = node.get("x").asInt();
            counterTask.end = node.get("y").asInt();
            t = counterTask;
        } else {
            ProjectGenerationTask projectTask = new ProjectGenerationTask();
            t = projectTask;
        }

        if (node.has("name")) {
            t.name = node.get("name").asText();
        }
        if (node.has("status")) {
            t.status = Status.valueOf(node.get("status").asText());
        }
        if (node.has("creationDate")) {
            t.creationDate = node.get("creationDate").asText() != null ? new java.util.Date(node.get("creationDate").asLong()) : null;
        }
        return t;
    }
}
