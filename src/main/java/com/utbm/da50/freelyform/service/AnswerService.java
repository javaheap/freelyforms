package com.utbm.da50.freelyform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utbm.da50.freelyform.enums.TypeRule;
import com.utbm.da50.freelyform.exceptions.*;
import com.utbm.da50.freelyform.model.AnswerUser;
import com.utbm.da50.freelyform.enums.TypeField;
import com.utbm.da50.freelyform.model.*;
import com.utbm.da50.freelyform.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for processing and validating answers submitted by users.
 */
@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final PrefabService prefabService;
    private final UserService userService;
    private final FieldService fieldService;

    /**
     * Processes a user's answer by validating it and saving it to the repository.
     *
     * @param prefabId the ID of the prefab associated with the answer
     * @param user     the user submitting the answer
     * @param answerGroup  the answer request containing the answers
     * @throws UniqueResponseException if a unique response exists or validation fails
     */
    public void processAnswer(String prefabId, User user, AnswerGroup answerGroup) throws RuntimeException {
        String userId = Optional.ofNullable(user).map(User::getId).orElse("guest");
        validateUniqueUserResponse(prefabId, userId);
        checkFormPrefab(prefabId, answerGroup);

        answerGroup.setUserId(userId);
        answerGroup.setPrefabId(prefabId);

        answerRepository.save(answerGroup);
    }

    /**
     * Retrieves an answer group by prefab ID and answer ID.
     *
     * @param prefabId the ID of the prefab
     * @param answerId the ID of the answer
     * @return the found AnswerGroup
     * @throws ResourceNotFoundException if no response is found for the provided IDs
     */
    public AnswerGroup getAnswerGroup(String prefabId, String answerId, User user) {
        String userId = user.getId();

        if(!prefabService.doesUserOwnPrefab(userId, prefabId))
            throw new RuntimeException(
                    String.format("The user '%s' doesn't own this prefab '%s'", userId, prefabId)
            );

        AnswerGroup answerGroup = answerRepository.findByPrefabIdAndId(prefabId, answerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No response found for prefabId '%s' and answerId '%s'", prefabId, answerId)
                ));

        userId = answerGroup.getUserId();

        AnswerUser answerUser = new AnswerUser("Guest", "");
        if (!Objects.equals(userId, "guest")) {
            answerUser.setName(String.format("%s %s", user.getFirstName(), user.getLastName()));
            answerUser.setEmail(user.getEmail());
        }

        answerGroup.setUser(answerUser);

        answerGroup = updateAnswerForm(answerGroup, prefabId);

        return answerGroup;
    }

    /**
     * Retrieves answer groups by prefab ID
     *
     * @param prefabId the ID of the prefab
     * @return the found AnswerGroup
     * @throws ResourceNotFoundException if no response is found for the provided IDs
     */
    public List<AnswerGroup> getAnswerGroupByPrefabId(String prefabId){
        List<AnswerGroup> answerGroup = answerRepository.findByPrefabId(prefabId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No response found for prefabId '%s'", prefabId)
                ));

        return answerGroup.stream().peek(group -> {
            String userId = group.getUserId();
            AnswerUser answerUser = new AnswerUser();

            answerUser.setName("Guest");
            answerUser.setEmail("");

            if (!Objects.equals(userId, "guest")) {
                User user = userService.getUserById(userId);
                if (user != null) {
                    answerUser.setName(String.format("%s %s", user.getFirstName(), user.getLastName()));
                    answerUser.setEmail(user.getEmail());
                }
            }

            group.setUser(answerUser);

        }).collect(Collectors.toList());
    }

    /**
     * Validates that a user has not already responded to the specified prefab.
     *
     * @param prefabId the ID of the prefab
     * @param userId   the ID of the user
     * @throws UniqueResponseException if a response with the same prefab ID and user ID already exists
     */
    public void validateUniqueUserResponse(String prefabId, String userId) throws UniqueResponseException {
        if (!Objects.equals(userId, "guest") && answerRepository.existsByPrefabIdAndUserId(prefabId, userId)) {
            throw new UniqueResponseException(
                    String.format("A response with prefabId '%s' and userId '%s' already exists.", prefabId, userId)
            );
        }
    }

    /**
     * Checks that the prefab is active and the number of answer groups matches the prefab's groups.
     *
     * @param prefabId the ID of the prefab
     * @param answerGroup  the answer request containing the answers
     * @throws ValidationException if the prefab is inactive or the number of groups does not match
     */
    public void checkFormPrefab(String prefabId, AnswerGroup answerGroup) throws ValidationException {
        Prefab prefab = prefabService.getPrefabById(prefabId, false);

        if(!prefab.getIsActive())
            throw new ValidationException("The prefab is inactive.");

        List<Group> prefabGroups = prefab.getGroups();
        List<AnswerSubGroup> answerGroups = answerGroup.getAnswers();

        if (prefabGroups.size() != answerGroups.size()) {
            throw new ValidationException("Number of groups in the prefab does not match the number of answer groups.");
        }

        for (int i = 0; i < prefabGroups.size(); i++) {
            checkAnswerGroup(prefabGroups.get(i), answerGroups.get(i), Integer.toString(i));
        }
    }

    /**
     * Checks that the answer group matches the prefab group.
     *
     * @param prefabGroup the prefab group to check against
     * @param answerGroup the answer group to validate
     * @param index      the index of the group in the list
     * @throws ValidationException if the groups do not match
     */
    public void checkAnswerGroup(Group prefabGroup, AnswerSubGroup answerGroup, String index) throws ValidationException {
        if (!prefabGroup.getName().equals(answerGroup.getGroup())) {
            throw new ValidationException(
                    String.format("Group index '%s': Prefab and Answer names don't match.\nPrefab: '%s', Answer: '%s'",
                            index, prefabGroup.getName(), answerGroup.getGroup())
            );
        }

        List<Field> fields = prefabGroup.getFields();
        List<AnswerQuestion> questions = answerGroup.getQuestions();

        if (fields.size() != questions.size()) {
            throw new ValidationException(String.format("Group index '%s': Mismatch in number of fields and questions.",
                    index));
        }

        for (int i = 0; i < fields.size(); i++) {
            checkAnswerField(fields.get(i), questions.get(i));
        }
    }

    /**
     * Validates the answer for a specific field.
     *
     * @param field    the field to validate against
     * @param question the answer question to validate
     * @throws ValidationException if validation fails
     */
    public void checkAnswerField(Field field, AnswerQuestion question) throws ValidationException {
        validateFieldAndQuestion(field.getLabel(), question.getQuestion());

        TypeField type = field.getType();
        Object answer = question.getAnswer();

        if (answer instanceof LinkedHashMap<?, ?> mapAnswer) {
            if (mapAnswer.isEmpty() && !field.getOptional()) {
                throw new ValidationException(String.format("Answer at the question '%s' is empty.",
                        question.getQuestion()));
            }
            if(mapAnswer.isEmpty())
                return;
        }

        System.out.print(answer);
        System.out.print(answer == "No answer");
        System.out.print("\n");

        if(answer == null && field.getOptional())
            return;

        if(answer == null && !field.getOptional())
            throw new ValidationException(String.format("Answer at the question '%s' is empty.",
                    question.getQuestion()));

        validateAnswerType(answer, type);
        try{ // Validate the field rules
            fieldService.validateFieldsRules(field, answer);
        }catch (ValidationRuleException e){
            throw new ValidationException(e.getMessage());
        }
    }

    /**
     * Validates that the field matches the question.
     *
     * @param field   the field name
     * @param question the question text
     * @throws ValidationException if the field and question do not match
     */
    private void validateFieldAndQuestion(String field, String question) {
        if (!Objects.equals(field, question)) {
            throw new ValidationException(String.format("Field mismatch: Field '%s' does not match question '%s'.",
                    field, question));
        }
    }

    /**
     * Validates the type of the answer against the expected type.
     *
     * @param answer the answer object to validate
     * @param type   the expected type of the field
     * @throws ValidationException if the answer does not match the expected type and the type is unsupported
     */
    private void validateAnswerType(Object answer, TypeField type) {
        if(type == TypeField.TEXT && !(answer instanceof String))
            throw new ValidationException(String.format("Answer '%s' is not a string", answer));
        if(type == TypeField.NUMBER)
                validateNumericAnswer(answer);
        if(type == TypeField.DATE)
                validateDateAnswer(answer);
        if(type == TypeField.GEOLOCATION)
                validateGeolocationAnswer(answer);

    }

    /**
     * Validates a numeric answer.
     *
     * @param answer the answer object to validate
     * @throws ValidationException if the answer is not a valid number
     */
    private void validateNumericAnswer(Object answer) {
        try {
            new BigDecimal(answer.toString());
        } catch (NumberFormatException e) {
            throw new ValidationException(String.format("Answer '%s' is not a valid number", answer));
        }
    }

    /**
     * Validates a date answer.
     *
     * @param answer the answer object to validate
     * @throws ValidationException if the answer is not a valid date
     */
    private void validateDateAnswer(Object answer) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse((String) answer, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException(String.format("Answer '%s' has not a valid format date", answer));
        }
    }

    /**
     * Validates a geolocation answer.
     *
     * @param answer the answer object to validate
     * @throws ValidationException if the answer is not a valid geolocation
     */
    private void validateGeolocationAnswer(Object answer) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode;

            if (answer instanceof String) {
                jsonNode = objectMapper.readTree((String) answer);
            } else {
                String jsonString = objectMapper.writeValueAsString(answer);
                jsonNode = objectMapper.readTree(jsonString);
            }

            if (jsonNode.has("lat") && jsonNode.has("lng")) {
                double lat = jsonNode.get("lat").asDouble();
                double lng = jsonNode.get("lng").asDouble();

                System.out.println("Latitude: " + lat);
                System.out.println("Longitude: " + lng);
            } else {
                throw new ValidationException("Geolocation answer must contain both 'lat' and 'lng' fields.");
            }

        } catch (Exception e) {
            throw new ValidationException(String.format("Answer '%s' is not a valid geolocation", answer));
        }
    }

    public AnswerGroup updateAnswerForm(AnswerGroup answerGroup, String prefabId){
        Prefab prefab = prefabService.getPrefabById(prefabId, false);
        Field field;
        TypeField type;
        int a = 0;
        int b;

        List<AnswerSubGroup> answerSubGroups = answerGroup.getAnswers();

        for(AnswerSubGroup answerSubGroup: answerSubGroups){
            b = 0;
            List<AnswerQuestion> answerQuestions = answerSubGroup.getQuestions();

            for(AnswerQuestion answerQuestion: answerQuestions){
                field = prefab.getGroups().get(a).getFields().get(b);
                type = field.getType();
                answerQuestion.setType(type);

                if(type == TypeField.MULTIPLE_CHOICE){

                    answerQuestion.setChoices(field.getOptions().getChoices().toArray(new String[0]));
                    if(field.getValidationRules().contains(TypeRule.IS_RADIO))
                        answerQuestion.setAnswer(new String[]{(String) answerQuestion.getAnswer()});
                }

                b++;
            }

            answerSubGroup.setQuestions(answerQuestions);
            a++;
        }
        answerGroup.setAnswers(answerSubGroups);

        return answerGroup;
    }
}
