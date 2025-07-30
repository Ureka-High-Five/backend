package org.highfive.backend.action.log;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Builder
@AllArgsConstructor
public class MetaInfoLog {

    private Map<Long, String> genres;

    private Map<Long, String> director;

    private Map<Long, String> actors;

    private Map<Long, String> country;
}
