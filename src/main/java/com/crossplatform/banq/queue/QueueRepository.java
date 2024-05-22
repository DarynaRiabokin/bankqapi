package com.crossplatform.banq.queue;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QueueRepository extends MongoRepository<Queue, UUID> {
    Queue findById(ObjectId id);
    List<Queue> findAllByDepartmentId(String departmentId);
    int countByWindowId(String windowId);
}
