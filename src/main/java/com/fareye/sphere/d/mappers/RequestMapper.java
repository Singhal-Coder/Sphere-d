package com.fareye.sphere.d.mappers;

import com.fareye.sphere.d.dtos.RequestDto;
import com.fareye.sphere.d.dtos.RequestLogDto;
import com.fareye.sphere.d.entities.Asset;
import com.fareye.sphere.d.entities.Request;
import com.fareye.sphere.d.entities.RequestLog;
import com.fareye.sphere.d.entities.User;
import com.fareye.sphere.d.repositories.AssetRepository;
import com.fareye.sphere.d.repositories.UserRepository;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.Setter;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
@Setter(onMethod_ = {@Autowired})
public abstract class RequestMapper {


    protected IdUtils idUtils;

    protected AssetRepository assetRepository;

    protected UserRepository userRepository;

    protected RequestLogMapper requestLogMapper;

    @Mapping(target = "requestId", source = "requestId", qualifiedByName = "formatReqId")
    @Mapping(target = "assignedAssetSerialNumber", source = "assignedAsset.serialNumber", qualifiedByName = "formatAssetId")
    @Mapping(target = "requestedForId", source = "requestedFor.userId", qualifiedByName = "formatUserId")
    @Mapping(target = "lastModifierRole", source = "lastModifier.role")
    @Mapping(target = "logs", source = "requestLogs", qualifiedByName = "mapRequestLogToObject")
    public abstract RequestDto toDto(Request entity);

    @Mapping(target = "requestId", source = "requestId", qualifiedByName = "parseReqId")
    @Mapping(target = "assignedAsset", source = "assignedAssetSerialNumber")
    @Mapping(target = "requestedFor", source = "requestedForId")
    @Mapping(target = "lastModifier", ignore = true)
    @Mapping(target = "requestLogs", ignore = true)
    public abstract Request toEntity(RequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "requestedFor", ignore = true)
    public abstract void updateRequestFromDto(RequestDto dto, @MappingTarget Request entity);

    @Named("mapRequestLogToObject")
    public List<RequestLogDto> mapRequestLogs(List<RequestLog> logs){
        if (logs == null) return null;

        return logs.stream()
                .map(requestLogMapper::toDto)
                .sorted() // uses compareTo()
                .toList();
    }

    @Named("formatReqId")
    public String formatReqId(Long id) { return idUtils.formatRequestId(id).orElse(null); }
    @Named("parseReqId")
    public Long parseReqId(String id) { return idUtils.parseRequestId(id).orElse(null); }

    @Named("formatAssetId")
    public String formatAssetId(Long id) { return idUtils.formatSerialNumber(id).orElse(null); }
    @Named("formatUserId")
    public String formatUserId(Long id) { return idUtils.formatUserId(id).orElse(null); }

    public Asset idToAsset(String id) { return idUtils.parseSerialNumber(id).map(assetRepository::getReferenceById).orElse(null); }
    public User idToUser(String id) { return idUtils.parseUserId(id).map(userRepository::getReferenceById).orElse(null); }
}