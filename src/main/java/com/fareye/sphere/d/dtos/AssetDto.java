package com.fareye.sphere.d.dtos;

import com.fareye.sphere.d.annotations.ValidAssetStatus;
import com.fareye.sphere.d.annotations.ValidFormattedId;
import com.fareye.sphere.d.entities.enums.AssetStatus;
import com.fareye.sphere.d.entities.enums.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @ValidAssetStatus
public class AssetDto {
    @NotBlank @ValidFormattedId(type = "ASSET")
    private String serialNumber;

    private Category category;

    @NotBlank
    private String name;

    private AssetStatus status;

    @ValidFormattedId(type = "USER")
    private String ownerId;
}