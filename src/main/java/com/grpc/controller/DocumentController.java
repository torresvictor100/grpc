package com.grpc.controller;

import com.grpc.domain.Document;
import com.grpc.domain.User;
import com.grpc.repository.IDocumentRepository;
import com.grpc.repository.IUserRepository;
import com.grpc.v1.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class DocumentController extends DocumentServiceGrpc.DocumentServiceImplBase {
    @Autowired
    private final IDocumentRepository repository;

    @Autowired
    private final IUserRepository userRepository;

    public DocumentController(IDocumentRepository repository, IUserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public void createDocument(DocumentReq request, StreamObserver<DocumentRes> responseObserver) {

        User user = userRepository.findById(request.getUserId()).orElse(null);

        Document document = new Document(request.getName(), request.getData(), user);
        Document saved = repository.save(document);
        DocumentRes documentRes = DocumentRes.newBuilder().setId(saved.getId())
                .setName(saved.getName())
                .setData(saved.getData())
                .setUserId(saved.getUser().getId())
                .build();
        responseObserver.onNext(documentRes);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllDocumentResList(EmptyReq request, StreamObserver<DocumentResList> responseObserver) {
        List<Document> listDocument =  repository.findAll();
        List<DocumentRes> documentRes = listDocument.stream().map(document -> DocumentRes.newBuilder()
                .setId(document.getId())
                .setName(document.getName())
                .setData(document.getData())
                .setUserId(document.getUser().getId())
                .build()).toList();

        DocumentResList documentResList = DocumentResList.newBuilder().addAllDocuments(documentRes).build();
        responseObserver.onNext(documentResList);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllServerDocumentResStream(EmptyReq request, StreamObserver<DocumentRes> responseObserver) {
        super.getAllServerDocumentResStream(request, responseObserver);
    }
}
