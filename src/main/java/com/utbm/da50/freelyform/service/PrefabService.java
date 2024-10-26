package com.utbm.da50.freelyform.service;

import com.utbm.da50.freelyform.exceptions.ValidationFieldException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Group;
import com.utbm.da50.freelyform.model.Prefab;
import com.utbm.da50.freelyform.model.User;
import com.utbm.da50.freelyform.repository.PrefabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrefabService {
    private final PrefabRepository repository;
    private final FieldService fieldService;
    @Autowired
    public PrefabService(PrefabRepository repository, FieldService fieldService) {
        this.repository = repository;
        this.fieldService = fieldService;
    }

    public Prefab createPrefab(Prefab newPrefab) throws ValidationFieldException {
        // check if every field are valid
        newPrefab.getGroups().forEach(group -> fieldService.validateFields(group.getFields()));
        return repository.save(newPrefab);
    }

    public Prefab updatePrefab(String id, Prefab prefab) throws NoSuchElementException, ValidationFieldException {
        Prefab existingPrefab = getPrefabById(id);
        existingPrefab.setName(prefab.getName());
        existingPrefab.setDescription(prefab.getDescription());
        existingPrefab.setTags(prefab.getTags());
        existingPrefab.setGroups(prefab.getGroups());
        // check if every field are valid
        existingPrefab.getGroups().forEach(group -> fieldService.validateFields(group.getFields()));
        return repository.save(existingPrefab);
    }

    public Prefab updatePrefabStatus(Prefab existingPrefab, Boolean toState) throws NoSuchElementException, ValidationFieldException {
        existingPrefab.setIsActive(toState);
        return repository.save(existingPrefab);
    }

    public Prefab deletePrefab(String id) throws NoSuchElementException{
        Prefab prefabToDelete = getPrefabById(id);
        repository.deleteById(id);
        return prefabToDelete;
    }

    public Prefab getPrefabById(String prefabId) throws NoSuchElementException {
        return getPrefabById(prefabId, true);
    }

    public Prefab getPrefabById(String prefabId, Boolean withHiddenFields) throws NoSuchElementException {
        Prefab p = repository.findById(prefabId).orElse(null);
        if (p == null) {
            throw new NoSuchElementException("Prefab not found");
        }
        if (!withHiddenFields) {
            return Prefab.builder()
                    .id(p.getId())
                    .name(p.getName())
                    .description(p.getDescription())
                    .tags(p.getTags())
                    .isActive(p.getIsActive())
                    .groups(
                            p.getGroups().stream()
                                    .map(group -> {
                                        List<Field> filteredFields = group.getFields().stream()
                                                .filter(field -> !field.getHidden()) // Exclude hidden fields if withHidden is true
                                                .collect(Collectors.toList());
                                        return new Group(group.getId(), group.getName(), filteredFields);
                                    })
                                    .collect(Collectors.toList())
                    )
                    .createdAt(p.getCreatedAt())
                    .updatedAt(p.getUpdatedAt())
                    .userId(p.getUserId())
                    .build();
        }

        return p;
    }

    public List<Prefab> getPrefabsByUser(String userId) {
        return repository.findByUserId(userId);
    }

    public boolean doesUserOwnPrefab(String userId, String prefabId) {
        Optional<Prefab> prefab = repository.findById(prefabId);
        return prefab.map(value -> value.getUserId().equals(userId)).orElse(false);
    }
}
