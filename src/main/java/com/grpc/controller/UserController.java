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
public class UserController extends UserServiceGrpc.UserServiceImplBase {
    @Autowired
    private final IUserRepository repository;

    @Autowired
    private final IDocumentRepository documentRepository;

    public UserController(IUserRepository repository, IDocumentRepository documentRepository) {
        this.repository = repository;
        this.documentRepository = documentRepository;
    }

    @Override
    public void create(UserReq request, StreamObserver<UserRes> responseObserver) {
        User user = new User(request.getName(), request.getEmail());

        User saved = repository.save(user);

        UserRes userRes = UserRes.newBuilder().setId(saved.getId())
                .setName(saved.getName())
                .setEmail(saved.getEmail())
                .build();
        responseObserver.onNext(userRes);
        responseObserver.onCompleted();
    }

    @Override
    public void getAll(EmptyReq request, StreamObserver<UserResList> responseObserver) {
        List<User> listUser = repository.findAll();

        List<UserRes> userRes = listUser.stream().map(user -> UserRes.newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .setDocumentResList(getDocumentList(user.getId()))
                .build()).toList();

        UserResList userResList = UserResList.newBuilder().addAllUsers(userRes).build();
        responseObserver.onNext(userResList);
        responseObserver.onCompleted();
    }

    private DocumentResList getDocumentList(Long userId) {
        List<Document> listDocument = documentRepository.findByUserId(userId);
        List<DocumentRes> documentRes = listDocument.stream().map(document -> DocumentRes.newBuilder()
                .setId(document.getId())
                .setName(document.getName())
                .setData(document.getData())
                .setUserId(document.getUser().getId())
                .build()).toList();

        return DocumentResList.newBuilder().addAllDocuments(documentRes).build();
    }

    @Override
    public void getAllServerStream(EmptyReq request, StreamObserver<UserRes> responseObserver) {
        repository.findAll().forEach(user -> {
            UserRes userRes = UserRes.newBuilder()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();
            responseObserver.onNext(userRes);
        });
        responseObserver.onCompleted();
    }
}
