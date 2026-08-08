package com.ailab.chemistry.domain.thermodynamics;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record ReactionThermodynamicPropertySet(Map<ReactionThermodynamicProperty, ReactionThermodynamicResultProperty> properties) {

    public ReactionThermodynamicPropertySet {
        EnumMap<ReactionThermodynamicProperty, ReactionThermodynamicResultProperty> copy =
                new EnumMap<>(ReactionThermodynamicProperty.class);
        if (properties != null) {
            copy.putAll(properties);
        }
        properties = Map.copyOf(copy);
    }

    public static ReactionThermodynamicPropertySet of(ReactionThermodynamicResultProperty... properties) {
        EnumMap<ReactionThermodynamicProperty, ReactionThermodynamicResultProperty> map =
                new EnumMap<>(ReactionThermodynamicProperty.class);
        for (ReactionThermodynamicResultProperty property : properties) {
            map.put(property.property(), property);
        }
        return new ReactionThermodynamicPropertySet(map);
    }

    public static ReactionThermodynamicPropertySet of(List<ReactionThermodynamicResultProperty> properties) {
        return of(properties.toArray(ReactionThermodynamicResultProperty[]::new));
    }
}
