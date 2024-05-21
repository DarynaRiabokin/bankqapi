package com.crossplatform.banq.window;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "windows")
public class Window {
    public Window() {
    }

    private ObjectId id;
    private String name;
    private List<String> departments;
}
