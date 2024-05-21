package com.crossplatform.banq.department;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public String getAllDepartments(HttpServletResponse response) throws JSONException {
        JSONObject responseJson = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();
        List<Department> departments = departmentRepository.findAll();

        if (departments.isEmpty()) {
            response.setStatus(404);
        }

        List<JSONObject> departmentsJson = departments.stream().map(department -> {
            try {
                JSONObject departmentJson = new JSONObject(mapper.writeValueAsString(department));
                departmentJson.put("id", department.getId().toHexString());

                return departmentJson;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        responseJson.put("departments", departmentsJson);

        return responseJson.toString();
    }
}
