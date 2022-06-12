package com.example.codepaste;

import com.example.codepaste.controller.WebCodeController;
import com.example.codepaste.entity.CodeSnippet;
import com.example.codepaste.service.CodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(WebCodeController.class)
public class WebCodeControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CodeService codeService;

    @Test
    public void testPostCode() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/code/new"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(view().name("newCode"));
    }

    @Test
    public void getCode() throws Exception {
        UUID mockUuid = UUID.fromString("c81d4e2e-bcf2-11e6-869b-7df92533d2db");
        CodeSnippet mockSnippet = new CodeSnippet("public static main void(String[] args) {}", 100, 10);
        mockSnippet.setId(mockUuid);
        mockSnippet.setDate(LocalDateTime.now());
        when(codeService.getCodeSnippet(mockUuid)).thenReturn(mockSnippet);

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get("/code/{id}", mockUuid)
                        .contentType("text/html;charset=UTF-8")
                        .accept(String.valueOf(UUID.class))
                        .content(String.valueOf(mockUuid));

        this.mockMvc.perform(request)
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(view().name("code"))
                .andExpect(model().attribute("code",
                        allOf(
                                hasProperty("id", is(mockUuid)),
                                hasProperty("code", is(mockSnippet.getCode()))
                        )));
    }

    @Test
    public void getNonExistentCode() throws Exception {
        UUID fakeUuid = UUID.fromString("237e9877-e79b-12d4-a765-321741963000");
        when(codeService.getCodeSnippet(fakeUuid))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/code/{id}", fakeUuid))
                .andExpect(status().isNotFound());
    }


    @Test
    public void getLatestUploads() throws Exception {
        UUID mockUuid1 = UUID.fromString("c81d4e2e-bcf2-11e6-869b-7df92533d2db");
        CodeSnippet mockSnippet1 = new CodeSnippet("public static main void(String[] args) {}", 0, 0);
        mockSnippet1.setId(mockUuid1);
        mockSnippet1.setDate(LocalDateTime.now());

        UUID mockUuid2 = UUID.fromString("237e9877-e79b-12d4-a765-321741963000");
        CodeSnippet mockSnippet2 = new CodeSnippet("{System.out.println(\"Hello, World\")}", 0, 0);
        mockSnippet2.setId(mockUuid2);
        mockSnippet2.setDate(LocalDateTime.now().minusHours(3));

        UUID mockUuid3 = UUID.fromString("c6a8669e-ee95-4c42-9ef6-4a9b61380164");
        CodeSnippet mockSnippet3 = new CodeSnippet("public interface CodeService {}", 0, 0);
        mockSnippet3.setId(mockUuid3);
        mockSnippet3.setDate(LocalDateTime.now().plusDays(2));

        List<CodeSnippet> mockSnippets = List.of(mockSnippet2, mockSnippet1, mockSnippet3);

        when(codeService.getLatestUploads()).thenReturn(mockSnippets);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/code/latest"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(view().name("latestCode"))
                .andExpect(model().attribute("uploads", equalTo(mockSnippets)));

    }

    @Test
    public void onlyReturnsNonRestrictedSnippets() throws Exception {
        UUID mockUuid1 = UUID.fromString("c81d4e2e-bcf2-11e6-869b-7df92533d2db");
        CodeSnippet mockSnippet1 = new CodeSnippet("public static main void(String[] args) {}", 0, 0);
        mockSnippet1.setId(mockUuid1);
        mockSnippet1.setDate(LocalDateTime.now());

        UUID mockUuid2 = UUID.fromString("237e9877-e79b-12d4-a765-321741963000");
        CodeSnippet mockSnippet2 = new CodeSnippet("{System.out.println(\"Hello, World\")}", 0, 5);
        mockSnippet2.setId(mockUuid2);
        mockSnippet2.setDate(LocalDateTime.now().minusHours(3));

        UUID mockUuid3 = UUID.fromString("c6a8669e-ee95-4c42-9ef6-4a9b61380164");
        CodeSnippet mockSnippet3 = new CodeSnippet("public interface CodeService {}", 100, 0);
        mockSnippet3.setId(mockUuid3);
        mockSnippet3.setDate(LocalDateTime.now().plusDays(2));

        List<CodeSnippet> mockSnippets = List.of(mockSnippet2, mockSnippet1, mockSnippet3);
        List<CodeSnippet> unrestrictedSnippets = List.of(mockSnippet1);

        when(codeService.getLatestUploads()).thenReturn(unrestrictedSnippets);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/code/latest"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(view().name("latestCode"))
                .andExpect(model().attribute("uploads", equalTo(unrestrictedSnippets)));
    }
}
