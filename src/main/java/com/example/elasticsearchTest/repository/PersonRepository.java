package com.example.elasticsearchTest.repository;

import com.example.elasticsearchTest.document.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends ElasticsearchRepository<Person, String> {
}
