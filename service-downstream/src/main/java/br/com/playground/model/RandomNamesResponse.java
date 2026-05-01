package br.com.playground.model;

import java.util.List;

public record RandomNamesResponse(
        String resource,
        String status,
        int total,
        List<String> names
) {
}

