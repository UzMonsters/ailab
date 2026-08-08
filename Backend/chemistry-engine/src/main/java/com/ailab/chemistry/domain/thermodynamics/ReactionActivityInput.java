package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record ReactionActivityInput(List<ParticipantActivity> activities) {

    public ReactionActivityInput {
        activities = List.copyOf(activities);
    }

    public static ReactionActivityInput of(List<ParticipantActivity> activities) {
        return new ReactionActivityInput(activities);
    }

    public Optional<ParticipantActivity> find(String compoundCode, MatterState state) {
        return activities.stream()
                .filter(activity -> activity.compoundCode().equalsIgnoreCase(compoundCode))
                .filter(activity -> activity.state() == state)
                .findFirst();
    }

    public Map<String, ParticipantActivity> asMap() {
        LinkedHashMap<String, ParticipantActivity> map = new LinkedHashMap<>();
        for (ParticipantActivity activity : activities) {
            map.put(activity.key(), activity);
        }
        return Map.copyOf(map);
    }
}
