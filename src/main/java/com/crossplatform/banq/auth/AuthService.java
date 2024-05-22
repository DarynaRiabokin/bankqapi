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

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");

        if (bearer.isEmpty() || !bearer.split(" ")[1].equals(authToken)) {
            response.setStatus(403);
            responseJson.put("message", "Заборонено");

            return responseJson.toString();
        }

        return "";
    }

    public String login(String login, String password, HttpServletRequest request, HttpServletResponse response) {
        JSONObject responseJson = new JSONObject();

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");

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
