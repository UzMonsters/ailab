package com.ailab.chemistry.infrastructure.persistence.element.property;

import com.ailab.chemistry.domain.element.StandardState;
import com.ailab.chemistry.domain.element.property.*;
import com.ailab.chemistry.domain.measurement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ElementPropertyMapper {

    public static ElementPropertyProfile toDomain(ElementPropertyProfileEntity entity) {
        if (entity == null) return null;

        PropertyDatasetVersion version = new PropertyDatasetVersion(
                entity.getDatasetVersionId(),
                "IUPAC / CRC / NIST Extended Element Properties Dataset",
                "2026-08-04"
        );

        List<Valency> valencies = entity.getValencies().stream()
                .map(v -> new Valency(
                        v.getValency(),
                        v.isCommon(),
                        ScientificEvidenceStatus.valueOf(v.getEvidenceStatus()),
                        new PropertyProvenance(v.getSourceIdentifier(), v.getSourceTitle(), "CRC Press", "104", "2026-08-04", "Valency", "Open")
                ))
                .collect(Collectors.toList());

        List<OxidationState> oxidationStates = entity.getOxidationStates().stream()
                .map(os -> new OxidationState(
                        os.getState(),
                        os.isCommon(),
                        os.isUncommon(),
                        os.isPredicted(),
                        ScientificEvidenceStatus.valueOf(os.getEvidenceStatus()),
                        new PropertyProvenance(os.getSourceIdentifier(), os.getSourceTitle(), "CRC Press", "104", "2026-08-04", "OxidationState", "Open")
                ))
                .collect(Collectors.toList());

        List<Electronegativity> electronegativities = entity.getElectronegativities().stream()
                .map(en -> new Electronegativity(
                        en.getValue(),
                        ElectronegativityScale.valueOf(en.getScale()),
                        en.isPredicted(),
                        ScientificEvidenceStatus.valueOf(en.getEvidenceStatus()),
                        new PropertyProvenance(en.getSourceIdentifier(), en.getSourceTitle(), "CRC Press", "104", "2026-08-04", "Electronegativity", "Open")
                ))
                .collect(Collectors.toList());

        List<ElementRadius> radii = entity.getRadii().stream()
                .map(r -> {
                    RadiusKind kind = RadiusKind.valueOf(r.getKind());
                    IonicRadiusContext ionicContext = (kind == RadiusKind.IONIC && r.getIonicCharge() != null)
                            ? new IonicRadiusContext(r.getIonicCharge(), r.getCoordinationNumber(), r.getSpinState() != null ? ElectronSpinState.valueOf(r.getSpinState()) : ElectronSpinState.NOT_APPLICABLE)
                            : null;
                    return new ElementRadius(
                            kind,
                            Length.of(r.getRadiusPm(), LengthUnit.PICOMETER),
                            ionicContext,
                            ScientificEvidenceStatus.valueOf(r.getEvidenceStatus()),
                            new PropertyProvenance(r.getSourceIdentifier(), r.getSourceTitle(), "CRC Press", "104", "2026-08-04", "Radius", "Open")
                    );
                })
                .collect(Collectors.toList());

        List<DensityDatum> densities = entity.getDensities().stream()
                .map(d -> new DensityDatum(
                        Density.of(d.getDensityKgM3(), DensityUnit.KILOGRAM_PER_CUBIC_METER),
                        d.getRefTempK() != null ? Temperature.of(d.getRefTempK(), TemperatureUnit.KELVIN) : null,
                        d.getRefPressureKpa() != null ? Pressure.of(d.getRefPressureKpa(), PressureUnit.KILOPASCAL) : null,
                        d.getRefState() != null ? StandardState.valueOf(d.getRefState()) : null,
                        ScientificEvidenceStatus.valueOf(d.getEvidenceStatus()),
                        new PropertyProvenance(d.getSourceIdentifier(), d.getSourceTitle(), "CRC Press", "104", "2026-08-04", "Density", "Open")
                ))
                .collect(Collectors.toList());

        List<PhaseTransitionDatum> phaseTransitions = entity.getPhaseTransitions().stream()
                .map(pt -> new PhaseTransitionDatum(
                        PhaseTransitionKind.valueOf(pt.getKind()),
                        pt.getTempK() != null ? Temperature.of(pt.getTempK(), TemperatureUnit.KELVIN) : null,
                        pt.getRefPressureKpa() != null ? Pressure.of(pt.getRefPressureKpa(), PressureUnit.KILOPASCAL) : null,
                        TransitionBehavior.valueOf(pt.getBehavior()),
                        ScientificEvidenceStatus.valueOf(pt.getEvidenceStatus()),
                        new PropertyProvenance(pt.getSourceIdentifier(), pt.getSourceTitle(), "CRC Press", "104", "2026-08-04", "PhaseTransition", "Open")
                ))
                .collect(Collectors.toList());

        ElementAppearance appearance = entity.getAppearance() != null
                ? new ElementAppearance(
                        entity.getAppearance().getNormalizedColorName(),
                        entity.getAppearance().getAppearanceDescription(),
                        ScientificEvidenceStatus.valueOf(entity.getAppearance().getEvidenceStatus()),
                        new PropertyProvenance(entity.getAppearance().getSourceIdentifier(), entity.getAppearance().getSourceTitle(), "CRC Press", "104", "2026-08-04", "Appearance", "Open")
                )
                : null;

        return new ElementPropertyProfile(
                entity.getAtomicNumber(),
                entity.getSymbol(),
                version,
                valencies,
                oxidationStates,
                electronegativities,
                radii,
                new ElementPhysicalProperties(densities, phaseTransitions),
                appearance
        );
    }
}
