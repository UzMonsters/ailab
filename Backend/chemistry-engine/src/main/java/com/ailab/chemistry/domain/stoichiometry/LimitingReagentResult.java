package com.ailab.chemistry.domain.stoichiometry;

import java.util.*;

public final class LimitingReagentResult {

    private final List<String> limitingCompoundCodes;
    private final Map<String, ReactionExtent> extents;
    private final boolean isTied;

    public LimitingReagentResult(List<String> limitingCompoundCodes, Map<String, ReactionExtent> extents) {
        Objects.requireNonNull(limitingCompoundCodes, "Limiting compound codes must not be null");
        if (limitingCompoundCodes.isEmpty()) {
            throw new StoichiometryException(StoichiometryErrorCode.MISSING_REACTANTS, "Limiting reagent list cannot be empty");
        }
        this.limitingCompoundCodes = Collections.unmodifiableList(new ArrayList<>(limitingCompoundCodes));
        this.extents = Collections.unmodifiableMap(new HashMap<>(Objects.requireNonNull(extents, "Extents map must not be null")));
        this.isTied = limitingCompoundCodes.size() > 1;
    }

    public List<String> getLimitingCompoundCodes() {
        return limitingCompoundCodes;
    }

    public String getPrimaryLimitingCompoundCode() {
        return limitingCompoundCodes.get(0);
    }

    public Map<String, ReactionExtent> getExtents() {
        return extents;
    }

    public boolean isTied() {
        return isTied;
    }

    public ReactionExtent getLimitingExtent() {
        return extents.get(getPrimaryLimitingCompoundCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LimitingReagentResult that = (LimitingReagentResult) o;
        return isTied == that.isTied &&
                limitingCompoundCodes.equals(that.limitingCompoundCodes) &&
                extents.equals(that.extents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(limitingCompoundCodes, extents, isTied);
    }

    @Override
    public String toString() {
        return "LimitingReagentResult{limiting=" + limitingCompoundCodes + ", isTied=" + isTied + '}';
    }
}
