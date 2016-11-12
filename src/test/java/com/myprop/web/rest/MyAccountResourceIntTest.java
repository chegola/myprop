package com.myprop.web.rest;

import com.myprop.MypropApp;
import com.myprop.domain.MyAccount;
import com.myprop.repository.MyAccountRepository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the MyAccountResource REST controller.
 *
 * @see MyAccountResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MypropApp.class)
@WebAppConfiguration
@IntegrationTest
public class MyAccountResourceIntTest {

    private static final String DEFAULT_NAME_SURNAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME_SURNAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_MOBILE = "AAAAA";
    private static final String UPDATED_MOBILE = "BBBBB";

    private static final Boolean DEFAULT_APPROVED = false;
    private static final Boolean UPDATED_APPROVED = true;

    @Inject
    private MyAccountRepository myAccountRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restMyAccountMockMvc;

    private MyAccount myAccount;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MyAccountResource myAccountResource = new MyAccountResource();
        ReflectionTestUtils.setField(myAccountResource, "myAccountRepository", myAccountRepository);
        this.restMyAccountMockMvc = MockMvcBuilders.standaloneSetup(myAccountResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        myAccount = new MyAccount();
        myAccount.setName_surname(DEFAULT_NAME_SURNAME);
        myAccount.setMobile(DEFAULT_MOBILE);
        myAccount.setApproved(DEFAULT_APPROVED);
    }

    @Test
    @Transactional
    public void createMyAccount() throws Exception {
        int databaseSizeBeforeCreate = myAccountRepository.findAll().size();

        // Create the MyAccount

        restMyAccountMockMvc.perform(post("/api/my-accounts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(myAccount)))
                .andExpect(status().isCreated());

        // Validate the MyAccount in the database
        List<MyAccount> myAccounts = myAccountRepository.findAll();
        assertThat(myAccounts).hasSize(databaseSizeBeforeCreate + 1);
        MyAccount testMyAccount = myAccounts.get(myAccounts.size() - 1);
        assertThat(testMyAccount.getName_surname()).isEqualTo(DEFAULT_NAME_SURNAME);
        assertThat(testMyAccount.getMobile()).isEqualTo(DEFAULT_MOBILE);
        assertThat(testMyAccount.isApproved()).isEqualTo(DEFAULT_APPROVED);
    }

    @Test
    @Transactional
    public void checkName_surnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = myAccountRepository.findAll().size();
        // set the field null
        myAccount.setName_surname(null);

        // Create the MyAccount, which fails.

        restMyAccountMockMvc.perform(post("/api/my-accounts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(myAccount)))
                .andExpect(status().isBadRequest());

        List<MyAccount> myAccounts = myAccountRepository.findAll();
        assertThat(myAccounts).hasSize(databaseSizeBeforeTest);
    }
    @Ignore
    @Test
    @Transactional
    public void getAllMyAccounts() throws Exception {
        // Initialize the database
        myAccountRepository.saveAndFlush(myAccount);

        // Get all the myAccounts
        restMyAccountMockMvc.perform(get("/api/my-accounts?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(myAccount.getId().intValue())))
                .andExpect(jsonPath("$.[*].name_surname").value(hasItem(DEFAULT_NAME_SURNAME.toString())))
                .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE.toString())))
                .andExpect(jsonPath("$.[*].approved").value(hasItem(DEFAULT_APPROVED.booleanValue())));
    }

    @Test
    @Transactional
    public void getMyAccount() throws Exception {
        // Initialize the database
        myAccountRepository.saveAndFlush(myAccount);

        // Get the myAccount
        restMyAccountMockMvc.perform(get("/api/my-accounts/{id}", myAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(myAccount.getId().intValue()))
            .andExpect(jsonPath("$.name_surname").value(DEFAULT_NAME_SURNAME.toString()))
            .andExpect(jsonPath("$.mobile").value(DEFAULT_MOBILE.toString()))
            .andExpect(jsonPath("$.approved").value(DEFAULT_APPROVED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingMyAccount() throws Exception {
        // Get the myAccount
        restMyAccountMockMvc.perform(get("/api/my-accounts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Ignore
    @Test
    @Transactional
    public void updateMyAccount() throws Exception {
        // Initialize the database
        myAccountRepository.saveAndFlush(myAccount);
        int databaseSizeBeforeUpdate = myAccountRepository.findAll().size();

        // Update the myAccount
        MyAccount updatedMyAccount = new MyAccount();
        updatedMyAccount.setId(myAccount.getId());
        updatedMyAccount.setName_surname(UPDATED_NAME_SURNAME);
        updatedMyAccount.setMobile(UPDATED_MOBILE);
        updatedMyAccount.setApproved(UPDATED_APPROVED);

        restMyAccountMockMvc.perform(put("/api/my-accounts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedMyAccount)))
                .andExpect(status().isOk());

        // Validate the MyAccount in the database
        List<MyAccount> myAccounts = myAccountRepository.findAll();
        assertThat(myAccounts).hasSize(databaseSizeBeforeUpdate);
        MyAccount testMyAccount = myAccounts.get(myAccounts.size() - 1);
        assertThat(testMyAccount.getName_surname()).isEqualTo(UPDATED_NAME_SURNAME);
        assertThat(testMyAccount.getMobile()).isEqualTo(UPDATED_MOBILE);
        assertThat(testMyAccount.isApproved()).isEqualTo(UPDATED_APPROVED);
    }
    @Ignore
    @Test
    @Transactional
    public void deleteMyAccount() throws Exception {
        // Initialize the database
        myAccountRepository.saveAndFlush(myAccount);
        int databaseSizeBeforeDelete = myAccountRepository.findAll().size();

        // Get the myAccount
        restMyAccountMockMvc.perform(delete("/api/my-accounts/{id}", myAccount.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<MyAccount> myAccounts = myAccountRepository.findAll();
        assertThat(myAccounts).hasSize(databaseSizeBeforeDelete - 1);
    }
}
