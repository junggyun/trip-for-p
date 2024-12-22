package team.seventhmile.tripforp.domain.region.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.region.dto.CreateRegionRequest;
import team.seventhmile.tripforp.domain.region.entity.Province;
import team.seventhmile.tripforp.domain.region.entity.Region;
import team.seventhmile.tripforp.domain.region.repository.RegionRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository regionRepository;

    @Transactional
    public Long createRegion(CreateRegionRequest request) {
        Region region = Region.builder()
            .province(Province.findByName(request.getProvince()))
            .city(request.getCity())
            .build();
        regionRepository.save(region);

        return region.getId();
    }

    @Transactional
    public void deleteRegion(Long id) {
        Region region = regionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Region.class, id));
        regionRepository.delete(region);
    }

    public List<String> getProvinceList() {
        List<String> result = new ArrayList<>();
        for (Province province : Province.values()) {
            result.add(province.getName());
        }
        return result;
    }

    public List<String> getCityListByProvince(String province) {
        List<Region> regions = regionRepository.findByProvince(Province.findByName(province));
        List<String> result = new ArrayList<>();
        for (Region region : regions) {
            result.add(region.getCity());
        }
        return result;
    }

}
