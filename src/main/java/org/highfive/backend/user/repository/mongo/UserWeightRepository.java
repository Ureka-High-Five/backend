package org.highfive.backend.user.repository.mongo;

import org.highfive.backend.user.entity.preference.MongoUserWeight;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserWeightRepository extends MongoRepository<MongoUserWeight, String> {

    @Query(value = "{ 'user_id': ?0, 'type': 'genre' }", sort = "{ 'weight': -1 }")
    List<MongoUserWeight> findTop2Genres(Long userId, Pageable pageable);
}
