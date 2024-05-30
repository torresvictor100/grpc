package com.grpc.repository;

import com.grpc.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUserId(Long userId);
}
