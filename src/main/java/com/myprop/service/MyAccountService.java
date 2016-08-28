package com.myprop.service;

/**
 * Created by che on 17/8/2559.
 */

import com.myprop.domain.Authority;
import com.myprop.domain.MyAccount;
import com.myprop.domain.User;
import com.myprop.repository.AuthorityRepository;
import com.myprop.repository.MyAccountRepository;
import com.myprop.repository.UserRepository;
import com.myprop.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class MyAccountService {
    private final Logger log = LoggerFactory.getLogger(MyAccountService.class);

    @Inject
    private MyAccountRepository myAccountRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private UserService userService;

    public MyAccount approveResidential(MyAccount myAccount) {
        log.debug("Approving residential for {}", myAccount.getName_surname());

        Authority authority_resident = authorityRepository.findOne(AuthoritiesConstants.RESIDENT);
        Optional<Set<Authority>> authorities = userRepository.findOneById(myAccount.getUser().getId()).map(user-> {return user.getAuthorities();});
        authorities.get().add(authority_resident);
        userRepository.findOneById(myAccount.getUser().getId())
            .map(user-> {
                user.setAuthorities(authorities.get());
                userRepository.save(user);
               return user;
            });
        myAccount.setApproved(true);
        myAccountRepository.save(myAccount);
        return myAccount;
    }

    public void unApproveResidential(Long id) {
        final MyAccount myAccount = myAccountRepository.findOne(id);
        log.debug("Un-approving residential for {}", myAccount.getName_surname());
        myAccount.setApproved(false);
        userService.resetAuthorityToUserRole(myAccount.getUser().getId());
        myAccountRepository.save(myAccount);
    }
}
            /*
        /*//*return myAccountRepository.findOne(myAccount.getId())
            .map(user -> {
                // activate given user for the registration key.
             *//**//*   user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);
                log.debug("Activated user: {}", user);
           *//**//*
            myAccountRepository.save(user)        ;


                      return user;
            });*/

    /*public Optional<MyAccount> approveResidential(MyAccount myAccount) {
        log.debug("Approving residential for {}", myAccount.getName_surname());
        return myAccountRepository.findOne(myAccount.getId()
            .map(myaccount -> {
                // activate given user for the registration key.
                *//*
                m
                user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);
                log.debug("Activated user: {}", user);*//*
                return myaccount;
            });
    }*/


