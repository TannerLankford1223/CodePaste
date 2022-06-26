package com.example.codepaste.persistence;

import com.example.codepaste.entity.CodeSnippet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SnippetRepository extends JpaRepository<CodeSnippet, UUID> {
    List<CodeSnippet> findAllByOrderByDateDesc();
}
