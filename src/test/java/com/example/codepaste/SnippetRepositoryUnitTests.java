package com.example.codepaste;

import com.example.codepaste.entity.CodeSnippet;
import com.example.codepaste.persistence.SnippetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class SnippetRepositoryUnitTests {

    @Autowired
    SnippetRepository snippetRepo;

    @BeforeEach
    public void insertSnippets() {
        CodeSnippet snippet1 = new CodeSnippet("public static main void(String[] args) {}", 0, 0);
        CodeSnippet snippet2 = new CodeSnippet("{System.out.println(\"Hello, World\")}", 0, 0);
        CodeSnippet snippet3 = new CodeSnippet("public interface CodeService {}", 0, 0);
        snippetRepo.saveAll(List.of(snippet2, snippet1, snippet3));
    }

    @Test
    public void findAllSnippets_ReturnsListOfSnippetsInOrderByDate() {
        List<CodeSnippet> snippets = snippetRepo.findAllByOrderByDateDesc();
        assertEquals(3, snippets.size());
        assertEquals("public interface CodeService {}", snippets.get(0).getCode());
        assertEquals("public static main void(String[] args) {}", snippets.get(1).getCode());
        assertEquals("{System.out.println(\"Hello, World\")}", snippets.get(2).getCode());
    }

}
