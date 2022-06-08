package com.example.codepaste.dao;

import com.example.codepaste.entity.CodeSnippet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SnippetDSGatewayImpl implements SnippetDSGateway {
    @Override
    public void insert(CodeSnippet code) {

    }

    @Override
    public Optional<CodeSnippet> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<CodeSnippet> findAll() {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public List<CodeSnippet> findMostRecent() {
        return null;
    }
}
