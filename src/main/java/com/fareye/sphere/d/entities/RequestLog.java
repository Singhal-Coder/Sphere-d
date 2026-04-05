package com.fareye.sphere.d.entities;

import com.fareye.sphere.d.entities.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor @Getter @Setter @AllArgsConstructor @Builder
@Entity
@Table(name = "request_logs")
public class RequestLog {
    @Id
    @SequenceGenerator(name = "request_log_id_seq",sequenceName = "request_log_id_seq",allocationSize = 1)
    @GeneratedValue(generator = "request_log_id_seq", strategy = GenerationType.SEQUENCE)
    private Long requestLogId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @CreationTimestamp
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name="modifier")
    private User modifier;

    @ManyToOne
    @JoinColumn(name="parent_request", nullable = false)
    private Request parentRequest;
}