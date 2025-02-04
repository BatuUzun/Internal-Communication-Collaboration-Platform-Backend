package com.chat_search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.chat_search.repository")
public class ElasticsearchConfig {

	@Bean
	public ElasticsearchClient elasticsearchClient() {
	    RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
	    ElasticsearchTransport transport = new RestClientTransport(
	        restClient, new co.elastic.clients.json.jackson.JacksonJsonpMapper()
	    );
	    return new ElasticsearchClient(transport);
	}

}
