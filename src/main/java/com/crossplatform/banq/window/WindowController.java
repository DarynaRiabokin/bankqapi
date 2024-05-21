package com.crossplatform.banq.window;

import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class WindowController {
    private final WindowService windowService;

    @Autowired
    public WindowController(WindowService windowService) {
        this.windowService = windowService;
    }

    @GetMapping("/windows")
    @ResponseBody
    public String getAll(HttpServletResponse response) throws JSONException {
        return windowService.getAll(response);
    }
}
