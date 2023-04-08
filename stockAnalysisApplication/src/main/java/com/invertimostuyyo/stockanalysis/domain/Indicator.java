package com.invertimostuyyo.stockanalysis.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Indicator.
 */
@Table("indicator")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Indicator implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Transient
    @JsonIgnoreProperties(value = { "indicators", "stock" }, allowSetters = true)
    private Analysis analysis;

    @Column("analysis_id")
    private Long analysisId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Indicator id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Indicator name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Indicator description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Analysis getAnalysis() {
        return this.analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
        this.analysisId = analysis != null ? analysis.getId() : null;
    }

    public Indicator analysis(Analysis analysis) {
        this.setAnalysis(analysis);
        return this;
    }

    public Long getAnalysisId() {
        return this.analysisId;
    }

    public void setAnalysisId(Long analysis) {
        this.analysisId = analysis;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Indicator)) {
            return false;
        }
        return id != null && id.equals(((Indicator) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Indicator{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
