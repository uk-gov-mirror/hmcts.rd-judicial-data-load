package uk.gov.hmcts.reform.juddata.camel.email;

import static org.apache.camel.Exchange.EXCEPTION_CAUGHT;
import static org.apache.camel.Exchange.FILE_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import javax.mail.internet.MimeMessage;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSender;
import uk.gov.hmcts.reform.juddata.camel.exception.EmailFailureException;
import uk.gov.hmcts.reform.juddata.camel.service.EmailService;

public class EmailServiceTest extends CamelTestSupport {

    @InjectMocks
    EmailService emailService;

    @Mock
    JavaMailSender mailSender;
    String mailFrom;
    String mailTo;
    String mailsubject;
    String messageBody;
    String filename;
    Boolean mailEnabled = true;
    @Mock
    private MimeMessage mimeMessage;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockData();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    }

    private void mockData() {
        mailFrom = "no-reply@reform.hmcts.net";
        mailTo = "example1@hmcts.net,example2@hmcts.net";
        mailsubject = "Test mail";
        messageBody = "Test";
        filename = "File1.csv";
        mailEnabled = true;
        setField(emailService, "mailFrom", mailFrom);
        setField(emailService, "mailTo", mailTo);
        setField(emailService, "mailsubject", mailsubject);
        setField(emailService, "mailEnabled", Boolean.TRUE);
    }

    @Test
    public void sendEmail() {
        doNothing().when(mailSender).send(any(MimeMessage.class));
        emailService.sendEmail(messageBody, filename);
        assertEquals("Test", messageBody);
        assertEquals("File1.csv", filename);

    }


    @Test(expected = EmailFailureException.class)
    public void sendEmailException() {

        EmailFailureException emailFailureException = new EmailFailureException(new Throwable());

        doThrow(emailFailureException).when(mailSender).send(any(MimeMessage.class));

        emailService.sendEmail("Test", "File1.csv");
    }

    @Test
    public void process() throws Exception {
        CamelContext context = new DefaultCamelContext();
        Exchange exchange = createExchangeWithBody(context, "body");
        exchange.setProperty(EXCEPTION_CAUGHT, new Exception("Test Message"));
        exchange.setProperty(FILE_NAME, filename);
        emailService.process(exchange);
        assertEquals("no-reply@reform.hmcts.net", mailFrom);
        assertEquals("example1@hmcts.net,example2@hmcts.net", mailTo);
        assertEquals("Test mail", mailsubject);
        assertTrue(mailEnabled);
    }
}