package com.example.codepaste;

import com.example.codepaste.controller.ApiCodeController;
import com.example.codepaste.dto.RequestDTO;
import com.example.codepaste.dto.ResponseDTO;
import com.example.codepaste.entity.CodeSnippet;
import com.example.codepaste.service.CodeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@WebMvcTest(ApiCodeController.class)
public class ApiCodeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private CodeService codeService;

    @Test
    public void getValidCodeSnippet() throws Exception {
        UUID mockUuid = UUID.fromString("c81d4e2e-bcf2-11e6-869b-7df92533d2db");
        CodeSnippet mockSnippet = new CodeSnippet("public static main void(String[] args) {}",
                100, 10);
        mockSnippet.setId(mockUuid);
        mockSnippet.setDate(LocalDateTime.now());

        String code = mockSnippet.getCode();
        String date = mockSnippet.getDate();
        when(codeService.getCode(mockUuid)).thenReturn(mockSnippet);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/code/{id}", mockSnippet.getId()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(code)))
                .andExpect(jsonPath("$.date", is(date)));
    }

    @Test
    public void getNonExistentCode() throws Exception {
        UUID fakeUuid = UUID.fromString("237e9877-e79b-12d4-a765-321741963000");
        when(codeService.getCode(fakeUuid))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/code/{id}", fakeUuid))
                .andExpect(status().isNotFound());
    }

    @Test
    public void postCode() throws Exception {
        RequestDTO mockRequest = new RequestDTO("{Hello, world!}", 0, 0);
        CodeSnippet mockSnippet = new CodeSnippet(mockRequest.getCode(), mockRequest.getTime(), mockRequest.getTime());
        ResponseDTO mockResponse = new ResponseDTO(UUID.randomUUID(), mockRequest.getCode());
        when(codeService.insertCode(mockSnippet)).thenReturn(mockResponse);

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.post("/api/code/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest));

        mockMvc.perform(request)
                .andExpect(status().isOk());
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

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/code/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(mockSnippets.size()));

    }
}
