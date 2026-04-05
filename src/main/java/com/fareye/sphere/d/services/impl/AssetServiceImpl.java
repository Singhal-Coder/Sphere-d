package com.fareye.sphere.d.services.impl;

import com.fareye.sphere.d.dtos.AssetDto;
import com.fareye.sphere.d.dtos.RequestDto;
import com.fareye.sphere.d.entities.Asset;
import com.fareye.sphere.d.entities.enums.AssetStatus;
import com.fareye.sphere.d.entities.enums.RequestStatus;
import com.fareye.sphere.d.entities.enums.Role;
import com.fareye.sphere.d.exceptions.InvalidIdException;
import com.fareye.sphere.d.exceptions.ResourceNotFoundException;
import com.fareye.sphere.d.mappers.AssetMapper;
import com.fareye.sphere.d.repositories.AssetRepository;
import com.fareye.sphere.d.services.AssetService;
import com.fareye.sphere.d.services.RequestService;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;
    private final IdUtils idUtils;
    private final RequestService requestService;

    private Asset getEntityFromSerial(String serial){
        Long id = idUtils.parseSerialNumber(serial)
                .orElseThrow(() -> new InvalidIdException(serial));
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", "serial", serial));
    }

    @Override
    public AssetDto createAsset(AssetDto assetDto) {
        Asset asset = assetMapper.toEntity(assetDto);
        asset.setIsActive(true);
        return assetMapper.toDto(assetRepository.save(asset));
    }

    @Override
    public AssetDto getAssetBySerial(String serial) {
        Asset asset=getEntityFromSerial(serial);
        return assetMapper.toDto(asset);
    }

    @Override
    public Page<AssetDto> getAllAssets(Pageable pageable) {
        return assetRepository.findAll(pageable)
                .map(assetMapper::toDto);
    }

    @Override
    public AssetDto updateAsset(String serial, AssetDto assetDto){
        Asset asset=getEntityFromSerial(serial);

        assetMapper.updateAssetFromDto(assetDto, asset);
        Asset updatedAsset = assetRepository.save(asset);
        return assetMapper.toDto(updatedAsset);
    }

    @Override
    public String changeStatus(String serial, AssetStatus status){
        Asset asset=getEntityFromSerial(serial);
        asset.setStatus(status);
        assetRepository.save(asset);
        if (status==AssetStatus.BROKEN){
            requestService.createRequest(
                    RequestDto.builder()
                            .status(RequestStatus.DRAFT)
                            .requestedForCategory(asset.getCategory())
                            .lastModifierRole(Role.SYSTEM)
                            .requestedForId(
                                    idUtils.formatUserId(
                                            asset.
                                                    getOwner().
                                                    getUserId()
                                    ).orElseThrow(
                                            () -> new InvalidIdException(serial)
                                    )
                            )
                            .build()
            );
            return "Status Updated and System Generated Draft Request";
        }
        return "Status Updated";
    }

    @Override
    public void deleteAsset(String serial) {
        Asset asset=getEntityFromSerial(serial);

        asset.setIsActive(false);
        assetRepository.save(asset);
    }
}