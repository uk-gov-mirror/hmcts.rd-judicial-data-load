package uk.gov.hmcts.reform.elinks.exception;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.INTERNAL_SERVER_ERROR;

@SuppressWarnings({"AbbreviationAsWordInName","MemberName"})
public class ElinksExceptionTest {

    @Test
    void elinksExceptionInternalServerError() {

        ElinksException elinksException = new ElinksException(
                HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);

        assertThat(elinksException.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(elinksException.getErrorDescription()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(elinksException.getErrorMessage()).isEqualTo(INTERNAL_SERVER_ERROR);

    }
}
