package com.invertimostuyyo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Analysis.
 */
@Entity
@Table(name = "analysis")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Analysis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "analysis")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "analysis" }, allowSetters = true)
    private Set<Indicator> indicators = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "analyses" }, allowSetters = true)
    private Stock stock;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Analysis id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Analysis date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return this.description;
    }

    public Analysis description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Indicator> getIndicators() {
        return this.indicators;
    }

    public void setIndicators(Set<Indicator> indicators) {
        if (this.indicators != null) {
            this.indicators.forEach(i -> i.setAnalysis(null));
        }
        if (indicators != null) {
            indicators.forEach(i -> i.setAnalysis(this));
        }
        this.indicators = indicators;
    }

    public Analysis indicators(Set<Indicator> indicators) {
        this.setIndicators(indicators);
        return this;
    }

    public Analysis addIndicator(Indicator indicator) {
        this.indicators.add(indicator);
        indicator.setAnalysis(this);
        return this;
    }

    public Analysis removeIndicator(Indicator indicator) {
        this.indicators.remove(indicator);
        indicator.setAnalysis(null);
        return this;
    }

    public Stock getStock() {
        return this.stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Analysis stock(Stock stock) {
        this.setStock(stock);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Analysis)) {
            return false;
        }
        return id != null && id.equals(((Analysis) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Analysis{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
