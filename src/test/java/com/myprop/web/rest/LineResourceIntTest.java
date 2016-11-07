package com.myprop.web.rest;

import com.myprop.MypropApp;
import com.myprop.domain.Line;
import com.myprop.repository.LineRepository;

import org.junit.Before;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the LineResource REST controller.
 *
 * @see LineResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MypropApp.class)
@WebAppConfiguration
@IntegrationTest
public class LineResourceIntTest {

    private static final String DEFAULT_SOURCE_ID = "AAAAA";
    private static final String UPDATED_SOURCE_ID = "BBBBB";
    private static final String DEFAULT_SOURCE_TYPE = "AAAAA";
    private static final String UPDATED_SOURCE_TYPE = "BBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final LocalDate DEFAULT_TIMESTAMP = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TIMESTAMP = LocalDate.now(ZoneId.systemDefault());
    private static final String DEFAULT_DISPLAY_NAME = "AAAAA";
    private static final String UPDATED_DISPLAY_NAME = "BBBBB";

    @Inject
    private LineRepository lineRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restLineMockMvc;

    private Line line;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LineResource lineResource = new LineResource();
        ReflectionTestUtils.setField(lineResource, "lineRepository", lineRepository);
        this.restLineMockMvc = MockMvcBuilders.standaloneSetup(lineResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        line = new Line();
        line.setSourceId(DEFAULT_SOURCE_ID);
        line.setSourceType(DEFAULT_SOURCE_TYPE);
        line.setActive(DEFAULT_ACTIVE);
        line.setTimestamp(DEFAULT_TIMESTAMP);
        line.setDisplayName(DEFAULT_DISPLAY_NAME);
    }

    @Test
    @Transactional
    public void createLine() throws Exception {
        int databaseSizeBeforeCreate = lineRepository.findAll().size();

        // Create the Line

        restLineMockMvc.perform(post("/api/lines")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(line)))
                .andExpect(status().isCreated());

        // Validate the Line in the database
        List<Line> lines = lineRepository.findAll();
        assertThat(lines).hasSize(databaseSizeBeforeCreate + 1);
        Line testLine = lines.get(lines.size() - 1);
        assertThat(testLine.getSourceId()).isEqualTo(DEFAULT_SOURCE_ID);
        assertThat(testLine.getSourceType()).isEqualTo(DEFAULT_SOURCE_TYPE);
        assertThat(testLine.isActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testLine.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testLine.getDisplayName()).isEqualTo(DEFAULT_DISPLAY_NAME);
    }

    @Test
    @Transactional
    public void getAllLines() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        // Get all the lines
        restLineMockMvc.perform(get("/api/lines?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(line.getId().intValue())))
                .andExpect(jsonPath("$.[*].sourceId").value(hasItem(DEFAULT_SOURCE_ID.toString())))
                .andExpect(jsonPath("$.[*].sourceType").value(hasItem(DEFAULT_SOURCE_TYPE.toString())))
                .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
                .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
                .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME.toString())));
    }

    @Test
    @Transactional
    public void getLine() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        // Get the line
        restLineMockMvc.perform(get("/api/lines/{id}", line.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(line.getId().intValue()))
            .andExpect(jsonPath("$.sourceId").value(DEFAULT_SOURCE_ID.toString()))
            .andExpect(jsonPath("$.sourceType").value(DEFAULT_SOURCE_TYPE.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.displayName").value(DEFAULT_DISPLAY_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLine() throws Exception {
        // Get the line
        restLineMockMvc.perform(get("/api/lines/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLine() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);
        int databaseSizeBeforeUpdate = lineRepository.findAll().size();

        // Update the line
        Line updatedLine = new Line();
        updatedLine.setId(line.getId());
        updatedLine.setSourceId(UPDATED_SOURCE_ID);
        updatedLine.setSourceType(UPDATED_SOURCE_TYPE);
        updatedLine.setActive(UPDATED_ACTIVE);
        updatedLine.setTimestamp(UPDATED_TIMESTAMP);
        updatedLine.setDisplayName(UPDATED_DISPLAY_NAME);

        restLineMockMvc.perform(put("/api/lines")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedLine)))
                .andExpect(status().isOk());

        // Validate the Line in the database
        List<Line> lines = lineRepository.findAll();
        assertThat(lines).hasSize(databaseSizeBeforeUpdate);
        Line testLine = lines.get(lines.size() - 1);
        assertThat(testLine.getSourceId()).isEqualTo(UPDATED_SOURCE_ID);
        assertThat(testLine.getSourceType()).isEqualTo(UPDATED_SOURCE_TYPE);
        assertThat(testLine.isActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testLine.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testLine.getDisplayName()).isEqualTo(UPDATED_DISPLAY_NAME);
    }

    @Test
    @Transactional
    public void deleteLine() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);
        int databaseSizeBeforeDelete = lineRepository.findAll().size();

        // Get the line
        restLineMockMvc.perform(delete("/api/lines/{id}", line.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Line> lines = lineRepository.findAll();
        assertThat(lines).hasSize(databaseSizeBeforeDelete - 1);
    }
}
