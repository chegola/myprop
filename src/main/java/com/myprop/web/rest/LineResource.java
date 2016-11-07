package com.myprop.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.myprop.domain.Line;
import com.myprop.repository.LineRepository;
import com.myprop.web.rest.util.HeaderUtil;
import com.myprop.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Line.
 */
@RestController
@RequestMapping("/api")
public class LineResource {

    private final Logger log = LoggerFactory.getLogger(LineResource.class);
        
    @Inject
    private LineRepository lineRepository;
    
    /**
     * POST  /lines : Create a new line.
     *
     * @param line the line to create
     * @return the ResponseEntity with status 201 (Created) and with body the new line, or with status 400 (Bad Request) if the line has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/lines",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Line> createLine(@RequestBody Line line) throws URISyntaxException {
        log.debug("REST request to save Line : {}", line);
        if (line.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("line", "idexists", "A new line cannot already have an ID")).body(null);
        }
        Line result = lineRepository.save(line);
        return ResponseEntity.created(new URI("/api/lines/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("line", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /lines : Updates an existing line.
     *
     * @param line the line to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated line,
     * or with status 400 (Bad Request) if the line is not valid,
     * or with status 500 (Internal Server Error) if the line couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/lines",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Line> updateLine(@RequestBody Line line) throws URISyntaxException {
        log.debug("REST request to update Line : {}", line);
        if (line.getId() == null) {
            return createLine(line);
        }
        Line result = lineRepository.save(line);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("line", line.getId().toString()))
            .body(result);
    }

    /**
     * GET  /lines : get all the lines.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of lines in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/lines",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Line>> getAllLines(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Lines");
        Page<Line> page = lineRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/lines");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /lines/:id : get the "id" line.
     *
     * @param id the id of the line to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the line, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/lines/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Line> getLine(@PathVariable Long id) {
        log.debug("REST request to get Line : {}", id);
        Line line = lineRepository.findOne(id);
        return Optional.ofNullable(line)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /lines/:id : delete the "id" line.
     *
     * @param id the id of the line to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/lines/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        log.debug("REST request to delete Line : {}", id);
        lineRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("line", id.toString())).build();
    }

}
