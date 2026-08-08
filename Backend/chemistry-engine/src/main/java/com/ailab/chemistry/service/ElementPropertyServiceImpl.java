package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ElementPropertyDetails;
import com.ailab.chemistry.api.ElementPropertyService;
import com.ailab.chemistry.domain.element.property.ElementPropertyErrorCode;
import com.ailab.chemistry.domain.element.property.ElementPropertyException;
import com.ailab.chemistry.domain.element.property.ElementPropertyProfile;
import com.ailab.chemistry.domain.element.property.ElementPropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ElementPropertyServiceImpl implements ElementPropertyService {
    private final ElementPropertyRepository propertyRepository;

    public ElementPropertyServiceImpl(ElementPropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public ElementPropertyDetails getByAtomicNumber(int atomicNumber) {
        if (atomicNumber < 1 || atomicNumber > 118) {
            throw new ElementPropertyException(
                    ElementPropertyErrorCode.PROPERTY_DATA_MISMATCH,
                    "Invalid atomic number: " + atomicNumber
            );
        }
        ElementPropertyProfile profile = propertyRepository.findByAtomicNumber(atomicNumber)
                .orElseThrow(() -> new ElementPropertyException(
                        ElementPropertyErrorCode.ELEMENT_PROPERTY_PROFILE_NOT_FOUND,
                        "Element property profile for atomic number " + atomicNumber + " not found"
                ));
        return new ElementPropertyDetails(profile);
    }

    @Override
    public ElementPropertyDetails getBySymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new ElementPropertyException(
                    ElementPropertyErrorCode.ELEMENT_PROPERTY_PROFILE_NOT_FOUND,
                    "Element symbol must not be empty"
            );
        }
        ElementPropertyProfile profile = propertyRepository.findBySymbol(symbol)
                .orElseThrow(() -> new ElementPropertyException(
                        ElementPropertyErrorCode.ELEMENT_PROPERTY_PROFILE_NOT_FOUND,
                        "Element property profile for symbol " + symbol + " not found"
                ));
        return new ElementPropertyDetails(profile);
    }
}
