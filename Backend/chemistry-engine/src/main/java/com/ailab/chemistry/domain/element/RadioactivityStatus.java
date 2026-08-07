package com.ailab.chemistry.domain.element;

/**
 * Classifies whether an element has stable isotopes.
 *
 * Scientific basis:
 * - HAS_STABLE_ISOTOPES: the element has at least one nuclide with a stable or effectively stable
 *   ground state. Source: IUPAC/NIST isotope tables.
 * - NO_STABLE_ISOTOPES: all known nuclides are radioactive. This includes elements where
 *   the longest-lived primordial isotope is radioactive (e.g. Bi-209, Tc-97, etc.).
 * - PRIMORDIAL_RADIOACTIVE: element occurs naturally on Earth but has no stable isotope;
 *   the most abundant/long-lived isotope is radioactive (e.g. Th, U, Ra, Rn).
 * - SYNTHETIC_RADIOACTIVE: element has no stable nuclides and does not occur naturally;
 *   only produced by artificial means (e.g. Tc, Pm, and all transuranic elements beyond Pu).
 * - UNKNOWN: classification cannot be determined from available data.
 *
 * Note on Bismuth (Bi, Z=83):
 * Bi-209 was confirmed in 2003 (Danevich et al.) to undergo alpha decay with a half-life of
 * ~2.01e19 years (about 10^9 times the age of the universe). Bismuth therefore has NO stable
 * isotope and must be classified as PRIMORDIAL_RADIOACTIVE. It must NOT be classified as
 * HAS_STABLE_ISOTOPES. This correction was applied in dataset v1.1.0.
 */
public enum RadioactivityStatus {
    /** Element has at least one genuinely stable isotope (ground-state binding makes decay unobservable). */
    HAS_STABLE_ISOTOPES,

    /** All nuclides are radioactive; element is primordially present (e.g. Ra, Th, U, Bi). */
    PRIMORDIAL_RADIOACTIVE,

    /** All nuclides are radioactive; element is only produced synthetically (e.g. Tc, Pm, transuranics). */
    SYNTHETIC_RADIOACTIVE,

    /** Classification undetermined. */
    UNKNOWN
}
