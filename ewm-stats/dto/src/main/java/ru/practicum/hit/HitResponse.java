package ru.practicum.hit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HitResponse {
    private String app;
    private String uri;
    private Long hits;
}