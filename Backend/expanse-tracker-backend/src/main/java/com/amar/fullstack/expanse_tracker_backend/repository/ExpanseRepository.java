package com.amar.fullstack.expanse_tracker_backend.repository;

import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpanseRepository extends JpaRepository<Expanse, Long> {

}
