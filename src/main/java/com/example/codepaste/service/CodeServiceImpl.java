package com.example.codepaste.service;

import com.example.codepaste.persistence.SnippetRepository;
import com.example.codepaste.dto.ResponseDTO;
import com.example.codepaste.entity.CodeSnippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class CodeServiceImpl implements CodeService {
    private final SnippetRepository snippetRepo;

    @Autowired
    public CodeServiceImpl(SnippetRepository snippetRepo) {
        this.snippetRepo = snippetRepo;
    }

    @Override
    public CodeSnippet getCode(UUID id) {
        Optional<CodeSnippet> codeOpt = snippetRepo.findById(id);
        if (codeOpt.isPresent()) {
            CodeSnippet code = codeOpt.get();
            code.updateTime();
            if (!code.isEnabled()) {
                snippetRepo.deleteById(code.getId());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            code.decrementViews();
            insertCode(code);
            return code;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseDTO insertCode(CodeSnippet request) {
        CodeSnippet response = snippetRepo.save(request);
        return new ResponseDTO(response.getId(), response.getCode());
    }

    @Override
    public List<CodeSnippet> findAllCode() {
        return snippetRepo.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        snippetRepo.deleteById(id);
    }

    @Override
    public List<CodeSnippet> getLatestUploads() {
        List<CodeSnippet> codeRepo = snippetRepo.findAllByOrderByDateDesc();
        List<CodeSnippet> uploads = new ArrayList<>();

        int i = 0;
        while (i < codeRepo.size() && uploads.size() < 10) {
            CodeSnippet code = codeRepo.get(i);
            if (!(code.isViewRestriction() || code.isTimeRestriction())) {
                uploads.add(code);
            }

            i++;
        }

        return uploads;
    }
}
