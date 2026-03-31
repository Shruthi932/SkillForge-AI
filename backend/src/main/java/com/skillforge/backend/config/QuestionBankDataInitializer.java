package com.skillforge.backend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillforge.backend.entity.QuestionBankItem;
import com.skillforge.backend.repository.QuestionBankRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

@Configuration
public class QuestionBankDataInitializer {

    @Bean
    CommandLineRunner seedQuestionBank(QuestionBankRepository questionBankRepository, ObjectMapper objectMapper) {
        return args -> {
            if (questionBankRepository.count() > 0) {
                return;
            }

            ClassPathResource resource = new ClassPathResource("question-bank.json");
            try (InputStream inputStream = resource.getInputStream()) {
                List<QuestionSeedItem> seedItems = objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<QuestionSeedItem>>() {}
                );

                List<QuestionBankItem> items = seedItems.stream()
                        .map(this::toEntity)
                        .toList();

                questionBankRepository.saveAll(items);
            }
        };
    }

    private QuestionBankItem toEntity(QuestionSeedItem seedItem) {
        QuestionBankItem item = new QuestionBankItem();
        item.setTopic(seedItem.topic().trim());
        item.setDifficulty(seedItem.difficulty().trim());
        item.setQuestionText(seedItem.questionText().trim());
        item.setUsageCount(seedItem.usageCount());
        return item;
    }

    private record QuestionSeedItem(String topic, String difficulty, String questionText, Integer usageCount) {
    }
}
