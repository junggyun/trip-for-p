package team.seventhmile.tripforp.external.google.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesApiRequest;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesRequest;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesResponse;
import team.seventhmile.tripforp.external.google.service.GoogleMapsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google-maps")
public class GoogleMapsController {

    private final GoogleMapsService googleMapsService;

    @PostMapping("/search")
    public Mono<SearchPlacesResponse> searchPlaces(
        @RequestBody SearchPlacesRequest request
    ) {
        return googleMapsService.searchPlacesApi(request);
    }
}
