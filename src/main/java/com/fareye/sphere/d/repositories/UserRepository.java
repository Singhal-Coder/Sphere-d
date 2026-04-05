package com.fareye.sphere.d.repositories;

import com.fareye.sphere.d.entities.User;
import com.fareye.sphere.d.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(Role role);
}