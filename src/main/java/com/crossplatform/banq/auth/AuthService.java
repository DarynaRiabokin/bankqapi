package com.crossplatform.banq.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final String login = "admin";
    private final String password = "admin";
    private final String authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

    public String checkAccess(HttpServletRequest request, HttpServletResponse response) {
        JSONObject responseJson = new JSONObject();
        String bearer = request.getHeader("Authorization");

        if (bearer.isEmpty() || !bearer.split(" ")[0].equals(authToken)) {
            response.setStatus(403);
            responseJson.put("message", "Заборонено");

            return responseJson.toString();
        }

        return "";
    }

    public String login(String login, String password, HttpServletResponse response) {
        JSONObject responseJson = new JSONObject();

        if (!login.equals(this.login)) {
            response.setStatus(4001);
            responseJson.put("message", "Невірний login");

            return responseJson.toString();
        }

        if (!password.equals(this.password)) {
            response.setStatus(4002);
            responseJson.put("message", "Невірний password");

            return responseJson.toString();
        }

        responseJson.put("token", authToken);

        return responseJson.toString();
    }
}
