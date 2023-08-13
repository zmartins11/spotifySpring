package com.spotify.Example.repository;

import com.spotify.Example.model.SessionEntity;
import jakarta.websocket.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRepository extends MongoRepository<SessionEntity, String> {
}
