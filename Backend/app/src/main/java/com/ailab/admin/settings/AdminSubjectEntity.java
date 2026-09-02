package com.ailab.admin.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_subjects")
public class AdminSubjectEntity {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean enabled;

    @Column(length = 50)
    private String accent;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected AdminSubjectEntity() {
    }

    public AdminSubjectEntity(String id, String name, boolean enabled, String accent, int sortOrder) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.accent = accent;
        this.sortOrder = sortOrder;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public String getAccent() { return accent; }
    public int getSortOrder() { return sortOrder; }

    public void update(Boolean enabled, String accent, Integer sortOrder) {
        if (enabled != null) this.enabled = enabled;
        if (accent != null) this.accent = accent;
        if (sortOrder != null) this.sortOrder = sortOrder;
    }
}
