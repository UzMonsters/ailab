package com.ailab.learning.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "learning_tasks")
public class LearningTaskEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "code", length = 64, nullable = false, unique = true)
    private String code;

    @Column(name = "task_type", length = 50, nullable = false)
    private String taskType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "validation_rule", columnDefinition = "jsonb", nullable = false)
    private String validationRuleJson = "{}";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "guide_template", columnDefinition = "jsonb", nullable = false)
    private String guideTemplateJson = "{}";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "translations", columnDefinition = "jsonb", nullable = false)
    private String translationsJson = "{}";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public LearningTaskEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getValidationRuleJson() { return validationRuleJson; }
    public void setValidationRuleJson(String validationRuleJson) { this.validationRuleJson = validationRuleJson; }

    public String getGuideTemplateJson() { return guideTemplateJson; }
    public void setGuideTemplateJson(String guideTemplateJson) { this.guideTemplateJson = guideTemplateJson; }

    public String getTranslationsJson() { return translationsJson; }
    public void setTranslationsJson(String translationsJson) { this.translationsJson = translationsJson; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
