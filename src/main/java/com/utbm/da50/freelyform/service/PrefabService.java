package com.utbm.da50.freelyform.service;

import com.utbm.da50.freelyform.exceptions.UniqueResponseException;
import com.utbm.da50.freelyform.exceptions.ValidationFieldException;
import com.utbm.da50.freelyform.model.Field;
import com.utbm.da50.freelyform.model.Group;
import com.utbm.da50.freelyform.model.Prefab;
import com.utbm.da50.freelyform.repository.PrefabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrefabService {
    private final PrefabRepository repository;
    private final FieldService fieldService;
    private final AnswerService answerService;
    @Autowired
    public PrefabService(PrefabRepository repository, FieldService fieldService, @Lazy AnswerService answerService) {
        this.repository = repository;
        this.fieldService = fieldService;
        this.answerService = answerService;
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

    /**
     * Get prefab by id and check if the user has already answered to this prefab
     * @param id prefab id
     * @param userId user id of the connected user
     * @return prefab with the flag isAlreadyAnswered set to true if user has already answered
     */
    public Prefab getPrefabById(String id, String userId) {
        Prefab prefab = getPrefabById(id);
        // Set the flag if user has already answered or groups is empty or null
        if (prefab.getGroups() == null || prefab.getGroups().isEmpty()) {
            prefab.setIsAlreadyAnswered(Boolean.TRUE);
            return prefab;
        }
        try {
            answerService.validateUniqueUserResponse(prefab.getId(), userId);
        } catch (UniqueResponseException e) {
            prefab.setIsAlreadyAnswered(Boolean.TRUE);
        }
        return prefab;
    }


    public Prefab getPrefabById(String prefabId) throws NoSuchElementException {
        return getPrefabById(prefabId, Boolean.TRUE);
    }

    public Prefab getPrefabById(String prefabId, Boolean withHiddenFields) throws NoSuchElementException {
        Prefab p = repository.findById(prefabId).orElseThrow(
                () -> new NoSuchElementException("Prefab with id " + prefabId + " not found")
        );
        if (p.getGroups() == null || p.getGroups().isEmpty()) p.setIsAlreadyAnswered(Boolean.TRUE);
        if (withHiddenFields)
            return p;
        return Prefab.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .tags(p.getTags())
                .isActive(p.getIsActive())
                .isAlreadyAnswered(p.getIsAlreadyAnswered())
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

    public List<Prefab> getPrefabsByUser(String userId) {
        return repository.findByUserId(userId);
    }

    public boolean doesUserOwnPrefab(String userId, String prefabId) {
        Optional<Prefab> prefab = repository.findById(prefabId);
        return prefab.map(value -> (Boolean) value.getUserId().equals(userId)).orElse(Boolean.FALSE);
    }
}
