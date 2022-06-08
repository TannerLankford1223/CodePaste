package com.example.codepaste.controller;

import com.example.codepaste.entity.CodeSnippet;
import com.example.codepaste.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/{id}")
    public String getCode(@PathVariable UUID id, Model model) {
        CodeSnippet userCode = codeService.getCodeEntity(id);
        model.addAttribute("code", userCode);

        return "code";
    }

    @GetMapping("/new")
    public String createCode() {
        return "newCode";
    }

    @GetMapping("/latest")
    public String getLatestUploads(Model model) {
        List<CodeSnippet> uploads = codeService.getLatestUploads();

        model.addAttribute("uploads", uploads);

        return "latestCode";
    }
}
