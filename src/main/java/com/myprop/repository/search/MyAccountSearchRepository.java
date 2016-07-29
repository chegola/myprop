package com.myprop.repository.search;

import com.myprop.domain.MyAccount;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the MyAccount entity.
 */
public interface MyAccountSearchRepository extends ElasticsearchRepository<MyAccount, Long> {
}
