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
public class JudicialUserRoleType {

    @DataField(pos = 1, columnName = "role_id")
    @NotBlank
    String roleId;

    @DataField(pos = 2, columnName = "role_desc_en")
    @NotBlank
    String roleDescEn;

    @DataField(pos = 3, columnName = "role_desc_cy")
    String roleDescCy;

}
