package uk.gov.hmcts.reform.juddata.camel.elinks.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient1;
import uk.gov.hmcts.reform.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.elinks.service.impl.ELinksServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ELinksServiceImplTest {

    @InjectMocks
    ELinksServiceImpl eLinksServiceImpl;

    @Spy
    BaseLocationRepository baseLocationRepository;

    @Spy
    LocationRepository LocationRepository;



    @Spy
    ElinksFeignClient1 elinksFeignClient;


    @Test
    void testLoadLocation() {

    }
}
