package com.ailab.chemistry.domain.element;

/**
 * Classifies the evidence status of a recorded electron configuration.
 *
 * Source coverage:
 * - NIST Atomic Spectra Database / NIST Ground Levels covers Z=1 to Z=92 with
 *   evaluated/measured configurations.
 * - For Z=93 to Z=118, configurations are extrapolated from relativistic DFT
 *   calculations or theoretical prediction; ground states may differ from predictions.
 *
 * Rules applied in dataset v1.1.0:
 * - Z=1..92  → EVALUATED (backed by NIST measurement data)
 * - Z=93..103 → PREDICTED (extrapolated from actinide series theoretical models)
 * - Z=104..118 → PROVISIONAL (highly uncertain; superheavy relativistic effects)
 */
public enum ElectronConfigurationStatus {
    /** Configuration backed by spectroscopic/experimental data (NIST). */
    EVALUATED,
    /** Configuration extrapolated from theoretical models; not directly measured. */
    PREDICTED,
    /** Configuration provisional and subject to revision; superheavy elements. */
    PROVISIONAL,
    /** Configuration unknown or not available. */
    UNKNOWN
}
