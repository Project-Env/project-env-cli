package ch.repolevedavaj.projectenv.core.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig;
import com.kjetland.jackson.jsonSchema.JsonSchemaDraft;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class JsonSchemaGenerator {

    @Test
    public void generateJsonSchema() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonSchemaConfig config = JsonSchemaConfig.vanillaJsonSchemaDraft4().withJsonSchemaDraft(JsonSchemaDraft.DRAFT_2019_09);
        com.kjetland.jackson.jsonSchema.JsonSchemaGenerator jsonSchemaGenerator = new com.kjetland.jackson.jsonSchema.JsonSchemaGenerator(objectMapper, config);

        JsonNode jsonSchema = jsonSchemaGenerator.generateJsonSchema(ProjectEnvConfiguration.class);

        String jsonSchemaAsString = objectMapper.writeValueAsString(jsonSchema);

        System.out.println(jsonSchemaAsString);
    }

}
