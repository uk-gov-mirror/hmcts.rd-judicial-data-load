package uk.gov.hmcts.reform.juddata.camel.elinks.domain;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.elinks.domain.Location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.juddata.camel.elinks.TestSupport.createRegionType;

class RegionTypeTest {

    @Test
    void testRegionType() {
        Location regionType = createRegionType();

        assertNotNull(regionType);
        assertEquals("0", regionType.getRegionId());
        assertEquals("default", regionType.getRegionDescEn());
        assertEquals("default", regionType.getRegionDescCy());
    }
}