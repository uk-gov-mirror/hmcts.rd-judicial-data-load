package uk.gov.hmcts.reform.juddata.camel.elinks.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.elinks.domain.Location;
import uk.gov.hmcts.reform.elinks.repository.LocationRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationRepositoryTest {

    @Mock
    private LocationRepository locationRepository;


    @Test
    void saveRegionTypeTest() {
        Location regionType = new Location();
        when(locationRepository.save(any())).thenReturn(regionType);

        Location rgionTypeResult = locationRepository.save(regionType);

        assertNotNull(rgionTypeResult);
    }

}
