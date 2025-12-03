package org.masouras.trace.control.repository.mongo;

import org.masouras.trace.domain.SpanInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpanInfoRepository extends MongoRepository<SpanInfo, String> {
    // Repository methods
}


