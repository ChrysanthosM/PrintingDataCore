package org.masouras.trace.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@Document(collection = "trace")
public class SpanInfo {
    @Id private String id;
    private String spanName;
    private long timestamp;
    private String methodName;
    private String parameters;
    private String result;
    private String error;
}

