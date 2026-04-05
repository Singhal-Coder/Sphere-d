package com.fareye.sphere.d.validators;

import com.fareye.sphere.d.annotations.ValidAssetStatus;
import com.fareye.sphere.d.dtos.AssetDto;
import com.fareye.sphere.d.entities.enums.AssetStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AssetDtoAssignmentValidator implements ConstraintValidator<ValidAssetStatus, AssetDto> {
    @Override
    public boolean isValid(AssetDto value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.getStatus() == AssetStatus.AVAILABLE) {
            return value.getOwnerId() == null;
        } else if (value.getStatus() == AssetStatus.ASSIGNED) {
            return value.getOwnerId() != null;
        }
        return true;
    }
}