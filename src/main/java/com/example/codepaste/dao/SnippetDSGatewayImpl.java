package com.example.codepaste.dao;

import com.example.codepaste.entity.CodeSnippet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Class acts as a gateway between the business logic and data source, creating a looser coupling and
// enabling better flexibility and interchangeability of data sources
@Service
public class SnippetDSGatewayImpl implements SnippetDSGateway {

   private final SnippetRepository snippetRepo;

    public SnippetDSGatewayImpl(SnippetRepository snippetRepo) {
        this.snippetRepo = snippetRepo;
    }

    @Override
    public CodeSnippet insert(CodeSnippet code) {
        return snippetRepo.save(code);

    }

    @Override
    public Optional<CodeSnippet> findById(UUID id) {
        return snippetRepo.findById(id);
    }

    @Override
    public List<CodeSnippet> findAll() {
        return snippetRepo.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        snippetRepo.deleteById(id);
    }

    @Override
    public List<CodeSnippet> findMostRecent() {
        return snippetRepo.findAllByOrderByDateDesc();
    }
}
