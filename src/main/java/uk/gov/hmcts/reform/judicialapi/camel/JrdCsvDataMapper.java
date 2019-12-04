package uk.gov.hmcts.reform.judicialapi.camel;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",", crlf = "UNIX", skipFirstLine = true)
public class JrdCsvDataMapper {

    @DataField(pos = 1)
    String sno;
    @DataField(pos = 2)
    String firstName;
    @DataField(pos = 3)
    String lastName;
    @DataField(pos = 4)
    String circuit;
    @DataField(pos = 5)
    String area;
}

