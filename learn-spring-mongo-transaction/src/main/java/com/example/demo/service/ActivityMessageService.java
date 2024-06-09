package com.example.demo.service;

import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.ActivityMessage;
import com.example.demo.model.MessageCreateRequest;
import com.example.demo.repository.ActivityLogRepository;
import com.example.demo.repository.ActivityMessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityMessageService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityMessageRepository activityMessageRepository;
    private final ObjectMapper objectMapper;

    public ActivityMessage createMessage(MessageCreateRequest request) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(request);
        String id = UUID.randomUUID().toString();
        ActivityMessage saving = ActivityMessage.builder()
            .id(id)
            .data(message)
            .build();
        return activityMessageRepository.save(saving);
    }

    public List<ActivityMessage> getMessages() {
        return activityMessageRepository.findAll();
    }

    @Transactional
    public void saveMessageToActivityLog(String id, boolean fakeError) throws Exception {
        Optional<ActivityMessage> activityMessageOpt = activityMessageRepository.findById(id);
        if (activityMessageOpt.isEmpty()) {
            return;
        }

        ActivityMessage activityMessage = activityMessageOpt.get();
        String data = activityMessage.getData();
        ActivityLog activityLog = objectMapper.readValue(data, ActivityLog.class);
        activityLogRepository.save(activityLog);

        activityMessage.setProcessStatus("SUCCESS");
        activityMessageRepository.save(activityMessage);

        if (fakeError) {
            throw new IllegalStateException("Fake error to test @Transactional annotation.");
        }
    }

    public List<ActivityLog> getActivityLogs() {
        return activityLogRepository.findAll();
    }

    public void resetData() {
        activityLogRepository.deleteAll();
        activityMessageRepository.deleteAll();
    }
}
