package com.utbm.da50.freelyform.service;

import com.utbm.da50.freelyform.model.Prefab;
import com.utbm.da50.freelyform.repository.PrefabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PrefabService {
    @Autowired
    private PrefabRepository repository;

    public List<Prefab> getAllPrefabs() {
        return repository.findAll();
    }

    public Prefab createPrefab(Prefab newPrefab) {
        return repository.save(newPrefab);
    }

    public Prefab updatePrefab(String id, Prefab prefab) {
        Prefab existingPrefab = repository.findById(id).orElse(null);
        assert existingPrefab != null;
        existingPrefab.setName(prefab.getName());
        existingPrefab.setDescription(prefab.getDescription());
        existingPrefab.setTags(prefab.getTags());
        return repository.save(existingPrefab);
    }

    public void deletePrefab(String id) {
        repository.deleteById(id);
    }

    public Prefab getPrefab(String id) {
        return repository.findById(id).orElse(null);
    }
}
