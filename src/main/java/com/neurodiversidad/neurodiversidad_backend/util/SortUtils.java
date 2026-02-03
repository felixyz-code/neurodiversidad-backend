package com.neurodiversidad.neurodiversidad_backend.util;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class SortUtils {

    private SortUtils() {
    }

    public static Sort parseSort(List<String> sortParams, Set<String> allowedFields, Sort defaultSort) {
        if (sortParams == null || sortParams.isEmpty()) {
            return defaultSort;
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < sortParams.size(); i++) {
            String raw = sortParams.get(i);
            if (raw == null || raw.isBlank()) {
                continue;
            }
            String rawTrimmed = raw.trim().toLowerCase(Locale.ROOT);
            if ("asc".equals(rawTrimmed) || "desc".equals(rawTrimmed)) {
                // Ignore stray direction-only values to keep backward compatibility.
                continue;
            }
            String[] parts = raw.split(",");
            String field = parts[0].trim();
            if (field.isEmpty() || !allowedFields.contains(field)) {
                throw new IllegalArgumentException("sort inválido: " + raw);
            }
            String dir;
            if (parts.length > 1) {
                dir = parts[1].trim().toLowerCase(Locale.ROOT);
            } else {
                // Support legacy pattern: sort=field&sort=asc|desc
                String nextDir = null;
                for (int j = i + 1; j < sortParams.size(); j++) {
                    String next = sortParams.get(j);
                    if (next == null || next.isBlank()) {
                        continue;
                    }
                    String nextTrimmed = next.trim().toLowerCase(Locale.ROOT);
                    if ("asc".equals(nextTrimmed) || "desc".equals(nextTrimmed)) {
                        nextDir = nextTrimmed;
                    }
                    break;
                }
                dir = nextDir != null ? nextDir : "asc";
            }
            Sort.Direction direction;
            if ("asc".equals(dir)) {
                direction = Sort.Direction.ASC;
            } else if ("desc".equals(dir)) {
                direction = Sort.Direction.DESC;
            } else {
                throw new IllegalArgumentException("sort inválido: " + raw);
            }
            orders.add(new Sort.Order(direction, field));
        }

        if (orders.isEmpty()) {
            return defaultSort;
        }
        return Sort.by(orders);
    }
}
