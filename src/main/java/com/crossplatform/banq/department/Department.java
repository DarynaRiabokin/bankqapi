package com.crossplatform.banq.department;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "departments")
public class Department {
    public Department() {
    }

    private ObjectId id;
    private String name;
}
