package com.crossplatform.banq.window;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WindowService {
    private final WindowRepository windowRepository;

    @Autowired
    public WindowService(WindowRepository windowRepository) {
        this.windowRepository = windowRepository;
    }

    public String getAll(HttpServletResponse response) throws JSONException {
        JSONObject responseJson = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        List<Window> windowList = windowRepository.findAll();

        if (windowList.isEmpty()) {
            response.setStatus(404);
        }

        List<JSONObject> windowListJson = windowList.stream().map(window -> {
            try {
                JSONObject windowJson = new JSONObject(mapper.writeValueAsString(window));
                windowJson.put("id", window.getId().toHexString());
                return windowJson;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
        responseJson.put("windows", windowListJson);

        return responseJson.toString();
    }
}
