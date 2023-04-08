package com.invertimostuyyo.stockanalysis.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Stock.
 */
@Table("stock")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("sector")
    private String sector;

    @Column("fundation")
    private LocalDate fundation;

    @Column("description")
    private String description;

    @Column("icnome")
    private Double icnome;

    @Column("expenses")
    private Double expenses;

    @Column("capitalization")
    private Double capitalization;

    @Column("employees")
    private Integer employees;

    @Transient
    @JsonIgnoreProperties(value = { "indicators", "stock" }, allowSetters = true)
    private Set<Analysis> analyses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Stock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Stock name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return this.sector;
    }

    public Stock sector(String sector) {
        this.setSector(sector);
        return this;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public LocalDate getFundation() {
        return this.fundation;
    }

    public Stock fundation(LocalDate fundation) {
        this.setFundation(fundation);
        return this;
    }

    public void setFundation(LocalDate fundation) {
        this.fundation = fundation;
    }

    public String getDescription() {
        return this.description;
    }

    public Stock description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getIcnome() {
        return this.icnome;
    }

    public Stock icnome(Double icnome) {
        this.setIcnome(icnome);
        return this;
    }

    public void setIcnome(Double icnome) {
        this.icnome = icnome;
    }

    public Double getExpenses() {
        return this.expenses;
    }

    public Stock expenses(Double expenses) {
        this.setExpenses(expenses);
        return this;
    }

    public void setExpenses(Double expenses) {
        this.expenses = expenses;
    }

    public Double getCapitalization() {
        return this.capitalization;
    }

    public Stock capitalization(Double capitalization) {
        this.setCapitalization(capitalization);
        return this;
    }

    public void setCapitalization(Double capitalization) {
        this.capitalization = capitalization;
    }

    public Integer getEmployees() {
        return this.employees;
    }

    public Stock employees(Integer employees) {
        this.setEmployees(employees);
        return this;
    }

    public void setEmployees(Integer employees) {
        this.employees = employees;
    }

    public Set<Analysis> getAnalyses() {
        return this.analyses;
    }

    public void setAnalyses(Set<Analysis> analyses) {
        if (this.analyses != null) {
            this.analyses.forEach(i -> i.setStock(null));
        }
        if (analyses != null) {
            analyses.forEach(i -> i.setStock(this));
        }
        this.analyses = analyses;
    }

    public Stock analyses(Set<Analysis> analyses) {
        this.setAnalyses(analyses);
        return this;
    }

    public Stock addAnalysis(Analysis analysis) {
        this.analyses.add(analysis);
        analysis.setStock(this);
        return this;
    }

    public Stock removeAnalysis(Analysis analysis) {
        this.analyses.remove(analysis);
        analysis.setStock(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stock)) {
            return false;
        }
        return id != null && id.equals(((Stock) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Stock{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", sector='" + getSector() + "'" +
            ", fundation='" + getFundation() + "'" +
            ", description='" + getDescription() + "'" +
            ", icnome=" + getIcnome() +
            ", expenses=" + getExpenses() +
            ", capitalization=" + getCapitalization() +
            ", employees=" + getEmployees() +
            "}";
    }
}
