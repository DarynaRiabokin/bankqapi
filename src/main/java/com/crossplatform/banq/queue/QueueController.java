package com.crossplatform.banq.queue;

import com.crossplatform.banq.auth.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class QueueController {
    private final QueueService queueService;
    private final AuthService authService;

    @Autowired
    public QueueController(QueueService queueService, AuthService authService) {
        this.queueService = queueService;
        this.authService = authService;
    }

    @GetMapping(path="/queues")
    @ResponseBody
    public String getQueues(HttpServletResponse response) {
        return queueService.getAll(response);
    }

    @PostMapping(path="/queues")
    @ResponseBody
    public String addQueue(@RequestBody QueueRequest queue, HttpServletResponse response) throws JsonProcessingException {
        return queueService.create(queue, response);
    }

    @DeleteMapping(path="/queues/{id}")
    public @ResponseBody String deleteQueue(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) {
        String errorJson = authService.checkAccess(request, response);

        if (errorJson.isEmpty()) {
            return queueService.delete(id, request, response);
        }

        return errorJson;
    }
}
