package com.market.MSA.requests.others;

import com.market.MSA.models.others.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterTokenRequest(@NotBlank String token, @NotNull Platform platform) {}
