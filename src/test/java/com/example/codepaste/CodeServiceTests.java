package com.example.codepaste;

import com.example.codepaste.dto.ResponseDTO;
import com.example.codepaste.entity.CodeSnippet;
import com.example.codepaste.persistence.SnippetRepository;
import com.example.codepaste.service.CodeService;
import com.example.codepaste.service.CodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CodeServiceTests {

    @Mock
    private SnippetRepository snippetRepo;
    private CodeService codeService;
    private UUID mockUuid1;
    private UUID mockUuid2;
    private UUID mockUuid3;
    private CodeSnippet mockSnippet1;
    private CodeSnippet mockSnippet2;
    private CodeSnippet mockSnippet3;

    @BeforeEach
    void initUseCase() {
        // Implement gateway between service and persistence layers
        codeService = new CodeServiceImpl(snippetRepo);

        mockUuid1 = UUID.fromString("c81d4e2e-bcf2-11e6-869b-7df92533d2db");
        mockSnippet1 = new CodeSnippet("public static main void(String[] args) {}", 100, 10);
        mockSnippet1.setId(mockUuid1);
        mockSnippet1.setDate(LocalDateTime.now());

        mockUuid2 = UUID.fromString("237e9877-e79b-12d4-a765-321741963000");
        mockSnippet2 = new CodeSnippet("{System.out.println(\"Hello, World\")}", 0, 1);
        mockSnippet2.setId(mockUuid2);
        mockSnippet2.setDate(LocalDateTime.now().minusHours(3));

        mockUuid3 = UUID.fromString("c6a8669e-ee95-4c42-9ef6-4a9b61380164");
        mockSnippet3 = new CodeSnippet("public interface CodeService {}", 0, 0);
        mockSnippet3.setId(mockUuid3);
        mockSnippet3.setDate(LocalDateTime.now().plusDays(2));
    }

    @DisplayName("Get code snippet that exists in data source")
    @Test
    public void returnCodeSnippet() {
        when(snippetRepo.findById(mockUuid1)).thenReturn(Optional.of(mockSnippet1));
        when(snippetRepo.save(mockSnippet1)).thenReturn(mockSnippet1);

        snippetRepo.save(mockSnippet1);
        CodeSnippet snippet = codeService.getCode(mockUuid1);
        assertEquals(snippet.getId(), mockSnippet1.getId());
        assertEquals(snippet.getCode(), mockSnippet1.getCode());
    }

    @DisplayName("Code snippet's number of remaining views decrement on each view if restricted")
    @Test
    public void decrementSnippetViews() {
        when(snippetRepo.findById(mockUuid1)).thenReturn(Optional.of(mockSnippet1));
        when(snippetRepo.save(mockSnippet1)).thenReturn(mockSnippet1);

        snippetRepo.save(mockSnippet1);

        // View twice to decrement snippet's views and time remaining values
        codeService.getCode(mockUuid1);
        CodeSnippet snippet = codeService.getCode(mockUuid1);

        assertEquals(8, snippet.getViews());
    }

    @DisplayName("Code snippet's views won't decrement if views are not restricted")
    @Test
    public void doesntDecrementSnippetViews() {
        when(snippetRepo.findById(mockUuid3)).thenReturn(Optional.of(mockSnippet3));
        when(snippetRepo.save(mockSnippet3)).thenReturn(mockSnippet3);

        snippetRepo.save(mockSnippet3);

        for (int i = 3; i > 0; i--) {
            codeService.getCode(mockUuid3);
        }

        CodeSnippet snippet = codeService.getCode(mockUuid3);
        assertEquals(0, snippet.getViews());
    }

    @DisplayName("Code snippet's time will decrement if the snippet is time restricted")
    @Test
    public void decrementSnippetTime() {
        UUID mockUuid4 = UUID.fromString("c6a8669e-ee95-4c42-9ef6-4a9b61380164");
        CodeSnippet mockSnippet4 = new CodeSnippet("public interface CodeService {}", 100, 0);
        mockSnippet4.setId(mockUuid4);
        mockSnippet4.setDate(LocalDateTime.now().minusSeconds(20));

        when(snippetRepo.findById(mockUuid4)).thenReturn(Optional.of(mockSnippet4));
        when(snippetRepo.save(mockSnippet4)).thenReturn(mockSnippet4);

        snippetRepo.save(mockSnippet4);
        CodeSnippet snippet = codeService.getCode(mockUuid3);
        assertTrue(snippet.getTimeRemaining() < 100);
    }

    @DisplayName("Code snippet's time doesn't decrement if it is not time restricted")
    @Test
    public void doesntDecrementSnippetTime() {
        long originalTime = mockSnippet3.getTimeRemaining();
        when(snippetRepo.save(mockSnippet3)).thenReturn(mockSnippet3);
        when(snippetRepo.findById(mockUuid3)).thenReturn(Optional.of(mockSnippet3));

        snippetRepo.save(mockSnippet3);
        CodeSnippet snippet = codeService.getCode(mockUuid3);
        assertEquals(originalTime, snippet.getTimeRemaining());
    }

    @DisplayName("Throws response status error if no more views remain")
    @Test
    public void noMoreViewsRemain() {
        when(snippetRepo.save(mockSnippet2)).thenReturn(mockSnippet2);
        when(snippetRepo.findById(mockUuid2)).thenReturn(Optional.of(mockSnippet2));

        snippetRepo.save(mockSnippet2);

        // Decrement remaining views to zero
        codeService.getCode(mockUuid2);
        assertThrows(ResponseStatusException.class, () -> codeService.getCode(mockUuid2));
    }

    @DisplayName("Try to retrieve code snippet that does not exist in data source")
    @Test
    public void getNonExistentSnippet() {
        when(snippetRepo.findById(mockUuid1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> codeService.getCode(mockUuid1));
    }

    @DisplayName("Insert new Code into data source")
    @Test
    public void insertCode() {
        when(snippetRepo.save(mockSnippet1)).thenReturn(mockSnippet1);

        ResponseDTO request = codeService.insertCode(mockSnippet1);

        verify(snippetRepo).save(mockSnippet1);
        assertEquals(request.getId(), mockUuid1);
        assertEquals(request.getCode(), mockSnippet1.getCode());
    }

    @DisplayName("Returns a list of all code snippets from the data source")
    @Test
    public void findAllSnippets() {
        saveAll();

        when(snippetRepo.findAll()).thenReturn(List.of(mockSnippet1, mockSnippet2, mockSnippet3));

        List<CodeSnippet> snippets = codeService.findAllCode();

        assertEquals(3, snippets.size());
        assertTrue(snippets.contains(mockSnippet1));
        assertTrue(snippets.contains(mockSnippet2));
        assertTrue(snippets.contains(mockSnippet2));
    }

    @DisplayName("Returns an empty list of code snippets from the data source")
    @Test
    public void returnEmptyList() {
        when(snippetRepo.findAll()).thenReturn(List.of());

        List<CodeSnippet> snippets = codeService.findAllCode();

        assertEquals(0, snippets.size());
    }

    @DisplayName("Returns a list of code snippets that aren't view restricted in descending order of upload time")
    @Test
    public void getLatestUploads() {
        UUID mockUuid4 = UUID.fromString("c6a8669e-ee95-4c42-9ef6-4a9b61380164");
        CodeSnippet mockSnippet4 = new CodeSnippet("public class CodeSnippet {}", 0, 0);
        mockSnippet4.setId(mockUuid4);
        mockSnippet4.setDate(LocalDateTime.now().plusDays(4));

        UUID mockUuid5 = UUID.fromString("c6a8669e-ee95-4c42-9ef6-4a9b61380164");
        CodeSnippet mockSnippet5 = new CodeSnippet("public class ApiWebController {}", 0, 0);
        mockSnippet5.setId(mockUuid5);
        mockSnippet5.setDate(LocalDateTime.now().plusDays(2).minusHours(1));

        saveAll();
        snippetRepo.save(mockSnippet4);
        snippetRepo.save(mockSnippet5);

        when(snippetRepo.findAllByOrderByDateDesc()).thenReturn(List.of(mockSnippet5, mockSnippet3, mockSnippet4));

        List<CodeSnippet> snippets = codeService.getLatestUploads();

        assertEquals(3, snippets.size());
        assertEquals(mockSnippet5, snippets.get(0));
        assertEquals(mockSnippet3, snippets.get(1));
        assertEquals(mockSnippet4, snippets.get(2));
    }

    @DisplayName("Returns an empty list of code snippets if all snippets are view restricted")
    @Test
    public void returnEmptyListOfLatestUpdates() {
        snippetRepo.save(mockSnippet1);
        snippetRepo.save(mockSnippet2);

        when(snippetRepo.findAllByOrderByDateDesc()).thenReturn(List.of());

        List<CodeSnippet> snippets = codeService.getLatestUploads();

        assertEquals(0, snippets.size());
    }

    @DisplayName("Deletes an existent code snippet from the data source")
    @Test
    public void deleteById() {
        saveAll();
        when(snippetRepo.findAll()).thenReturn(List.of(mockSnippet2, mockSnippet3));

        codeService.deleteById(mockUuid1);
        List<CodeSnippet> snippets = codeService.findAllCode();

        assertEquals(2, snippets.size());
        assertFalse(snippets.contains(mockSnippet1));
    }

    @DisplayName("Does not delete a non existent code snippet from the data source")
    @Test
    public void deleteNonExistentSnippet() {
        UUID fakeUuid = UUID.fromString("c6a8669e-ee95-4c42-9ef6-4a9b61380164");
        saveAll();

        when(snippetRepo.findAll()).thenReturn(List.of(mockSnippet1, mockSnippet2, mockSnippet3));

        codeService.deleteById(fakeUuid);
        List<CodeSnippet> snippets = codeService.findAllCode();

        assertEquals(3, snippets.size());
    }

    // Utility function to reduce repetitive code
    void saveAll() {
        snippetRepo.save(mockSnippet1);
        snippetRepo.save(mockSnippet2);
        snippetRepo.save(mockSnippet3);
    }
}
