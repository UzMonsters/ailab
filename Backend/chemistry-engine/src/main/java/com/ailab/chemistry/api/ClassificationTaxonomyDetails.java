package com.ailab.chemistry.api;

import java.util.List;

public class ClassificationTaxonomyDetails {
    private final String version;
    private final String name;
    private final String publicationDate;
    private final List<DefinitionDetail> definitions;

    public ClassificationTaxonomyDetails(String version, String name, String publicationDate, List<DefinitionDetail> definitions) {
        this.version = version;
        this.name = name;
        this.publicationDate = publicationDate;
        this.definitions = List.copyOf(definitions);
    }

    public String getVersion() { return version; }
    public String getName() { return name; }
    public String getPublicationDate() { return publicationDate; }
    public List<DefinitionDetail> getDefinitions() { return definitions; }

    public static record DefinitionDetail(
            String code,
            String dimension,
            String name,
            String description,
            int sortOrder,
            String parentCode
    ) {}
}
