package com.spotify.Example.repository;

import com.spotify.Example.model.SavedTrackEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SavedTrackRepository extends MongoRepository<SavedTrackEntity, String> {
}
