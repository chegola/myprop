package com.myprop.repository;

import com.myprop.domain.Unit;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Unit entity.
 */
@SuppressWarnings("unused")
public interface UnitRepository extends JpaRepository<Unit,Long> {

}
