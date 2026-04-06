package com.fareye.sphere.d.dtos;

import com.fareye.sphere.d.annotations.ValidFormattedId;
import com.fareye.sphere.d.entities.enums.Category;
import com.fareye.sphere.d.entities.enums.RequestStatus;
import com.fareye.sphere.d.entities.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor @Builder
public class RequestDto {
    @ValidFormattedId(type = "REQUEST")
    private String requestId;
    private RequestStatus status;
    @NotBlank
    private Category requestedForCategory;

    @ValidFormattedId(type = "ASSET")
    private String assignedAssetSerialNumber;

    private Role lastModifierRole;

    /** Writable on create/update so clients can set the subject user (e.g. employee creating own request). */
    @ValidFormattedId(type = "USER")
    private String requestedForId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<RequestLogDto> logs;
}