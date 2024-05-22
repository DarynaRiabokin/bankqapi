package com.crossplatform.banq.window;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WindowRepository extends MongoRepository<Window, UUID> {
    Window findFirstByDepartmentsContaining(String departmentId);
    List<Window> findAllByDepartmentsContaining(String departmentId);
}
