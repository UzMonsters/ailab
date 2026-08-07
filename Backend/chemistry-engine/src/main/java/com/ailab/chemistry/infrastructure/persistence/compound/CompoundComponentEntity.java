package com.ailab.chemistry.infrastructure.persistence.compound;

import com.ailab.chemistry.infrastructure.persistence.element.ElementEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "compound_components", schema = "chemistry")
public class CompoundComponentEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id", nullable = false)
    private CompoundEntity compound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "element_id", nullable = false)
    private ElementEntity element;

    @Column(name = "atomic_number", nullable = false)
    private int atomicNumber;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "atom_count", nullable = false)
    private BigDecimal atomCount;

    public CompoundComponentEntity() {}

    public CompoundComponentEntity(UUID id, CompoundEntity compound, ElementEntity element, int atomicNumber, String symbol, BigDecimal atomCount) {
        this.id = id;
        this.compound = compound;
        this.element = element;
        this.atomicNumber = atomicNumber;
        this.symbol = symbol;
        this.atomCount = atomCount;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public CompoundEntity getCompound() { return compound; }
    public void setCompound(CompoundEntity compound) { this.compound = compound; }

    public ElementEntity getElement() { return element; }
    public void setElement(ElementEntity element) { this.element = element; }

    public int getAtomicNumber() { return atomicNumber; }
    public void setAtomicNumber(int atomicNumber) { this.atomicNumber = atomicNumber; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getAtomCount() { return atomCount; }
    public void setAtomCount(BigDecimal atomCount) { this.atomCount = atomCount; }
}
