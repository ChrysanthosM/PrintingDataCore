package org.masouras.trace.control.repository;

import org.masouras.trace.domain.SpanInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpanInfoMongoRepository extends MongoRepository<SpanInfo, String> {
    // Repository methods
}


