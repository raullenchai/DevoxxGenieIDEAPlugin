package com.devoxx.genie.chatmodel.local.rapidmlx;

import com.devoxx.genie.model.CustomChatModel;
import com.devoxx.genie.model.LanguageModel;
import com.devoxx.genie.model.enumarations.ModelProvider;
import com.devoxx.genie.model.rapidmlx.RapidMLXModelEntryDTO;
import com.devoxx.genie.ui.settings.DevoxxGenieStateService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RapidMLXChatModelFactoryTest {

    private static RapidMLXModelEntryDTO modelEntry(String id, Integer maxModelLen) {
        RapidMLXModelEntryDTO entry = new RapidMLXModelEntryDTO();
        entry.setId(id);
        entry.setObject("model");
        entry.setMaxModelLen(maxModelLen);
        return entry;
    }

    @Test
    void createChatModel_usesConfiguredRapidMLXUrl() {
        try (MockedStatic<DevoxxGenieStateService> ignored = Mockito.mockStatic(DevoxxGenieStateService.class)) {
            DevoxxGenieStateService state = mock(DevoxxGenieStateService.class);
            when(DevoxxGenieStateService.getInstance()).thenReturn(state);
            when(state.getRapidMLXModelUrl()).thenReturn("http://localhost:8000/v1/");

            CustomChatModel customChatModel = new CustomChatModel();
            customChatModel.setModelName("qwen3.5-4b-4bit");

            ChatModel result = new RapidMLXChatModelFactory().createChatModel(customChatModel);

            assertThat(result).isNotNull();
        }
    }

    @Test
    void createStreamingChatModel_usesConfiguredRapidMLXUrl() {
        try (MockedStatic<DevoxxGenieStateService> ignored = Mockito.mockStatic(DevoxxGenieStateService.class)) {
            DevoxxGenieStateService state = mock(DevoxxGenieStateService.class);
            when(DevoxxGenieStateService.getInstance()).thenReturn(state);
            when(state.getRapidMLXModelUrl()).thenReturn("http://localhost:8000/v1/");

            CustomChatModel customChatModel = new CustomChatModel();
            customChatModel.setModelName("qwen3.5-4b-4bit");

            StreamingChatModel result = new RapidMLXChatModelFactory().createStreamingChatModel(customChatModel);

            assertThat(result).isNotNull();
        }
    }

    @Test
    void buildLanguageModel_withReportedMaxModelLen_usesIt() {
        LanguageModel model = new RapidMLXChatModelFactory()
                .buildLanguageModel(modelEntry("qwen3.5-4b-4bit", 32_768));

        assertThat(model.getProvider()).isEqualTo(ModelProvider.RapidMLX);
        assertThat(model.getModelName()).isEqualTo("qwen3.5-4b-4bit");
        assertThat(model.getDisplayName()).isEqualTo("qwen3.5-4b-4bit");
        assertThat(model.getInputMaxTokens()).isEqualTo(32_768);
        assertThat(model.isApiKeyUsed()).isFalse();
        assertThat(model.getInputCost()).isZero();
        assertThat(model.getOutputCost()).isZero();
    }

    @Test
    void buildLanguageModel_withoutMaxModelLen_usesDefaultContextLength() {
        LanguageModel model = new RapidMLXChatModelFactory()
                .buildLanguageModel(modelEntry("mlx-community/Qwen3.5-4B-MLX-4bit", null));

        assertThat(model.getDisplayName()).isEqualTo("Qwen3.5-4B-MLX-4bit");
        assertThat(model.getInputMaxTokens()).isEqualTo(RapidMLXChatModelFactory.DEFAULT_CONTEXT_LENGTH);
    }

    @Test
    void buildLanguageModel_withNonPositiveMaxModelLen_usesDefaultContextLength() {
        LanguageModel model = new RapidMLXChatModelFactory()
                .buildLanguageModel(modelEntry("qwen3.5-4b-4bit", 0));

        assertThat(model.getInputMaxTokens()).isEqualTo(RapidMLXChatModelFactory.DEFAULT_CONTEXT_LENGTH);
    }

    @Test
    void resolveDisplayName_withTrailingSlash_keepsRawId() {
        assertThat(modelEntry("mlx-community/", null).resolveDisplayName()).isEqualTo("mlx-community/");
    }

    @Test
    void resolveDisplayName_withNullId_returnsEmptyString() {
        assertThat(new RapidMLXModelEntryDTO().resolveDisplayName()).isEmpty();
    }
}
