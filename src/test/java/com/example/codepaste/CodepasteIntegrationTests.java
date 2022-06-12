package com.example.codepaste;

import com.example.codepaste.dao.SnippetRepository;
import com.example.codepaste.dto.RequestDTO;
import com.example.codepaste.entity.CodeSnippet;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/snippets")
@Testcontainers
public class CodepasteIntegrationTests {

    @Rule
    public JUnitRestDocumentation restDoc = new JUnitRestDocumentation("build/generated-snippets");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    SnippetRepository snippetRepo;

    // Iniitialize PostgreSQL docker container
    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer("postgres")
            .withDatabaseName("testPostgres")
            .withUsername("user")
            .withPassword("testPass");


    @Test
    public void dbShouldhaveEntriesAfterBoot() {
        List<CodeSnippet> snippets = snippetRepo.findAll();
        assertThat(snippets.size()).isEqualTo(7);
        assertThat(snippets.get(0).getCode()).isEqualTo("{Hello, World!}");
        assertThat(snippets.get(4).getCode()).isEqualTo("testCode1");
    }

    @Test
    public void getCodeWorksThroughAllLayers_api() throws Exception {
        CodeSnippet newSnippet = snippetRepo.save(new CodeSnippet("This is a test!", 0, 35));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/code/{id}", newSnippet.getId()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(newSnippet.getCode())))
                .andExpect(jsonPath("$.date", is(newSnippet.getDate())))
                .andDo(document("getCode"));
    }

    @Test
    public void getCodeReturnsErrorIfNonExistent_api() throws Exception {
        UUID fakeUuid = UUID.randomUUID();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/code/{id}", fakeUuid))
                .andExpect(status().isNotFound());
    }

    @Test
    public void postCodeWorksThroughAllLayers_api() throws Exception {
        RequestDTO requestDTO = new RequestDTO("This is a test!", 55, 10);

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.post("/api/code/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(requestDTO));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andDo(document("postCode"));
    }

    @Test
    public void getLatestUploadsWorksThroughAllLayers_api() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/code/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andDo(document("getLatestUploads")); // Only return non restricted snippets
    }

    @Test
    public void getCodeWorksThroughAllLayers_web() throws Exception {
        CodeSnippet newSnippet = snippetRepo.save(new CodeSnippet("This is a test!", 25, 0));

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get("/code/{id}", newSnippet.getId())
                        .contentType("text/html;charset=UTF-8")
                        .accept(String.valueOf(UUID.class))
                        .content(String.valueOf(newSnippet.getId()));

        this.mockMvc.perform(request)
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(view().name("code"))
                .andExpect(model().attribute("code",
                        allOf(
                                hasProperty("id", is(newSnippet.getId())),
                                hasProperty("code", is(newSnippet.getCode()))
                        )));
    }

    @Test
    public void getCodeReturnsErrorIfNonExistent_web() throws Exception {
        UUID fakeUuid = UUID.randomUUID();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/code/{id}", fakeUuid))
                .andExpect(status().isNotFound());
    }

    @Test
    public void postCodeWorksThroughAllLayers_web() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/code/new"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(view().name("newCode"));
    }

    @Test
    public void getLatestUploadsWorksThroughAllLayers_web() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/code/latest"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(view().name("latestCode"))
                .andExpect(model().attribute("uploads", isA(List.class)));
    }

}
