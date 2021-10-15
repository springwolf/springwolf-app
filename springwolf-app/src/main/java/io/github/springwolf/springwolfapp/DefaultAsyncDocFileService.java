package io.github.springwolf.springwolfapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DefaultAsyncDocFileService implements AsyncDocFileService {

    private static final ObjectMapper mapper = new ObjectMapper();
    private Map<String, Map<String, Object>> docs;

    @Value("${files-dir}")
    private String dirName;

    @PostConstruct
    private void initialize() {
        var typeRef = new TypeReference<Map<String, Object>>() {
        };

        File[] files = new File(dirName).listFiles();

        if (files == null) {
            return;
        }



        docs = Stream.of(files)
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .filter(s -> s.endsWith(".json"))
                .map(file -> dirName + "/" + file)
                .map(Path::of)
                .map(this::readToString)
                .filter(Objects::nonNull)
                .map(s -> mapToMap(typeRef, s))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(this::getTitle, s -> s));
    }

    private String getTitle(Map<String, Object> map) {
        Map info = (Map) map.get("info");
        return (String) info.get("title");
    }

    private Map<String, Object> mapToMap(TypeReference<Map<String, Object>> typeRef, String s) {
        try {
            return mapper.readValue(s, typeRef);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String readToString(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, Map<String, Object>> getAsyncApiFiles() {
        return docs;
    }

}
