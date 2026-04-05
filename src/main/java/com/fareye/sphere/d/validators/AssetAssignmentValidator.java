package com.fareye.sphere.d.validators;

import com.fareye.sphere.d.annotations.ValidAssetStatus;
import com.fareye.sphere.d.entities.Asset;
import com.fareye.sphere.d.entities.enums.AssetStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AssetAssignmentValidator implements ConstraintValidator<ValidAssetStatus,Asset> {

    @Override
    public boolean isValid(Asset value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.getStatus() == AssetStatus.AVAILABLE) {
            return value.getOwner() == null;
        } else if (value.getStatus() == AssetStatus.ASSIGNED) {
            return value.getOwner() != null;
        }
        return true;
    }
}