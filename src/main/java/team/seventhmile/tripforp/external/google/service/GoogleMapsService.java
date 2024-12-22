package team.seventhmile.tripforp.external.google.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import team.seventhmile.tripforp.external.google.dto.DetailPlaceApiRequest;
import team.seventhmile.tripforp.external.google.dto.DetailPlaceResponse;
import team.seventhmile.tripforp.external.google.dto.GoogleMapsPlaceApiDto;
import team.seventhmile.tripforp.external.google.dto.SearchPlaceResponse;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesApiRequest;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesApiResponse;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleMapsService {

    private final WebClient webClient;

    public Mono<SearchPlacesResponse> searchPlacesApi(SearchPlacesApiRequest request) {

        return webClient.post()
            .uri(":searchText")
            .headers(headers -> {
                headers.add("X-Goog-FieldMask",
                    "places.id,places.displayName.text,places.formattedAddress,places.rating,places.userRatingCount,places.primaryTypeDisplayName,places.location,nextPageToken");
            })
            .bodyValue(request)
            .retrieve()
            .bodyToMono(SearchPlacesApiResponse.class)
            .map(response -> {
                List<SearchPlaceResponse> places = response.getPlaces().stream()
                    .map(SearchPlaceResponse::new)
                    .toList();
                return SearchPlacesResponse.builder()
                    .places(places)
                    .nextPageToken(response.getNextPageToken())
                    .build();
            });
    }

    public Mono<DetailPlaceResponse> detailPlaceApi(DetailPlaceApiRequest request) {

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/" + request.getId())
                .queryParam("languageCode", request.getLanguageCode())
                .queryParam("regionCode", request.getRegionCode())
                .build())
            .headers(headers -> {
                headers.add("X-Goog-FieldMask",
                    "id,location,displayName,formattedAddress,primaryTypeDisplayName,rating,userRatingCount");
            })
            .retrieve()
            .bodyToMono(GoogleMapsPlaceApiDto.class)
            .map(DetailPlaceResponse::new);
    }
}
