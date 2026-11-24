package com.farmonboard.onboarding.web;

import java.util.List;

// One page of results, as JSON:
// {"content": [...], "page": 0, "size": 50, "totalElements": 3, "totalPages": 1}
// Our own shape: Spring Data advises against sending its Page object as JSON,
// because that JSON can change between Spring versions and break the mobile app.
// <T> makes it reusable: PageResponse<ApplicationResponse> now, other lists later.
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
