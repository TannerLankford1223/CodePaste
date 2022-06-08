package com.example.codepaste.service;

import com.example.codepaste.dao.SnippetDSGateway;
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
    private final SnippetDSGateway dsGateway;

    @Autowired
    public CodeServiceImpl(SnippetDSGateway dsGateway) {
        this.dsGateway = dsGateway;
    }

    @Override
    public CodeSnippet getCodeEntity(UUID id) {
        Optional<CodeSnippet> codeOpt = dsGateway.findById(id);
        if (codeOpt.isPresent()) {
            CodeSnippet code = codeOpt.get();
            code.updateTime();
            if (!code.isEnabled()) {
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
    public void insertCode(CodeSnippet code) {
        dsGateway.insert(code);
    }

    @Override
    public List<CodeSnippet> findAll() {
        return dsGateway.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        dsGateway.deleteById(id);
    }

    @Override
    public List<CodeSnippet> getLatestUploads() {
        List<CodeSnippet> codeRepo = dsGateway.findMostRecent();
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
