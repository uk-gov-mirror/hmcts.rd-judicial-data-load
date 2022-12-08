package uk.gov.hmcts.reform.juddata.camel.elinks;

import uk.gov.hmcts.reform.elinks.domain.Location;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestSupport {

    private static final LocalDate date = LocalDate.now();
    private static final LocalDateTime dateTime = LocalDateTime.now();

    private TestSupport() {

    }

    public static Location createRegionType() {
        Location regionType = new Location();
        regionType.setRegionId("0");
        regionType.setRegionDescEn("default");
        regionType.setRegionDescCy("default");

        return regionType;
    }
}
