package com.devoxx.genie.chatmodel.local.rapidmlx;

import com.devoxx.genie.model.rapidmlx.RapidMLXModelsResponseDTO;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RapidMLXModelServiceTest {

    private static class TestableModelService extends RapidMLXModelService {
        String testBuildModelsUrl(String baseUrl) {
            return buildModelsUrl(baseUrl);
        }
    }

    private final TestableModelService service = new TestableModelService();
    private final Gson gson = new Gson();

    @Test
    void buildModelsUrl_withTrailingSlash_appendsModels() {
        assertThat(service.testBuildModelsUrl("http://localhost:8000/v1/"))
                .isEqualTo("http://localhost:8000/v1/models");
    }

    @Test
    void buildModelsUrl_withoutTrailingSlash_insertsSeparator() {
        assertThat(service.testBuildModelsUrl("http://localhost:8000/v1"))
                .isEqualTo("http://localhost:8000/v1/models");
    }

    @Test
    void buildModelsUrl_withCustomPort_preservesHostAndPort() {
        assertThat(service.testBuildModelsUrl("http://127.0.0.1:8001/v1/"))
                .isEqualTo("http://127.0.0.1:8001/v1/models");
    }

    @Test
    void buildModelsUrl_withNullBaseUrl_returnsDefaultUrl() {
        assertThat(service.testBuildModelsUrl(null)).isEqualTo("http://localhost:8000/v1/models");
    }

    @Test
    void buildModelsUrl_withBlankBaseUrl_returnsDefaultUrl() {
        assertThat(service.testBuildModelsUrl("   ")).isEqualTo("http://localhost:8000/v1/models");
    }

    @Test
    void buildModelsUrl_withSurroundingWhitespace_trimsBeforeAppending() {
        assertThat(service.testBuildModelsUrl("  http://localhost:8000/v1/  "))
                .isEqualTo("http://localhost:8000/v1/models");
    }

    @Test
    void responseDto_parsesModelsPayloadWithMaxModelLen() {
        String json = """
                {
                  "object": "list",
                  "data": [
                    {"id": "qwen3.5-4b-4bit", "object": "model", "created": 1760000000, "owned_by": "rapid-mlx", "max_model_len": 32768}
                  ]
                }
                """;

        RapidMLXModelsResponseDTO response = gson.fromJson(json, RapidMLXModelsResponseDTO.class);

        assertThat(response.getObject()).isEqualTo("list");
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().get(0).getId()).isEqualTo("qwen3.5-4b-4bit");
        assertThat(response.getData().get(0).getCreated()).isEqualTo(1760000000L);
        assertThat(response.getData().get(0).getMaxModelLen()).isEqualTo(32768);
    }

    @Test
    void responseDto_withEmptyModelList_yieldsEmptyData() {
        RapidMLXModelsResponseDTO response = gson.fromJson("{\"object\":\"list\",\"data\":[]}", RapidMLXModelsResponseDTO.class);

        assertThat(response.getData()).isEmpty();
    }
}
