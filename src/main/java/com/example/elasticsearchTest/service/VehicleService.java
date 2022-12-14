package com.example.elasticsearchTest.service;

import com.example.elasticsearchTest.document.Vehicle;
import com.example.elasticsearchTest.helper.Indices;
import com.example.elasticsearchTest.search.SearchRequestDTO;
import com.example.elasticsearchTest.search.util.SearchUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class VehicleService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(VehicleService.class);
    private final RestHighLevelClient client;

    @Autowired
    public VehicleService(RestHighLevelClient client) {
        this.client = client;
    }

    public List<Vehicle> search(SearchRequestDTO dto) {
        SearchRequest request = SearchUtil.buildSearchRequest(Indices.VEHICLE_INDEX, dto);

        if (request == null) {
            LOG.error("Failed to build search request");
            return Collections.emptyList();
        }
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHit[] searchHits = response.getHits().getHits();
            List<Vehicle> vehicles = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                vehicles.add(MAPPER.readValue(hit.getSourceAsString(), Vehicle.class));
            }
            return vehicles;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public Boolean save(final Vehicle vehicle) {
        try {
            final String vehicleAsString = MAPPER.writeValueAsString(vehicle);

            final IndexRequest request = new IndexRequest(Indices.VEHICLE_INDEX);
            request.id(vehicle.getId());
            request.source(vehicleAsString, XContentType.JSON);

            final IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            return response != null && response.status().equals(RestStatus.OK);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }


    public Vehicle getById(final String vehicleId) {
        try {
            final GetResponse documentFields = client.get(new GetRequest(Indices.VEHICLE_INDEX, vehicleId), RequestOptions.DEFAULT);
            if (documentFields == null || documentFields.isSourceEmpty()) {
                return null;
            }

            return MAPPER.readValue(documentFields.getSourceAsString(), Vehicle.class);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
