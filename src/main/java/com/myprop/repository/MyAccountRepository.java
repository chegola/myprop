package com.myprop.repository;

import com.myprop.domain.MyAccount;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the MyAccount entity.
 */
@SuppressWarnings("unused")
public interface MyAccountRepository extends JpaRepository<MyAccount,Long> {

    @Query("select distinct myAccount from MyAccount myAccount left join fetch myAccount.units")
    List<MyAccount> findAllWithEagerRelationships();

    @Query("select myAccount from MyAccount myAccount left join fetch myAccount.units where myAccount.id =:id")
    MyAccount findOneWithEagerRelationships(@Param("id") Long id);

}
