package com.example.codepaste.dao;

import com.example.codepaste.entity.CodeSnippet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SnippetDSGateway {
    CodeSnippet insert(CodeSnippet code);
    Optional<CodeSnippet> findById(UUID id);
    List<CodeSnippet> findAll();
    void deleteById(UUID id);
    List<CodeSnippet> findMostRecent();
}
