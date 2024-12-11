package team.seventhmile.tripforp.external.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import team.seventhmile.tripforp.external.ai.dto.DateAndAreaRequest;
import team.seventhmile.tripforp.external.weather.dto.WeatherDto;
import team.seventhmile.tripforp.external.weather.service.WeatherService;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final WeatherService weatherService;

    public Map<String, Object> generateV1(DateAndAreaRequest request)
        throws JsonProcessingException {
        String response = generation(getUserPromptV1(request));
        return parseJsonResponse(response);
    }

    public String generation(String userInput) {
        return this.chatClient.prompt()
            .system(getSystemPrompt())
            .user(userInput)
            .call()
            .content();
    }

    public String getUserPromptV1(DateAndAreaRequest request) {
        List<WeatherDto> data = weatherService.getWeather(request);
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 지역명과 날씨 데이터를 기반으로 여행 계획을 추천해주세요:\n")
            .append("지역: " + request.getArea() + "\n")
            .append("[날씨 데이터 JSON]\n");
        for (WeatherDto weather : data) {
            prompt.append("날짜 및 시간: ").append(weather.getDatetime()).append("\n")
                .append("기온: ").append(weather.getTemperature()).append("\n")
                .append("습도: ").append(weather.getHumidity()).append("\n")
                .append("상태: ").append(weather.getStatus()).append("\n")
                .append("구름: ").append(weather.getCloud()).append("\n")
                .append("풍속: ").append(weather.getWind()).append("\n")
                .append("강수확률: ").append(weather.getPop()).append("\n")
                .append("예상 강수량-3시간: ").append(weather.getRain()).append("\n")
                .append("예상 강설량-3시간: ").append(weather.getSnow()).append("\n")
                .append("====================\n");
        }
        prompt.append("다음 사항들을 고려한 일정을 추천해주세요:\n"
            + "1. 지역을 행정 구역으로 알려줄텐데 해당 행정구역의 도시(예:전남-여수, 강원-강릉) 중에 현재 계절에 인기 있는 지역으로 여행 코스를 추천\n"
            + "2. 응답의 지역 필드는 행정구역명에서 도시명을 설정\n"
            + "3  날짜별로 나누어 응답\n"
            + "4. 여행 기간 동안의 날씨를 전체적으로 분석하여 최대한 중복되는 코스가 없도록 추천(카페는 예외)\n"
            + "5. 날씨를 고려한 실내/실외 활동 배분\n"
            + "6. 체력 소모를 고려한 일정 구성\n"
            + "7. 추천 장소는 맛집(카페 포함) 1곳, 활동(관광지, 체험활동, 축제 등) 1곳을 포함\n"
            + "8. 맛집과 활동지는 중복 불가\n"
            + "9. 맛집의 장소명은 메뉴가 아닌 정확한 음식점으로 표기\n"
            + "10. 장소 정보에는 장소명,설명, 링크를 표기\n"
            + "11. 링크는 네이버 맵에 장소명을 띄어쓰기 없이 검색한 링크를 제공\n"
            + "12. 장소 정보의 설명에는 장소설명 및 날씨 또는 여행 흐름에 따라 선정한 근거를 표기");

        return prompt.toString();
    }

    private String getSystemPrompt() {
        return "당신은 한국의 지역별 여행 계획을 JSON 형식으로 제공하는 여행 플래너 AI입니다. 모든 응답은 정형화된 JSON 구조로 제공하며, 날씨 데이터를 분석하여 최적화된 여행 일정을 구성합니다.\n"
            + "\n"
            + "응답은 다음의 JSON 스키마를 따릅니다:\n"
            + "```json\n"
            + "{\n"
            + "  \"지역\": \"string\",\n"
            + "  \"날씨_요약\": \"string\",\n"
            + "  \"일정\": [\n"
            + "    {\n"
            + "      \"날짜\": \"string (YYYY-MM-DD)\",\n"
            + "      \"아침\": {\n"
            + "        \"시간\": \"09:00-12:00\",\n"
            + "        \"날씨\": {\n"
            + "          \"기온\": \"string\",\n"
            + "          \"상태\": \"string\",\n"
            + "          \"강수_확률\": \"string\"\n"
            + "        },\n"
            + "        \"추천\": {\n"
            + "          \"맛집\": {\n"
            + "            \"장소명\": \"string\",\n"
            + "            \"설명\": \"string\",\n"
            + "            \"링크\": \"string\"\n"
            + "          },\n"
            + "          \"활동\": {\n"
            + "            \"장소명\": \"string\",\n"
            + "            \"설명\": \"string\",\n"
            + "            \"링크\": \"string\"\n"
            + "          }\n"
            + "        }\n"
            + "      },\n"
            + "      \"점심\": {\n"
            + "        \"시간\": \"12:00-18:00\",\n"
            + "        \"날씨\": {\n"
            + "          \"기온\": \"string\",\n"
            + "          \"상태\": \"string\",\n"
            + "          \"강수_확률\": \"string\"\n"
            + "        },\n"
            + "        \"추천\": {\n"
            + "          \"맛집\": {\n"
            + "            \"장소명\": \"string\",\n"
            + "            \"설명\": \"string\",\n"
            + "            \"링크\": \"string\"\n"
            + "          },\n"
            + "          \"활동\": {\n"
            + "            \"장소명\": \"string\",\n"
            + "            \"설명\": \"string\",\n"
            + "            \"링크\": \"string\"\n"
            + "          }\n"
            + "        }\n"
            + "      },\n"
            + "      \"저녁\": {\n"
            + "        \"시간\": \"18:00-21:00\",\n"
            + "        \"날씨\": {\n"
            + "          \"기온\": \"string\",\n"
            + "          \"상태\": \"string\",\n"
            + "          \"강수_확률\": \"string\"\n"
            + "        },\n"
            + "        \"추천\": {\n"
            + "          \"맛집\": {\n"
            + "            \"장소명\": \"string\",\n"
            + "            \"설명\": \"string\",\n"
            + "            \"링크\": \"string\"\n"
            + "          },\n"
            + "          \"활동\": {\n"
            + "            \"장소명\": \"string\",\n"
            + "            \"설명\": \"string\",\n"
            + "            \"링크\": \"string\"\n"
            + "          }\n"
            + "        }\n"
            + "      },\n"
            + "      \"밤\": {\n"
            + "        \"시간\": \"21:00-24:00\",\n"
            + "        \"날씨\": {\n"
            + "          \"기온\": \"string\",\n"
            + "          \"상태\": \"string\",\n"
            + "          \"강수_확률\": \"string\"\n"
            + "        },\n"
            + "        \"추천\": {\n"
            + "          \"맛집\": {\n"
            + "            \"장소명\": \"string\",\n"
            + "            \"설명\": \"string\",\n"
            + "            \"링크\": \"string\"\n"
            + "          },\n"
            + "          \"활동\": {\n"
            + "            \"장소명\": \"string\",\n"
            + "            \"설명\": \"string\",\n"
            + "            \"링크\": \"string\"\n"
            + "          }\n"
            + "        }\n"
            + "      }\n"
            + "    }\n"
            + "  ]\n"
            + "}\n"
            + "```";
    }

