package com.ailab.chemistry.infrastructure.persistence.hazard;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "hazard_availability", schema = "chemistry")
public class HazardAvailabilityEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private HazardProfileEntity profile;

    @Column(name = "classification_system", nullable = false)
    private String classificationSystem;

    @Column(name = "availability_status", nullable = false)
    private String availabilityStatus;

    public HazardAvailabilityEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public HazardProfileEntity getProfile() { return profile; }
    public void setProfile(HazardProfileEntity profile) { this.profile = profile; }
    public String getClassificationSystem() { return classificationSystem; }
    public void setClassificationSystem(String classificationSystem) { this.classificationSystem = classificationSystem; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
}
