package com.crossplatform.banq.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "queues")
public class Queue {
    private ObjectId id;
    private String queueId;
    private String userName;
    private String windowId;
    private String departmentId;

    public Queue(String departmentId, String userName, String windowId) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        this.departmentId = departmentId;
        this.userName = userName;
        this.windowId = windowId;
        this.queueId = timestamp.substring(timestamp.length() - 4);
    }

    public Queue() {}
}
