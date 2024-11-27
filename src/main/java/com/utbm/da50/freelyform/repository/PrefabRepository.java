package com.utbm.da50.freelyform.repository;

import com.utbm.da50.freelyform.model.Prefab;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrefabRepository extends MongoRepository<Prefab, String> {
    // Find all prefabs by user id
    List<Prefab> findByUserId(String id);

    // Delete by user id
    void deleteByUserId(String id);
}
