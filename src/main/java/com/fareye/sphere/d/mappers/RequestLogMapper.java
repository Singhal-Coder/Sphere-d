package com.fareye.sphere.d.mappers;

import com.fareye.sphere.d.dtos.RequestLogDto;
import com.fareye.sphere.d.entities.RequestLog;
import com.fareye.sphere.d.entities.User;
import com.fareye.sphere.d.entities.enums.Role;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Setter(onMethod_ = {@Autowired})
@Mapper(componentModel = "spring")
public abstract class RequestLogMapper {

    @Mapping(target = "modifierRole", source = "modifier.role", qualifiedByName = "getModifierRole")
    public abstract RequestLogDto toDto(RequestLog entity);

    @Named("getModifierRole")
    public Role getModifierRole(User modifier){
        return modifier.getRole();
    }
}