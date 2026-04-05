package com.fareye.sphere.d.entities;

import com.fareye.sphere.d.entities.enums.Department;
import com.fareye.sphere.d.entities.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor @Getter @Setter @AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @SequenceGenerator(name = "user_id_seq",sequenceName = "user_id_seq",allocationSize = 1)
    @GeneratedValue(generator = "user_id_seq", strategy = GenerationType.SEQUENCE)
    private Long userId;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(min = 2, max = 35)
    private String fullName;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Department department;

    @OneToMany(mappedBy = "requestedFor")
    private List<Request> requests;

    @OneToMany(mappedBy = "owner")
    private List<Asset> assets;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;
}