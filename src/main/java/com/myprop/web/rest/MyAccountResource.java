package com.myprop.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.myprop.domain.MyAccount;
import com.myprop.repository.MyAccountRepository;
import com.myprop.service.MyAccountService;
import com.myprop.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing MyAccount.
 */
@RestController
@RequestMapping("/api")
public class MyAccountResource {

    private final Logger log = LoggerFactory.getLogger(MyAccountResource.class);

    @Inject
    private MyAccountRepository myAccountRepository;

    @Inject
    private MyAccountService myAccountService;


    /**
     * POST  /my-accounts : Create a new myAccount.
     *
     * @param myAccount the myAccount to create
     * @return the ResponseEntity with status 201 (Created) and with body the new myAccount, or with status 400 (Bad Request) if the myAccount has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/my-accounts",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MyAccount> createMyAccount(@Valid @RequestBody MyAccount myAccount) throws URISyntaxException {
        log.debug("REST request to save MyAccount : {}", myAccount);
        if (myAccount.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("myAccount", "idexists", "A new myAccount cannot already have an ID")).body(null);
        }

        myAccount.setApproved(false);
        MyAccount result = myAccountRepository.save(myAccount);
        return ResponseEntity.created(new URI("/api/my-accounts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("myAccount", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /my-accounts : Updates an existing myAccount.
     *
     * @param myAccount the myAccount to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated myAccount,
     * or with status 400 (Bad Request) if the myAccount is not valid,
     * or with status 500 (Internal Server Error) if the myAccount couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/my-accounts",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MyAccount> updateMyAccount(@Valid @RequestBody MyAccount myAccount) throws URISyntaxException {
        log.debug("REST request to update MyAccount : {}", myAccount);
        MyAccount result;

        if (myAccount.getId() == null) {
            return createMyAccount(myAccount);
        }

        if (myAccount.isApproved()) {
            result = myAccountService.approveResidential(myAccount);
        } else {
            result = myAccountRepository.save(myAccount);
        }

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("myAccount", myAccount.getId().toString()))
            .body(result);
    }


    /**
     * GET  /my-accounts : get all the myAccounts.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of myAccounts in body
     */
    @RequestMapping(value = "/my-accounts",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<MyAccount> getAllMyAccounts() {
        log.debug("REST request to get all MyAccounts");
        List<MyAccount> myAccounts = myAccountRepository.findAllWithEagerRelationships();
        return myAccounts;
    }

    /**
     * GET  /my-accounts/:id : get the "id" myAccount.
     *
     * @param id the id of the myAccount to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the myAccount, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/my-accounts/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MyAccount> getMyAccount(@PathVariable Long id) {
        log.debug("REST request to get MyAccount : {}", id);
        MyAccount myAccount = myAccountRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(myAccount)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /my-accounts/:id : delete the "id" myAccount.
     *
     * @param id the id of the myAccount to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/my-accounts/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteMyAccount(@PathVariable Long id) {
        log.debug("REST request to delete MyAccount : {}", id);
        myAccountRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("myAccount", id.toString())).build();
    }

}
