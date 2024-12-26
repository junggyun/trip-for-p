package team.seventhmile.tripforp.external.google.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import team.seventhmile.tripforp.external.google.dto.DetailPlaceApiRequest;
import team.seventhmile.tripforp.external.google.dto.DetailPlaceResponse;
import team.seventhmile.tripforp.external.google.dto.PhotoPlaceResponse;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesApiRequest;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesResponse;
import team.seventhmile.tripforp.external.google.service.GoogleMapsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google-maps")
public class GoogleMapsController {

    private final GoogleMapsService googleMapsService;

    @GetMapping("/search")
    public ResponseEntity<Mono<SearchPlacesResponse>> searchPlaces(
        @ModelAttribute SearchPlacesApiRequest request
    ) {
        return ResponseEntity.ok(googleMapsService.searchPlacesApi(request));
    }

    @GetMapping("/detail")
    public ResponseEntity<Mono<DetailPlaceResponse>> detailPlace(
        @ModelAttribute DetailPlaceApiRequest request
    ) {
        return ResponseEntity.ok(googleMapsService.detailPlaceApi(request));
    }

    @GetMapping("/photo")
    public ResponseEntity<PhotoPlaceResponse> photoPlace(
        @ModelAttribute DetailPlaceApiRequest request
    ) {
        return ResponseEntity.ok(googleMapsService.photoPlaceApi(request));
    }


}
