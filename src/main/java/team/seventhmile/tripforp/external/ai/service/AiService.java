package team.seventhmile.tripforp.external.ai.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;
    @Value("${gemini.api.key}")
    private String key;
    @Value("${gemini.api.url}")
    private String url;

    public String generateContent(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", key);

        prompt = generatePromptV2(prompt);

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class
            );

            return parseGeminiResponse(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Gemini API", e);
        }
    }

    private String generatePromptV1(String prompt) {
        return "주어진 장소들의 위치와 특성을 고려하여 효율적이고 합리적인 여행 코스를 만들어주세요. 각 장소의 정보는 [id, 위도, 경도, 카테고리] 형식으로 제공됩니다:\n"
            + prompt + "\n"
            + "다음 조건을 고려해서 최적의 코스를 추천해주세요:\n"
            + "\n"
            + "1. 이동 거리와 시간을 최소화\n"
            + "2. 카테고리 순서가 자연스러워야 함 (예: 관광지 -> 식사 -> 카페)\n"
            + "3. 전체 동선이 효율적이어야 함\n"
            + "4. 코스를 id값만 쉼표로 구분하여 응답해야함.\n"
            + "5. 다른 설명은 포함하면 안됨.";
    }

    private String generatePromptV2(String prompt) {
        return "주어진 장소들의 위치와 특성을 고려하여 효율적이고 합리적인 여행 코스를 만들어주세요. 각 장소의 정보는 [id, 장소명, 도로명주소, 카테고리] 형식으로 제공됩니다:\n"
            + prompt + "\n"
            + "다음 조건을 고려해서 최적의 코스를 추천해주세요:\n"
            + "\n"
            + "1. 장소 간의 거리를 최우선적으로 고려해야 함\n"
            + "2. 비슷한 범위 내에서는 카테고리 순서를 적절히 배치하는 것을 고려해야 함\n"
            + "3. 하루 일정 기준으로 개인 선호도는 특별히 고려하지 않고 트렌디한 코스로 제안해야함"
            + "4. 코스를 id값만 쉼표로 구분하여 응답해야함.\n"
            + "5. 다른 설명은 포함하면 안됨.";
    }

    private String parseGeminiResponse(Map<String, Object> responseBody) {
        // Gemini API 응답 구조에 맞게 파싱
        try {
            String jsonString = new JSONObject(responseBody).toString();
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);

            JSONArray candidates = (JSONArray) json.get("candidates");
            JSONObject firstCandidate = (JSONObject) candidates.get(0);

            JSONObject content = (JSONObject) firstCandidate.get("content");

            JSONArray parts = (JSONArray) content.get("parts");
            JSONObject firstPart = (JSONObject) parts.get(0);

            return (String) firstPart.get("text");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }

}
