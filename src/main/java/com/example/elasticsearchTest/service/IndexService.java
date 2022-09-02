package com.example.elasticsearchTest.service;

import com.example.elasticsearchTest.helper.Indices;
import com.example.elasticsearchTest.helper.Util;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;


@Service
public class IndexService {
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);
    private static final List<String> INDICES = List.of(Indices.VEHICLE_INDEX);
    private  final RestHighLevelClient client;


    @Autowired
    public IndexService(RestHighLevelClient client) {
        this.client = client;
    }

    @PostConstruct
    public void tryToCrateIndices() {
        recreateIndices(false);
    }

    public void recreateIndices(boolean deleteExisting) {
        final String settings = Util.loadAsString("static/es-settings.json");

        if (settings == null) {
            LOG.error("Failed to load index settings");
            return;
        }
        for (String indexName : INDICES) {
            try {
                boolean indexExists = client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
                if (indexExists) {
                    if (!deleteExisting) {
                        continue;
                    }
                    client.indices().delete(new DeleteIndexRequest(indexName), RequestOptions.DEFAULT);
                }

                final CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
                createIndexRequest.settings(settings, XContentType.JSON);

                final String mappings = loadMapping(indexName);
                if (mappings != null) {
                    createIndexRequest.mapping(mappings, XContentType.JSON);
                }

                client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private String loadMapping(String indexName) {
        String mappings = Util.loadAsString("static/mappings/" + indexName + ".json");
        if (mappings ==null) {
            LOG.error("Failed to load mappings for index with name '{}'", indexName);
            return null;
        }
        return mappings;
    }
}
