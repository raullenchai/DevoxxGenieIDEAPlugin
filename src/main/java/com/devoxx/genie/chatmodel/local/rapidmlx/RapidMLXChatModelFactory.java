package com.devoxx.genie.chatmodel.local.rapidmlx;

import com.devoxx.genie.chatmodel.local.LocalChatModelFactory;
import com.devoxx.genie.model.CustomChatModel;
import com.devoxx.genie.model.LanguageModel;
import com.devoxx.genie.model.enumarations.ModelProvider;
import com.devoxx.genie.model.rapidmlx.RapidMLXModelEntryDTO;
import com.devoxx.genie.ui.settings.DevoxxGenieStateService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Rapid-MLX (<a href="https://github.com/raullenchai/Rapid-MLX">github.com/raullenchai/Rapid-MLX</a>)
 * is an open-source server that runs MLX models locally on Apple Silicon and serves them over an
 * OpenAI-compatible API, so it plugs straight into the shared OpenAI chat/streaming clients.
 */
public class RapidMLXChatModelFactory extends LocalChatModelFactory {

    /**
     * Used only when {@code /v1/models} does not report {@code max_model_len} for a model.
     */
    public static final int DEFAULT_CONTEXT_LENGTH = 8000;

    public RapidMLXChatModelFactory() {
        super(ModelProvider.RapidMLX);
    }

    @Override
    public ChatModel createChatModel(@NotNull CustomChatModel customChatModel) {
        return createOpenAiChatModel(customChatModel);
    }

    @Override
    public StreamingChatModel createStreamingChatModel(@NotNull CustomChatModel customChatModel) {
        return createOpenAiStreamingChatModel(customChatModel);
    }

    @Override
    protected String getModelUrl() {
        return DevoxxGenieStateService.getInstance().getRapidMLXModelUrl();
    }

    @Override
    protected RapidMLXModelEntryDTO[] fetchModels() throws IOException {
        return RapidMLXModelService.getInstance().getModels().toArray(new RapidMLXModelEntryDTO[0]);
    }

    @Override
    protected LanguageModel buildLanguageModel(Object model) {
        RapidMLXModelEntryDTO rapidModel = (RapidMLXModelEntryDTO) model;
        Integer maxModelLen = rapidModel.getMaxModelLen();
        return LanguageModel.builder()
                .provider(modelProvider)
                .modelName(rapidModel.getId())
                .displayName(rapidModel.resolveDisplayName())
                .inputCost(0)
                .outputCost(0)
                .inputMaxTokens(maxModelLen != null && maxModelLen > 0 ? maxModelLen : DEFAULT_CONTEXT_LENGTH)
                .apiKeyUsed(false)
                .build();
    }
}
