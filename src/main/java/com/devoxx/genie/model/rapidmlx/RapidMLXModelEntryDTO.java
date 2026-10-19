package com.devoxx.genie.model.rapidmlx;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * One entry of Rapid-MLX's OpenAI-compatible {@code GET /v1/models} response.
 * <p>
 * The id is the short model alias the server was started with (e.g. {@code qwen3.5-4b-4bit}) or a
 * Hugging Face repo id. {@code max_model_len} reports the context window the server will accept.
 */
@Getter
@Setter
public class RapidMLXModelEntryDTO {

    @SerializedName("id")
    private String id;

    @SerializedName("object")
    private String object;

    @SerializedName("created")
    private Long created;

    @SerializedName("max_model_len")
    private Integer maxModelLen;

    /**
     * Strips a Hugging Face org prefix (e.g. {@code mlx-community/}) so the model dropdown shows the
     * short name. Falls back to the raw id when there is no prefix (or nothing after it).
     */
    public @NotNull String resolveDisplayName() {
        if (id == null || id.isBlank()) {
            return "";
        }
        int slash = id.lastIndexOf('/');
        if (slash < 0 || slash == id.length() - 1) {
            return id;
        }
        return id.substring(slash + 1);
    }
}
