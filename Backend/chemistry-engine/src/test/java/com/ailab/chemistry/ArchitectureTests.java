package com.ailab.chemistry;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTests {

    @Test
    void chemistryEngineShouldNotDependOnAuthOrUser() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("com.ailab.auth..", "com.ailab.user..");

        rule.check(importedClasses);
    }

    @Test
    void measurementDomainShouldNotDependOnFrameworksOrInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry.domain.measurement");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "com.ailab.auth..",
                        "com.ailab.user.."
                );

        rule.check(importedClasses);
    }

    @Test
    void elementDomainShouldNotDependOnFrameworksOrInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry.domain.element");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "com.ailab.auth..",
                        "com.ailab.user..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void formulaDomainShouldNotDependOnFrameworksOrInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry.domain.formula");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "com.ailab.auth..",
                        "com.ailab.user..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void equationDomainShouldNotDependOnFrameworksOrInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry.domain.equation");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "com.ailab.auth..",
                        "com.ailab.user..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void compoundDomainShouldNotDependOnFrameworksOrInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry.domain.compound");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "org.flywaydb..",
                        "com.ailab.auth..",
                        "com.ailab.user..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void compoundDomainShouldNotContainDuplicateAtomicMassTable() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry.domain.compound");

        ArchRule rule = noClasses()
                .should()
                .haveSimpleName("KnownAtomicMassTable")
                .orShould()
                .haveSimpleName("KnownElementMassRegistry");

        rule.check(importedClasses);
    }

    @Test
    void classificationDomainShouldNotDependOnFrameworksOrInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry.domain.classification");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "org.flywaydb..",
                        "com.ailab.auth..",
                        "com.ailab.user..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void physicalPropertyDomainShouldNotDependOnFrameworksOrInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry.domain.physicalproperty");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "org.flywaydb..",
                        "com.ailab.auth..",
                        "com.ailab.user..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void hazardDomainShouldNotDependOnFrameworksOrInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry.domain.hazard");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "org.flywaydb..",
                        "com.ailab.auth..",
                        "com.ailab.user..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void acidBaseDomainShouldNotDependOnFrameworksOrInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry.domain.acidbase");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "org.flywaydb..",
                        "com.ailab.auth..",
                        "com.ailab.user..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void thermodynamicDomainShouldNotDependOnFrameworksOrInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry.domain.thermodynamics");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "org.flywaydb..",
                        "com.ailab.auth..",
                        "com.ailab.user..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void gasPhaseBehaviorAndElectrochemistryDomainsShouldNotDependOnFrameworksOrInfrastructure() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(
                "com.ailab.chemistry.domain.gas",
                "com.ailab.chemistry.domain.phasebehavior",
                "com.ailab.chemistry.domain.electrochemistry");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "org.flywaydb..",
                        "com.ailab.auth..",
                        "com.ailab.user..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void phaseTenShouldNotExposeExcludedRealGasOrPhaseDiagramApis() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry");

        ArchRule rule = noClasses()
                .should()
                .haveSimpleNameContaining("PengRobinson")
                .orShould()
                .haveSimpleNameContaining("RedlichKwong")
                .orShould()
                .haveSimpleNameContaining("VanDerWaals")
                .orShould()
                .haveSimpleNameContaining("Fugacity")
                .orShould()
                .haveSimpleNameContaining("Flash")
                .orShould()
                .haveSimpleNameContaining("Vle")
                .orShould()
                .haveSimpleNameContaining("PhaseDiagram");

        rule.check(importedClasses);
    }

    @Test
    void phaseElevenShouldNotExposeExcludedElectrochemicalEngineeringApis() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.ailab.chemistry");

        ArchRule rule = noClasses()
                .should()
                .haveSimpleNameContaining("ButlerVolmer")
                .orShould()
                .haveSimpleNameContaining("Tafel")
                .orShould()
                .haveSimpleNameContaining("Overpotential")
                .orShould()
                .haveSimpleNameContaining("Corrosion")
                .orShould()
                .haveSimpleNameContaining("CyclicVoltammetry")
                .orShould()
                .haveSimpleNameContaining("Impedance")
                .orShould()
                .haveSimpleNameContaining("CurrentDensity");

        rule.check(importedClasses);
    }

    @Test
    void phaseTwelveDomainsShouldRemainFrameworkIndependent() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(
                        "com.ailab.chemistry.domain.equipment",
                        "com.ailab.chemistry.domain.container",
                        "com.ailab.chemistry.domain.labenvironment",
                        "com.ailab.chemistry.domain.laboratory");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "org.flywaydb..",
                        "java.sql..",
                        "javax.sql..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void phaseTwelveShouldNotExposeExcludedSimulationOrRuntimeLaboratoryApis() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(
                        "com.ailab.chemistry.domain.equipment",
                        "com.ailab.chemistry.domain.container",
                        "com.ailab.chemistry.domain.labenvironment",
                        "com.ailab.chemistry.domain.laboratory");

        ArchRule rule = noClasses()
                .should()
                .haveSimpleNameContaining("Event")
                .orShould()
                .haveSimpleNameContaining("SimulationState")
                .orShould()
                .haveSimpleNameContaining("SimulationEngine")
                .orShould()
                .haveSimpleNameContaining("IoT")
                .orShould()
                .haveSimpleNameContaining("Inventory")
                .orShould()
                .haveSimpleNameContaining("RuntimeSafety")
                .orShould()
                .haveSimpleNameContaining("Controller");

        rule.check(importedClasses);
    }

    @Test
    void phaseThirteenDomainsShouldRemainFrameworkIndependent() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(
                        "com.ailab.chemistry.domain.laboratoryprocess",
                        "com.ailab.chemistry.domain.laboratoryevent",
                        "com.ailab.chemistry.domain.simulationstate");

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "org.flywaydb..",
                        "java.sql..",
                        "javax.sql..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void phaseThirteenShouldNotExposeSimulationEngineOrRuntimeSafetyApis() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(
                        "com.ailab.chemistry.domain.laboratoryprocess",
                        "com.ailab.chemistry.domain.laboratoryevent",
                        "com.ailab.chemistry.domain.simulationstate");

        ArchRule rule = noClasses()
                .should()
                .haveSimpleNameContaining("SimulationEngine")
                .orShould()
                .haveSimpleNameContaining("RuntimeSafety")
                .orShould()
                .haveSimpleNameContaining("EmergencyShutdown")
                .orShould()
                .haveSimpleNameContaining("SensorIngestion")
                .orShould()
                .haveSimpleNameContaining("EquipmentControl")
                .orShould()
                .haveSimpleNameContaining("ReactionExecution")
                .orShould()
                .haveSimpleNameContaining("EquilibriumProgression")
                .orShould()
                .haveSimpleNameContaining("Controller");

        rule.check(importedClasses);
    }

    @Test
    void phaseFourteenSimulationEngineDomainShouldRemainFrameworkIndependent() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("com.ailab.chemistry.domain.simulationengine");
        assertThat(importedClasses).isNotEmpty();

        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..",
                        "org.flywaydb..",
                        "java.sql..",
                        "javax.sql..",
                        "com.ailab.chemistry.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    void phaseFourteenShouldNotExposeRuntimeSafetyIoTSchedulersRestOrDynamicModelLoading() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("com.ailab.chemistry");

        ArchRule rule = noClasses()
                .should()
                .haveSimpleNameContaining("RuntimeSafety")
                .orShould()
                .haveSimpleNameContaining("EmergencyShutdown")
                .orShould()
                .haveSimpleNameContaining("SensorIngestion")
                .orShould()
                .haveSimpleNameContaining("EquipmentControl")
                .orShould()
                .haveSimpleNameContaining("IoT")
                .orShould()
                .haveSimpleNameContaining("Scheduler")
                .orShould()
                .haveSimpleNameContaining("WebSocket")
                .orShould()
                .haveSimpleNameContaining("Controller")
                .orShould()
                .haveSimpleNameContaining("DynamicModelLoader")
                .orShould()
                .haveSimpleNameContaining("ReactionDiscovery");

        rule.check(importedClasses);
    }

    @Test
    void phaseFourteenShouldHaveNoProductionInMemorySimulationFallback() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("com.ailab.chemistry.infrastructure.persistence.simulation");

        ArchRule rule = noClasses()
                .should()
                .haveSimpleNameContaining("InMemorySimulation")
                .orShould()
                .haveSimpleNameContaining("InMemoryCalculationAudit");

        rule.check(importedClasses);
    }
}
