package com.fareye.sphere.d.controllers;

import com.fareye.sphere.d.advices.ApiResponse;
import com.fareye.sphere.d.dtos.AssetDto;
import com.fareye.sphere.d.entities.enums.AssetStatus;
import com.fareye.sphere.d.entities.enums.Role;
import com.fareye.sphere.d.services.AssetService;
import com.fareye.sphere.d.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<ApiResponse<AssetDto>> createAsset(@Valid @RequestBody AssetDto assetDto) {
        AssetDto createdAsset = assetService.createAsset(assetDto);
        ApiResponse<AssetDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "Asset created", createdAsset);
        response.add(linkTo(methodOn(AssetController.class).getAssetBySerial(createdAsset.getSerialNumber())).withSelfRel());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssetDto>> getAssetBySerial(@PathVariable String id) {
        AssetDto asset = assetService.getAssetBySerial(id);
        ApiResponse<AssetDto> response = new ApiResponse<>(HttpStatus.OK.value(), "Asset fetched", asset);

        Role role = securityUtils.getCurrentUserRole();
        String currentUserId = securityUtils.getCurrentUserId();

        response.add(linkTo(methodOn(AssetController.class).getAssetBySerial(id)).withSelfRel());

        if (role == Role.IT_SUPPORT_MEMBER || role == Role.SYSTEM) {
            response.add(linkTo(methodOn(AssetController.class).updateAsset(id, null)).withRel("update-asset"));
            response.add(linkTo(methodOn(AssetController.class).deleteAsset(id)).withRel("delete-asset"));
        }
        if (role == Role.EMPLOYEE && currentUserId.equals(asset.getOwnerId())) {
            response.add(linkTo(methodOn(AssetController.class).changeStatus(id, AssetStatus.BROKEN)).withRel("report-broken"));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AssetDto>>> getAllAssets(Pageable pageable) {
        Page<AssetDto> assets = assetService.getAllAssets(pageable);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Assets fetched", assets));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AssetDto>> updateAsset(@PathVariable String id, @Valid @RequestBody AssetDto assetDto) {
        AssetDto updatedAsset = assetService.updateAsset(id, assetDto);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Asset updated", updatedAsset));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> changeStatus(@PathVariable String id, @RequestParam AssetStatus status) {
        String msg = assetService.changeStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), msg, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAsset(@PathVariable String id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "Asset deleted", null));
    }
}