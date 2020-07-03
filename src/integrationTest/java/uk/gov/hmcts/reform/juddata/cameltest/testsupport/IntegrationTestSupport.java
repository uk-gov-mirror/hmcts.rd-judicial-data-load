package uk.gov.hmcts.reform.juddata.cameltest.testsupport;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.reform.juddata.camel.binder.JudicialOfficeAuthorisation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface IntegrationTestSupport {

    String COMMA = ",";
    String BLANK = "";

    static void setSourcePath(String path, String propertyPlaceHolder) throws Exception {

        String loadFile = ResourceUtils.getFile(path).getCanonicalPath();

        if (loadFile.endsWith(".csv")) {
            int lastSlash = loadFile.lastIndexOf("/");
            String result = loadFile.substring(0, lastSlash);
            String fileName = loadFile.substring(lastSlash + 1);

            System.setProperty(propertyPlaceHolder, "file:"
                    + result + "?fileName=" + fileName + "&noop=true");
        } else {
            System.setProperty(propertyPlaceHolder, "file:" + loadFile.replaceFirst("/", ""));
        }
    }

    static List<JudicialOfficeAuthorisation> getFileAuthorisationObjectsFromCsv(String inputFilePath)  {
        List<JudicialOfficeAuthorisation> authorisations = new LinkedList<>();
        try{
            File inputF = ResourceUtils.getFile(inputFilePath);
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputFS));
            authorisations = bufferedReader.lines().skip(1).map(line -> mapJudicialOfficeAuthorisation(line)).collect(Collectors.toList());
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return authorisations ;
    }

    static JudicialOfficeAuthorisation mapJudicialOfficeAuthorisation(String line) {
        List<String> columns = Arrays.asList(line.split("\\,", -1));
        JudicialOfficeAuthorisation judicialOfficeAuthorisation = new JudicialOfficeAuthorisation();
        judicialOfficeAuthorisation.setElinksId(handleNull(columns.get(0), false));
        judicialOfficeAuthorisation.setJurisdiction(handleNull(columns.get(1), false));
        judicialOfficeAuthorisation.setTicketId(isBlank(columns.get(2)) ? null : Long.parseLong(columns.get(2)));
        judicialOfficeAuthorisation.setStartDate(handleNull(columns.get(3), true));
        judicialOfficeAuthorisation.setEndDate(handleNull(columns.get(4), true));
        judicialOfficeAuthorisation.setCreatedDate(handleNull(columns.get(5), true));
        judicialOfficeAuthorisation.setLastUpdated(handleNull(columns.get(6), true));
        judicialOfficeAuthorisation.setLowerLevel(handleNull(columns.get(7), false));
        return judicialOfficeAuthorisation;
    }

    static String handleNull(String fieldValue, boolean timeStampField) {
        if (isBlank(fieldValue) && !timeStampField) {
            return BLANK;
        } else if (timeStampField && isBlank(fieldValue)){
            return null;
        } else if (timeStampField && !isBlank(fieldValue)) {
            return Timestamp.valueOf(fieldValue).toString();
        } else {
            return fieldValue;
        }
    }

    static String handleNull(Timestamp timestamp) {
        if (Objects.isNull(timestamp)) {
            return null;
        } else {
            return String.valueOf(timestamp);
        }
    }
}
