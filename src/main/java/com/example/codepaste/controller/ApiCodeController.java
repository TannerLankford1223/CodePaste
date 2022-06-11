package com.example.codepaste.controller;

import com.example.codepaste.dto.RequestDTO;
import com.example.codepaste.entity.CodeSnippet;
import com.example.codepaste.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/code")
public class ApiCodeController {
    private  final CodeService codeService;

    @Autowired
    public ApiCodeController(CodeService codeService) {
        this.codeService = codeService;
    }

    @GetMapping("/{id}")
    public CodeSnippet getCode(@PathVariable UUID id) {
        return codeService.getCodeSnippet(id);
    }

    @GetMapping("/latest")
    public List<CodeSnippet> getLatestUploads() {
        return codeService.getLatestUploads();
    }

    @PostMapping("/new")
    @ResponseBody
    public ResponseEntity<Map<String, UUID>> postCode(@RequestBody RequestDTO request) {
        CodeSnippet code = new CodeSnippet(request.getCode(),
                request.getTime(), request.getViews());
        UUID id = codeService.insertCode(code);
        return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }
}
