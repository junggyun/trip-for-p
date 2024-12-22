package team.seventhmile.tripforp.domain.region.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.region.dto.CreateRegionRequest;
import team.seventhmile.tripforp.domain.region.service.RegionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;

    @PostMapping
    public ResponseEntity<?> createRegion(
        @RequestBody CreateRegionRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(regionService.createRegion(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegion(
        @PathVariable(name = "id") Long id
    ) {
        regionService.deleteRegion(id);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @GetMapping("/provinces")
    public ResponseEntity<List<String>> getProvinceList() {
        return ResponseEntity
            .ok(regionService.getProvinceList());
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCityListByProvince(
        String province
    ) {
        return ResponseEntity
            .ok(regionService.getCityListByProvince(province));
    }
}
