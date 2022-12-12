package uk.gov.hmcts.reform.juddata.camel.elinks.controller;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.elinks.controller.advice.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"AbbreviationAsWordInName","MemberName"})
class ErrorResponseTest {

    @Test
    void errorResponseResourceNotFound() {

        ErrorResponse errorResponse = new ErrorResponse(400,
                "NOT_FOUND","resource not found","resource not found",null);



        assertThat(errorResponse.getErrorCode()).isEqualTo(400);
        assertThat(errorResponse.getStatus()).isEqualTo("NOT_FOUND");
        assertThat(errorResponse.getErrorDescription()).isEqualTo("resource not found");
        assertThat(errorResponse.getErrorMessage()).isEqualTo("resource not found");
        assertThat(errorResponse.getTimeStamp()).isBlank();

    }
}
