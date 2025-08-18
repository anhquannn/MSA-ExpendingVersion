package com.market.MSA.requests.others;

import java.util.Map;

public record PushRequest(Long userId, String title, String body, Map<String, String> data) {}
