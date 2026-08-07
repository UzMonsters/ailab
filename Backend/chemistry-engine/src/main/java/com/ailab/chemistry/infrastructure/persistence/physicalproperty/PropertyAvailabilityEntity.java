package com.ailab.chemistry.infrastructure.persistence.physicalproperty;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "compound_property_availability", schema = "chemistry")
public class PropertyAvailabilityEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private CompoundPhysicalPropertyProfileEntity profile;

    @Column(name = "property_type", nullable = false)
    private String propertyType;

    @Column(name = "availability_status", nullable = false)
    private String availabilityStatus;

    public PropertyAvailabilityEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public CompoundPhysicalPropertyProfileEntity getProfile() { return profile; }
    public void setProfile(CompoundPhysicalPropertyProfileEntity profile) { this.profile = profile; }
    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
}
