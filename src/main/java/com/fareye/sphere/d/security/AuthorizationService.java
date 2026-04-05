package com.fareye.sphere.d.security;

import com.fareye.sphere.d.entities.Asset;
import com.fareye.sphere.d.entities.Booking;
import com.fareye.sphere.d.entities.Request;
import com.fareye.sphere.d.repositories.AssetRepository;
import com.fareye.sphere.d.repositories.BookingRepository;
import com.fareye.sphere.d.repositories.RequestRepository;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("authz")
@RequiredArgsConstructor
public class AuthorizationService {

    private final AssetRepository assetRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;
    private final IdUtils idUtils;

    public boolean isSelf(String formattedUserId) {
        return currentPrincipal()
                .map(p -> p.getFormattedUserId().equals(formattedUserId))
                .orElse(false);
    }

    public boolean isAssetOwner(String serialNumber) {
        Optional<UserPrincipal> principal = currentPrincipal();
        if (principal.isEmpty()) {
            return false;
        }
        Optional<Long> id = idUtils.parseSerialNumber(serialNumber);
        if (id.isEmpty()) {
            return false;
        }
        return assetRepository.findById(id.get())
                .map(Asset::getOwner)
                .filter(owner -> owner != null)
                .map(owner -> owner.getUserId().equals(principal.get().getUserId()))
                .orElse(false);
    }

    public boolean isBookingOwner(String bookingId) {
        Optional<UserPrincipal> principal = currentPrincipal();
        if (principal.isEmpty()) {
            return false;
        }
        Optional<Long> id = idUtils.parseBookingId(bookingId);
        if (id.isEmpty()) {
            return false;
        }
        return bookingRepository.findById(id.get())
                .map(Booking::getUser)
                .filter(user -> user != null)
                .map(user -> user.getUserId().equals(principal.get().getUserId()))
                .orElse(false);
    }

    public boolean isRequestOwner(String requestId) {
        Optional<UserPrincipal> principal = currentPrincipal();
        if (principal.isEmpty()) {
            return false;
        }
        Optional<Long> id = idUtils.parseRequestId(requestId);
        if (id.isEmpty()) {
            return false;
        }
        return requestRepository.findById(id.get())
                .map(Request::getRequestedFor)
                .filter(requestedFor -> requestedFor != null)
                .map(requestedFor -> requestedFor.getUserId().equals(principal.get().getUserId()))
                .orElse(false);
    }

    private Optional<UserPrincipal> currentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        if (auth.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }
}
