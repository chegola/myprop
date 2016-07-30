package com.myprop.repository;

import com.myprop.domain.Announcement;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Announcement entity.
 */
@SuppressWarnings("unused")
public interface AnnouncementRepository extends JpaRepository<Announcement,Long> {

}
