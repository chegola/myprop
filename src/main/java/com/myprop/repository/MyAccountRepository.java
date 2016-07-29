package com.myprop.repository;

import com.myprop.domain.MyAccount;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the MyAccount entity.
 */
@SuppressWarnings("unused")
public interface MyAccountRepository extends JpaRepository<MyAccount,Long> {

}
