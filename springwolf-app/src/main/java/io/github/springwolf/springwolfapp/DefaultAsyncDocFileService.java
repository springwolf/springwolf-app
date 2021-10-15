package io.github.springwolf.springwolfapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class DefaultAsyncDocFileService implements AsyncDocFileService {

    private static final Logger log = LoggerFactory.getLogger(DefaultAsyncDocFileService.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> docTypeRef = new TypeReference<>() {};
    private final Map<String, Map<String, Object>> docs = new HashMap<>();

    @Value("${files-dir}")
    private String dirName;

    @PostConstruct
    private void initialize() {
        getFilesNames().forEach(this::addDoc);
    }

    @SuppressWarnings("unchecked")
    private void addDoc(String fileName) {
        var fullPath = Path.of(dirName + "/" + fileName);

        try {
            String contents = Files.readString(fullPath);
            var asMap = mapper.readValue(contents, docTypeRef);

            String title = Optional.ofNullable(asMap.get("info"))
                    .filter(info -> info instanceof Map)
                    .map(info -> (Map<String, Object>) info)
                    .map(info -> (String) info.get("title"))
                    .orElse(fileName);

            docs.put(title, asMap);
        } catch (IOException e) {
            log.error("Failed to load {}", fileName);
        }
    }

    private List<String> getFilesNames() {
        File[] files = new File(dirName).listFiles();

        if (files == null) {
            return Collections.emptyList();
        }

        return Stream.of(files)
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .filter(s -> s.endsWith(".json"))
                .collect(toList());
    }

    @Override
    public Map<String, Map<String, Object>> getAsyncApiFiles() {
        return docs;
    }

}
