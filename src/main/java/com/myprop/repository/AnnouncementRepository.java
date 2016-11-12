package com.myprop.repository;

import com.myprop.domain.Announcement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Announcement entity.
 */
@SuppressWarnings("unused")
public interface AnnouncementRepository extends JpaRepository<Announcement,Long> {
    @Query("SELECT a from Announcement a ORDER BY a.startDate DESC")
    Page<Announcement> findLastThree(Pageable pageable);
}
