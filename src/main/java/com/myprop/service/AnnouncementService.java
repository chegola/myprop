package com.myprop.service;

import com.myprop.domain.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Announcement.
 */
public interface AnnouncementService {

    /**
     * Save a announcement.
     * 
     * @param announcement the entity to save
     * @return the persisted entity
     */
    Announcement save(Announcement announcement);

    /**
     *  Get all the announcements.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Announcement> findAll(Pageable pageable);

    /**
     *  Get the "id" announcement.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Announcement findOne(Long id);

    /**
     *  Delete the "id" announcement.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);
}
