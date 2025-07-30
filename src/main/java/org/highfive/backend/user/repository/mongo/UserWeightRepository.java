package org.highfive.backend.user.repository.mongo;

import org.highfive.backend.user.entity.preference.MongoUserWeight;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserWeightRepository extends MongoRepository<MongoUserWeight, String> {
}
