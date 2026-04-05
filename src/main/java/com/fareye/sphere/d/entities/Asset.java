package com.fareye.sphere.d.entities;

import com.fareye.sphere.d.annotations.ValidAssetStatus;
import com.fareye.sphere.d.entities.enums.AssetStatus;
import com.fareye.sphere.d.entities.enums.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor @Getter @Setter @AllArgsConstructor
@Entity
@Table(name = "assets")
@ValidAssetStatus
@SQLRestriction("is_active = true")
public class Asset {
    @Id
    @SequenceGenerator(name = "serial_seq",sequenceName = "serial_seq",allocationSize = 1)
    @GeneratedValue(generator = "serial_seq", strategy = GenerationType.SEQUENCE)
    private Long serialNumber;

    @Enumerated(EnumType.STRING)
    private Category category;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private AssetStatus status=AssetStatus.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(nullable = false)
    private Boolean isActive = true;
}