package com.example.codepaste.service;

import com.example.codepaste.entity.CodeSnippet;

import java.util.List;
import java.util.UUID;

public interface CodeService {
    CodeSnippet getCodeSnippet(UUID id);

    UUID insertCode(CodeSnippet code);

    List<CodeSnippet> findAll();

    List<CodeSnippet> getLatestUploads();

    void deleteById(UUID id);
}
