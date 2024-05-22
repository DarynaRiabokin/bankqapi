package com.crossplatform.banq.queue;

import com.crossplatform.banq.department.Department;
import com.crossplatform.banq.department.DepartmentRepository;
import com.crossplatform.banq.window.Window;
import com.crossplatform.banq.window.WindowRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueueService {
    private final QueueRepository queueRepository;
    private final DepartmentRepository departmentRepository;
    private final WindowRepository windowRepository;

    public String getBestWindow(String departmentId) {
        List<Window> windows = windowRepository.findAllByDepartmentsContaining(departmentId);
        if (windows.isEmpty()) {
            return "";
        }

        Window bestWindow = windows.get(0);
        for (Window window : windows) {
            long windowQueueCount = queueRepository.countByWindowId(window.getId().toHexString());
            long bestWindowQueueCount = queueRepository.countByWindowId(bestWindow.getId().toHexString());
            if (windowQueueCount < bestWindowQueueCount) {
                bestWindow = window;
            }
        }
        return bestWindow.getId().toHexString();
    }

    @Autowired
    public QueueService(QueueRepository queueRepository, DepartmentRepository departmentRepository, WindowRepository windowRepository) {
        this.queueRepository = queueRepository;
        this.departmentRepository = departmentRepository;
        this.windowRepository = windowRepository;
    }

    public String create(QueueRequest queueRequest, HttpServletResponse response) throws JsonProcessingException, JSONException {
        JSONObject responseJSON = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        if (queueRequest.getUserName() == null || queueRequest.getUserName().isEmpty()) {
            response.setStatus(4001);
            responseJSON.put("message", "Невалідне значення userName");
            return responseJSON.toString();
        }

        if (queueRequest.getDepartmentId() == null || queueRequest.getDepartmentId().isEmpty()) {
            response.setStatus(4002);
            responseJSON.put("message", "Невалідне значення departmentId");
            return responseJSON.toString();
        }
            Department department = departmentRepository.findById(new ObjectId(queueRequest.getDepartmentId()));

            if (department == null) {
                response.setStatus(4041);
                responseJSON.put("message", "Не вдалось знайти department");
                return responseJSON.toString();
            }

            String bestWindowId = getBestWindow(queueRequest.getDepartmentId());

            if (bestWindowId.isEmpty()) {
                bestWindowId = windowRepository.findFirstByDepartmentsContaining(queueRequest.getDepartmentId()).getId().toHexString();
            }

            Queue queue = new Queue(queueRequest.getDepartmentId(), queueRequest.getUserName(), bestWindowId);

            queue = queueRepository.save(queue);
            JSONObject queueJSON = new JSONObject(mapper.writeValueAsString(queue));
            queueJSON.put("id", queue.getId().toHexString());
            responseJSON.put("saved", true);
            responseJSON.put("queue", queueJSON);

        return responseJSON.toString();
    }

    public String delete(String id, HttpServletRequest request, HttpServletResponse response) throws JSONException {
        JSONObject responseJSON = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        ObjectId queueId = new ObjectId(id);

        Queue queue = queueRepository.findById(queueId);

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");

        if (queue == null) {
            response.setStatus(404);
            responseJSON.put("message", "Такої позиції не існує");
            return responseJSON.toString();
        }

        queueRepository.delete(queue);

        responseJSON.put("deleted", true);

        return responseJSON.toString();
    }

    public String getAll(HttpServletResponse response) throws JSONException {
        JSONObject responseJSON = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        List<Queue> queues = queueRepository.findAll();

        if (queues.isEmpty()) {
            response.setStatus(404);
        }

        List<JSONObject> queuesJSON = queues.stream().map(queue -> {
            try {
                JSONObject queueJSON = new JSONObject(mapper.writeValueAsString(queue));

                queueJSON.put("id", queue.getId().toHexString());

                return queueJSON;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        responseJSON.put("queues", queuesJSON);

        return responseJSON.toString();
    }
}
