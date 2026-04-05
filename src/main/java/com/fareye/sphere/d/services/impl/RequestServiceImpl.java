package com.fareye.sphere.d.services.impl;

import com.fareye.sphere.d.dtos.RequestDto;
import com.fareye.sphere.d.entities.Asset;
import com.fareye.sphere.d.entities.Request;
import com.fareye.sphere.d.entities.RequestLog;
import com.fareye.sphere.d.entities.enums.AssetStatus;
import com.fareye.sphere.d.entities.enums.RequestStatus;
import com.fareye.sphere.d.entities.enums.Role;
import com.fareye.sphere.d.exceptions.BusinessPolicyException;
import com.fareye.sphere.d.exceptions.InvalidIdException;
import com.fareye.sphere.d.exceptions.InvalidWorkflowStateException;
import com.fareye.sphere.d.exceptions.ResourceInUseException;
import com.fareye.sphere.d.exceptions.ResourceNotFoundException;
import com.fareye.sphere.d.mappers.RequestMapper;
import com.fareye.sphere.d.repositories.AssetRepository;
import com.fareye.sphere.d.repositories.RequestLogRepository;
import com.fareye.sphere.d.repositories.RequestRepository;
import com.fareye.sphere.d.repositories.UserRepository;
import com.fareye.sphere.d.services.RequestService;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestLogRepository requestLogRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final RequestMapper requestMapper;
    private final IdUtils idUtils;

    private void updateLogs(Request request){
        RequestLog requestLog = RequestLog.builder()
                .status(request.getStatus())
                .time(LocalDateTime.now())
                .modifier(request.getLastModifier())
                .parentRequest(request)
                .build();

        requestLogRepository.save(requestLog);
    }

    @Override
    public RequestDto createRequest(RequestDto requestDto) {
        Request request = requestMapper.toEntity(requestDto);
        if (requestDto.getLastModifierRole()== Role.SYSTEM){
            request.setLastModifier(
                    userRepository.findByRole(Role.SYSTEM).getFirst()
            );
        }else if (requestDto.getLastModifierRole()== Role.EMPLOYEE){
            request.setLastModifier(
                    request.getRequestedFor()
            );
        }
        Request savedRequest = requestRepository.save(request);
        updateLogs(savedRequest);
        return requestMapper.toDto(savedRequest);
    }

    @Override
    public Page<RequestDto> getAllRequests(Pageable pageable, Role role, Long currentUserId) {
        if (role == Role.EMPLOYEE) {
            return requestRepository.findByRequestedFor_UserId(currentUserId, pageable).map(requestMapper::toDto);
        }
        return requestRepository.findAll(pageable).map(requestMapper::toDto);
    }

    @Override
    public RequestDto getRequestById(String requestId) {
        Long id = idUtils.parseRequestId(requestId)
                .orElseThrow(() -> new InvalidIdException(requestId));

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));

        return requestMapper.toDto(request);
    }

    @Override
    public RequestDto updateRequest(String requestId, RequestDto requestDto) {
        Long id = idUtils.parseRequestId(requestId)
                .orElseThrow(() -> new InvalidIdException(requestId));

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));

        RequestStatus currentStatus = request.getStatus();
        RequestStatus newStatus = requestDto.getStatus();

        if (newStatus != null && currentStatus != newStatus) {
                // 1. CHECK: State Machine Logic
                boolean isValidTransition = switch (currentStatus) {
                    case DRAFT -> newStatus == RequestStatus.PENDING;
                    case PENDING -> newStatus == RequestStatus.APPROVED || newStatus == RequestStatus.REJECTED;
                    case APPROVED -> newStatus == RequestStatus.ASSIGNED;
                    default -> false;
                };
        
                if (!isValidTransition) {
                    throw new InvalidWorkflowStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
        
                // 2. CHECK: Assignment Logic
                if (newStatus == RequestStatus.ASSIGNED) {
                    if (requestDto.getAssignedAssetSerialNumber() == null) {
                        throw new BusinessPolicyException("An asset must be provided to move request to ASSIGNED state.");
                    }
                    // Fetch asset and verify it is FREE
                    Asset asset = assetRepository.findById(idUtils.parseSerialNumber(requestDto.getAssignedAssetSerialNumber()).get())
                            .orElseThrow(() -> new ResourceNotFoundException("Asset", "serial", requestDto.getAssignedAssetSerialNumber()));
                    
                    if (asset.getStatus() != AssetStatus.AVAILABLE) {
                        throw new ResourceInUseException("This asset is currently not available for assignment.");
                    }
                    
                    // Assign owner to asset
                    asset.setOwner(request.getRequestedFor());
                    asset.setStatus(AssetStatus.ASSIGNED);
                    assetRepository.save(asset); // Update Asset table
                }
            }

        requestMapper.updateRequestFromDto(requestDto, request);

        Request updatedRequest = requestRepository.save(request);

        updateLogs(updatedRequest);

        return requestMapper.toDto(updatedRequest);
    }

    @Override
    public void deleteRequest(String requestId) {
        Long id = idUtils.parseRequestId(requestId)
                .orElseThrow(() -> new InvalidIdException(requestId));

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));

        requestRepository.delete(request);
    }
}
