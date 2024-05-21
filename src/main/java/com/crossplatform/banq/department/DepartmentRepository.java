package com.crossplatform.banq.department;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, UUID> {
    Department findById(ObjectId id);
}
