package com.fareye.sphere.d.dtos;

import com.fareye.sphere.d.entities.enums.RequestStatus;
import com.fareye.sphere.d.entities.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor
public class RequestLogDto implements Comparable<RequestLogDto>{
    private RequestStatus status;
    private LocalDateTime time;
    private Role modifierRole;

    @Override
    public int compareTo(RequestLogDto requestLogDto) {
        return requestLogDto.getTime().compareTo(this.getTime());
    }
}