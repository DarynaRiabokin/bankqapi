package com.crossplatform.banq.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class QueueRequest {
    private String userName;
    private String departmentId;
}
