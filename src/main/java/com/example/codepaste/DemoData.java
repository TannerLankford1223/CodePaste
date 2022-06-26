package com.example.codepaste;

import com.example.codepaste.persistence.SnippetRepository;
import com.example.codepaste.entity.CodeSnippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DemoData implements CommandLineRunner {

    @Autowired
    private SnippetRepository snippetRepo;

    @Override
    public void run(String... args) throws Exception {
        snippetRepo.deleteAllInBatch();
        List<CodeSnippet> snippets = new ArrayList<>();
        snippets.add(new CodeSnippet("{Hello, World!}", 0, 0));
        snippets.add(new CodeSnippet("public static void main(String[] args) {}", 0, 0));
        snippets.add(new CodeSnippet("public class CodePaste {}", 0, 0));
        snippets.add(new CodeSnippet("public interface CodeService {}", 100, 0));
        snippets.add(new CodeSnippet("testCode1", 0, 100));
        snippets.add(new CodeSnippet("testCode2", 1, 0));
        snippets.add(new CodeSnippet("testCode3", 0, 1));
        snippetRepo.saveAll(snippets);
    }
}
