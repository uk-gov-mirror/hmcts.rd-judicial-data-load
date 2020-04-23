package uk.gov.hmcts.reform.juddata.camel.binder;

import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@CsvRecord(separator = ",", crlf = "UNIX", skipFirstLine = true)
public class JudicialRegionType {

    @DataField(pos = 1, columnName = "region_id")
    @NotBlank
    String regionId;

    @DataField(pos = 2, columnName = "region_desc_en")
    @NotBlank
    String regionDescEn;

    @DataField(pos = 3, columnName = "region_desc_cy")
    String regionDescCy;

}
