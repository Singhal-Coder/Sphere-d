package com.fareye.sphere.d.dtos;

import com.fareye.sphere.d.annotations.ValidFormattedId;
import com.fareye.sphere.d.annotations.ValidPassword;
import com.fareye.sphere.d.entities.enums.Department;
import com.fareye.sphere.d.entities.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor
public class UserDto {
    @ValidFormattedId(type = "USER")
    private String userId;

    @NotBlank @Email
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ValidPassword
    private String password;

    @NotBlank @Size(min = 2, max = 35)
    private String fullName;
    private Role role;
    private Department department;
    private List<@ValidFormattedId(type = "REQUEST") String> requestIds;
    private List<@ValidFormattedId(type = "ASSET") String> assetSerialNumbers;
    private List<@ValidFormattedId(type = "BOOKING") String> bookingIds;
}