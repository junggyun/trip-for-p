package team.seventhmile.tripforp.external.ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.external.ai.service.AiService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    @PostMapping
    public ResponseEntity<String> generateContent(
        @RequestBody String prompt
    ) {
        return ResponseEntity.ok(aiService.generateContent(prompt));
    }
}
