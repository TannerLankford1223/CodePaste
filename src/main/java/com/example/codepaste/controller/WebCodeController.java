package com.example.codepaste.controller;

import com.example.codepaste.entity.CodeSnippet;
import com.example.codepaste.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/code")
public class WebCodeController {
    private final CodeService codeService;

    @Autowired
    public WebCodeController(CodeService codeService) {
        this.codeService = codeService;
    }

    @RequestMapping("/")
    public String routeToNewCode() {
        return "newCode";
    }

    @GetMapping("/{id}")
    public String getCode(@PathVariable UUID id, Model model) {
        CodeSnippet userCode = codeService.getCode(id);
        model.addAttribute("userCode", userCode);

        return "code";
    }

    @GetMapping("/new")
    public String postCode() {
        return "newCode";
    }

    @GetMapping("/latest")
    public String getLatestUploads(Model model) {
        List<CodeSnippet> uploads = codeService.getLatestUploads();

        model.addAttribute("uploads", uploads);

        return "latestCode";
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error-404";
            }
        }
        return "error";
    }
}
