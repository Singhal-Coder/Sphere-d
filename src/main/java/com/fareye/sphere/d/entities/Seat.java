package com.fareye.sphere.d.entities;

import com.fareye.sphere.d.entities.enums.Department;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor @Getter @Setter @AllArgsConstructor
@Entity
@Table(
        name = "seats",
        uniqueConstraints = @UniqueConstraint(name = "unique_seat_in_department", columnNames = {"grid_x", "grid_y", "department"})
)
public class Seat {
    @Id
    @SequenceGenerator(name = "seat_id_seq",sequenceName = "seat_id_seq",allocationSize = 1)
    @GeneratedValue(generator = "seat_id_seq", strategy = GenerationType.SEQUENCE)
    private Long seatId;

    @Column(name = "grid_x", nullable = false)
    @Min(0)
    private int gridX;

    @Column(name = "grid_y", nullable = false)
    @Min(0)
    private int gridY;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Department department;

    @OneToMany(mappedBy = "seat")
    private List<Booking> bookings;
}
