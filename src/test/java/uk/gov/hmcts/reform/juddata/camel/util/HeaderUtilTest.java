package uk.gov.hmcts.reform.juddata.camel.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import uk.gov.hmcts.reform.juddata.camel.binder.JudicialUserProfile;
import uk.gov.hmcts.reform.juddata.camel.route.beans.RouteProperties;

public class HeaderUtilTest {
    @Mock
    ApplicationContext applicationContext;
    @Mock
    CamelContext camelContext;
    @Mock
    PlatformTransactionManager platformTransactionManager;
    @Mock
    JdbcTemplate jdbcTemplate;

    @Autowired
    HeaderUtil headerUtil;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    //@Test
    public void testCheckHeader() throws Exception {
        HeaderUtil.checkHeader(null, new RouteProperties(), "exceptionMsg");
    }

    @Test
    public void testGetJrdHeaderValue() throws Exception {
        String ss="elinks_id,Personal_Code,Title,Known_As,Surname,Full_Name,Post_Nominals,Contract_Type_Id,Work_Pattern,Email_Id,Joining_Date,Last_Working_Date,Active_Flag,Extracted_Date";
        List<String> headers= Arrays.asList(ss.split(","));
        String result = headerUtil.getInvalidJrdHeader(JudicialUserProfile.class, headers , "judicialUserProfile");

    }

    @Test
    public void testGetJrdHeaderValueMoreHeader() throws Exception {
        String ss="dfads,elinks_id,Personal_Code,Title,Known_As,Surname,Full_Name,Post_Nominals,Contract_Type_Id,Work_Pattern,Email_Id,Joining_Date,Last_Working_Date,Active_Flag,Extracted_Date";
        List<String> headers= Arrays.asList(ss.split(","));
        String result = headerUtil.getInvalidJrdHeader(JudicialUserProfile.class, headers, "judicialUserProfile");

    }

    @Test
    public void testGetJrdHeaderValueLessHeader() throws Exception {
        String ss="Personal_Code,Title,Known_As,Surname,Full_Name,Post_Nominals,Contract_Type_Id,Work_Pattern,Email_Id,Joining_Date,Last_Working_Date,Active_Flag,Extracted_Date";
        List<String> headers= Arrays.asList(ss.split(","));
        String result = headerUtil.getInvalidJrdHeader(JudicialUserProfile.class, headers, "judicialUserProfile");

    }

    @Test
    public void testGetJrdHeaderValueDifferentOrdee() throws Exception {
        String ss="Personal_Code,elinks_id,Title,Known_As,Surname,Full_Name,Post_Nominals,Contract_Type_Id,Work_Pattern,Email_Id,Joining_Date,Last_Working_Date,Active_Flag,Extracted_Date";
        List<String> headers= Arrays.asList(ss.split(","));
        String result = headerUtil.getInvalidJrdHeader(JudicialUserProfile.class, headers, "judicialUserProfile");

    }

    @Test
    public void testreadYmlAsMap() throws Exception {
        Map readYmlAsMap=HeaderUtil.readYmlAsMap("header-mapping.yaml");
        String judicialUserProfile= (String) readYmlAsMap.get("judicialUserProfile");
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme