package team.seventhmile.tripforp.external.ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.external.ai.dto.DateAndAreaRequest;
import team.seventhmile.tripforp.external.ai.service.AiService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    @GetMapping("/date-area")
    public ResponseEntity<Map<String, Object>> generationDateAndAreaResponse(
        @RequestParam(name = "startDate") @NotNull @FutureOrPresent LocalDate startDate,
        @RequestParam(name = "endDate") @NotNull @FutureOrPresent LocalDate endDate,
        @RequestParam(name = "area") @Pattern(regexp = "^(서울|경기|인천|강원|충북|충남|대전|경북|경남|대구|울산|부산|전북|전남|광주|제주|세종)$") String area) {
        try {
            DateAndAreaRequest request = new DateAndAreaRequest(startDate, endDate, area);
            return ResponseEntity.ok(aiService.generateV1(request));
        } catch (JsonProcessingException ex) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "JSON 파싱 실패");
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }

    }

    @GetMapping("/date-area2")
    public String generationDateAndAreaResponse2(
        @RequestParam(name = "startDate") @NotNull @FutureOrPresent LocalDate startDate,
        @RequestParam(name = "endDate") @NotNull @FutureOrPresent LocalDate endDate,
        @RequestParam(name = "area") @Pattern(regexp = "^(서울|경기|인천|강원|충북|충남|대전|경북|경남|대구|울산|부산|전북|전남|광주|제주|세종)$") String area) {
        return aiService.generation(
            aiService.getUserPromptV1(new DateAndAreaRequest(startDate, endDate, area)));
    }


}
