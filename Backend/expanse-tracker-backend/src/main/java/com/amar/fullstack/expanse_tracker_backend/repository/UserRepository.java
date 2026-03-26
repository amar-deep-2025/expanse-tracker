package com.amar.fullstack.expanse_tracker_backend.repository;

import com.amar.fullstack.expanse_tracker_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByUsername(String username);
}
