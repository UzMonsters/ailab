package com.ailab.chemistry.domain.element.property;

import java.util.Objects;

public final class IonicRadiusContext {
    private final int ionicCharge;
    private final Integer coordinationNumber;
    private final ElectronSpinState spinState;

    public IonicRadiusContext(int ionicCharge, Integer coordinationNumber, ElectronSpinState spinState) {
        if (ionicCharge == 0) {
            throw new ElementPropertyException(
                    ElementPropertyErrorCode.INVALID_IONIC_RADIUS_CONTEXT,
                    "Ionic radius requires a non-zero ionic charge"
            );
        }
        this.ionicCharge = ionicCharge;
        this.coordinationNumber = coordinationNumber;
        this.spinState = spinState != null ? spinState : ElectronSpinState.NOT_APPLICABLE;
    }

    public int getIonicCharge() { return ionicCharge; }
    public Integer getCoordinationNumber() { return coordinationNumber; }
    public ElectronSpinState getSpinState() { return spinState; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IonicRadiusContext that = (IonicRadiusContext) o;
        return ionicCharge == that.ionicCharge &&
                Objects.equals(coordinationNumber, that.coordinationNumber) &&
                spinState == that.spinState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ionicCharge, coordinationNumber, spinState);
    }
}
