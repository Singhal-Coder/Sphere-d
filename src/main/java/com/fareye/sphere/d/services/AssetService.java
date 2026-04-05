package com.fareye.sphere.d.services;

import com.fareye.sphere.d.dtos.AssetDto;
import com.fareye.sphere.d.entities.enums.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetService {
    AssetDto createAsset(AssetDto assetDto);
    AssetDto getAssetBySerial(String serial);
    Page<AssetDto> getAllAssets(Pageable pageable);
    AssetDto updateAsset(String serial, AssetDto assetDto);

    String changeStatus(String serial, AssetStatus status);

    void deleteAsset(String serial);
}