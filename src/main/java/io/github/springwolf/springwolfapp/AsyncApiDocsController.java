package io.github.springwolf.springwolfapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/asyncapi")
public class AsyncApiDocsController {

    @Autowired
    private AsyncDocFileService asyncDocFileService;

    @GetMapping(value = "/docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Map<String, Object>> asyncApis() {
        return asyncDocFileService.getAsyncApiFiles();
    }

}