//    private String getSystemPrompt() {
//        return "당신은 날씨 데이터를 기반으로 최적의 여행 계획을 추천해주는 여행 플래너 AI입니다. 다음과 같은 원칙을 따릅니다:\n"
//            + "\n"
//            + "1. 날씨 데이터를 분석하여 시간대별로 적합한 활동을 추천합니다.\n"
//            + "2. 각 시간대의 기온, 습도, 날씨 상태를 고려하여 실내/실외 활동을 적절히 분배합니다.\n"
//            + "3. 강수 확률이 높을 때는 실내 활동을 우선적으로 추천합니다.\n"
//            + "4. 아침(09:00-12:00), 점심(12:00-18:00), 저녁(18:00-21:00), 밤(21:00-06:00) 시간대별로 구분하여 추천합니다.\n"
//            + "5. 지역의 특색있는 관광지, 맛집, 문화 활동을 포함합니다.\n"
//            + "6. 추천 시 다음 요소들을 고려합니다:\n"
//            + "   - 기온에 따른 적절한 야외 활동 가능 여부\n"
//            + "   - 습도와 바람에 따른 체감 온도\n"
//            + "   - 구름량에 따른 야외 활동의 적합성\n"
//            + "   - 시간대별 일반적인 관광 패턴\n"
//            + "\n"
//            + "응답은 JSON 형식으로 제공하며, 각 시간대별로 구체적인 활동 추천과 그 이유를 포함합니다.";
//    }


    private Map<String, Object> parseJsonResponse(String response) throws JsonProcessingException {
        response = response.replace("```json", "");
        response = response.replace("```", "");
        return objectMapper.readValue(response, Map.class);
    }

}
