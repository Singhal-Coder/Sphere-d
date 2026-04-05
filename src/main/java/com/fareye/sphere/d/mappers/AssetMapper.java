package com.fareye.sphere.d.mappers;

import com.fareye.sphere.d.dtos.AssetDto;
import com.fareye.sphere.d.entities.Asset;
import com.fareye.sphere.d.entities.User;
import com.fareye.sphere.d.repositories.UserRepository;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.Setter;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Setter(onMethod_ = {@Autowired})
@Mapper(componentModel = "spring")
public abstract class AssetMapper {

    protected IdUtils idUtils;
    protected UserRepository userRepository;

    @Mapping(target = "serialNumber", source = "serialNumber", qualifiedByName = "formatSerialNumber")
    @Mapping(target = "ownerId", source = "owner.userId", qualifiedByName = "formatUserId")
    public abstract AssetDto toDto(Asset entity);

    @Mapping(target = "serialNumber", source = "serialNumber", qualifiedByName = "parseSerialNumber")
    @Mapping(target = "owner", source = "ownerId")
    public abstract Asset toEntity(AssetDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "serialNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    public abstract void updateAssetFromDto(AssetDto dto, @MappingTarget Asset entity);

    @Named("formatSerialNumber")
    public String formatSerialNumber(Long id) { return idUtils.formatSerialNumber(id).orElse(null); }

    @Named("parseSerialNumber")
    public Long parseSerialNumber(String id) { return idUtils.parseSerialNumber(id).orElse(null); }

    @Named("formatUserId")
    public String formatUserId(Long id) { return idUtils.formatUserId(id).orElse(null); }

    public User idToUser(String id) {return idUtils.parseUserId(id).map(userRepository::getReferenceById).orElse(null);}
}