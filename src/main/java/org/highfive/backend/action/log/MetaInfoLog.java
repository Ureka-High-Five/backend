package org.highfive.backend.action.log;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Builder
@AllArgsConstructor
public class MetaInfoLog {

    private List<String> genres;

    private String director;

    private List<String> actors;

    private String country;
}
