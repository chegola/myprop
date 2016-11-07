package com.myprop.repository;

import com.myprop.domain.Line;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Line entity.
 */
@SuppressWarnings("unused")
public interface LineRepository extends JpaRepository<Line,Long> {

}
