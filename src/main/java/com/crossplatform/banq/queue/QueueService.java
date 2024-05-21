package com.crossplatform.banq.queue;

import com.crossplatform.banq.department.Department;
import com.crossplatform.banq.department.DepartmentRepository;
import com.crossplatform.banq.window.WindowRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QueueService {
    private final QueueRepository queueRepository;
    private final DepartmentRepository departmentRepository;
    private final WindowRepository windowRepository;

    private String getBestWindow(String departmentId) {
        List<Queue> queueByDepartment = queueRepository.findAllByDepartmentId(departmentId);

        HashMap<String, Integer> windowIdCountMap = new HashMap<>();

        for (Queue queue : queueByDepartment) {
            String windowId = queue.getWindowId();

            windowIdCountMap.put(windowId, windowIdCountMap.getOrDefault(windowId, 0) + 1);
        }

        int minCount = Integer.MAX_VALUE;
        String windowIdWithMinCount = "";
        for (Map.Entry<String, Integer> entry : windowIdCountMap.entrySet()) {
            if (entry.getValue() < minCount) {
                minCount = entry.getValue();
                windowIdWithMinCount = entry.getKey();
            }
        }

        return windowIdWithMinCount;
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

    public String delete(String id, HttpServletResponse response) throws JSONException {
        JSONObject responseJSON = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        ObjectId queueId = new ObjectId(id);

        Queue queue = queueRepository.findById(queueId);

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
