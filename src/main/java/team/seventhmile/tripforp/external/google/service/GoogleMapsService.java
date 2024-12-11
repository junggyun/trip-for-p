package team.seventhmile.tripforp.external.google.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import team.seventhmile.tripforp.external.google.dto.GoogleMapsPlaceDto;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesApiRequest;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesApiResponse;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesRequest;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleMapsService {

    private final WebClient webClient;

    public Mono<SearchPlacesResponse> searchPlacesApi(SearchPlacesRequest request) {
        SearchPlacesApiRequest body = SearchPlacesApiRequest.builder()
            .textQuery(request.getTextQuery())
            .languageCode("ko")
            .regionCode("kr")
            .pageSize(request.getPageSize())
            .pageToken(request.getPageToken())
            .build();
        return webClient.post()
            .uri(":searchText")
            .headers(headers -> {
                headers.add("X-Goog-FieldMask",
                    "places.id,places.displayName.text,places.formattedAddress,places.rating,places.userRatingCount,places.primaryTypeDisplayName,nextPageToken");
            })
            .bodyValue(body)
            .retrieve()
            .bodyToMono(SearchPlacesApiResponse.class)
            .map(response -> {
                List<GoogleMapsPlaceDto> places = response.getPlaces().stream()
                    .map(GoogleMapsPlaceDto::new)
                    .toList();
                return SearchPlacesResponse.builder()
                    .places(places)
                    .nextPageToken(response.getNextPageToken())
                    .build();
            });
    }

}
