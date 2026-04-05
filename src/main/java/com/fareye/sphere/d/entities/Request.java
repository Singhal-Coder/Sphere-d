package com.fareye.sphere.d.entities;

import com.fareye.sphere.d.annotations.RequestRestriction;
import com.fareye.sphere.d.entities.enums.Category;
import com.fareye.sphere.d.entities.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor @Getter @Setter @AllArgsConstructor
@Entity
@Table(name = "requests")
@RequestRestriction
public class Request {
    @Id
    @SequenceGenerator(name = "request_id_seq",sequenceName = "request_id_seq",allocationSize = 1)
    @GeneratedValue(generator = "request_id_seq", strategy = GenerationType.SEQUENCE)
    private Long requestId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status=RequestStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category requestedForCategory;

    @OneToOne
    @JoinColumn(name = "assigned_asset")
    private Asset assignedAsset;

    @ManyToOne
    @JoinColumn(name = "requested_for",nullable = false)
    private User requestedFor;

    @ManyToOne
    @JoinColumn(name = "last_modifier", nullable = false)
    private User lastModifier;

    @OneToMany(mappedBy = "parentRequest")
    @OrderBy("time DESC")
    private List<RequestLog> requestLogs;
}
