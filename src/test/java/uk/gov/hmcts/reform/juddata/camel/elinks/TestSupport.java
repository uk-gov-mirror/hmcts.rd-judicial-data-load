package uk.gov.hmcts.reform.juddata.camel.elinks;

import uk.gov.hmcts.reform.elinks.domain.RegionType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

public class TestSupport {

    private static final LocalDate date = LocalDate.now();
    private static final LocalDateTime dateTime = LocalDateTime.now();

    private TestSupport() {

    }

    public static RegionType createRegionType() {
        RegionType regionType = new RegionType();
        regionType.setRegionId("0");
        regionType.setRegionDescEn("default");
        regionType.setRegionDescCy("default");

        return regionType;
    }
}
