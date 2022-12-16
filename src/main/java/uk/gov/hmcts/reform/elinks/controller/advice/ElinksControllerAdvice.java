package uk.gov.hmcts.reform.elinks.controller.advice;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.reform.elinks.exception.ElinksException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@ControllerAdvice(basePackages = "uk.gov.hmcts.reform.elinks")
@RequestMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)

public class ElinksControllerAdvice {


    private static final String LOG_STRING = "handling exception: {}";


    @ExceptionHandler(ElinksException.class)
    protected ResponseEntity<Object> handleElinksException(
            HttpServletRequest request,
            ElinksException e
    ) {
        return errorDetailsResponseEntity(e, e.getStatus(), e.getErrorMessage());
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> customSerializationError(
            HttpMessageNotReadableException ex) {

        String field = "";
        if (ex.getCause() != null) {
            JsonMappingException jme = (JsonMappingException) ex.getCause();
            field = jme.getPath().get(0).getFieldName();
        }
        ErrorResponse errorDetails = ErrorResponse.builder()
                .errorMessage(BAD_REQUEST.getReasonPhrase())
                .errorDescription(field + " in invalid format")
                .timeStamp(getTimeStamp())
                .build();

        return new ResponseEntity<>(errorDetails, BAD_REQUEST);
    }


    @ExceptionHandler(ForbiddenException.class)
    protected ResponseEntity<Object> handleForbiddenException(
            HttpServletRequest request,
            ForbiddenException e
    ) {
        return errorDetailsResponseEntity(e, FORBIDDEN, e.getMessage());
    }


    public String getTimeStamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.ENGLISH).format(new Date());
    }

    public static Throwable getRootException(Throwable exception) {
        Throwable rootException = exception;
        while (rootException.getCause() != null) {
            rootException = rootException.getCause();
        }
        return rootException;
    }

    private ResponseEntity<Object> errorDetailsResponseEntity(Exception ex, HttpStatus httpStatus, String errorMsg) {

        log.error("{}:: {}", LOG_STRING, ex);
        ErrorResponse errorDetails = ErrorResponse.builder()
                .errorCode(httpStatus.value())
                .errorMessage(errorMsg)
                .errorDescription(getRootException(ex).getLocalizedMessage())
                .timeStamp(getTimeStamp())
                .build();

        return new ResponseEntity<>(
                errorDetails, httpStatus);
    }

    private ResponseEntity<Object> patternErrorDetailsResponseEntity(Exception ex, HttpStatus httpStatus,
                                                                     String errorMsg) {
        String errorDesc;

        try {
            errorDesc = ex.getMessage().substring(ex.getMessage().lastIndexOf("default message"));
            errorDesc = errorDesc.replace("default message [", "").replace("]]",
                    "");
        } catch (IndexOutOfBoundsException e) {
            errorDesc = getRootException(ex).getLocalizedMessage();
        }


        log.error("{}:: {}", LOG_STRING, ex);
        ErrorResponse errorDetails = ErrorResponse.builder()
                .errorMessage(errorMsg)
                .errorDescription(errorDesc)
                .timeStamp(getTimeStamp())
                .build();

        return new ResponseEntity<>(
                errorDetails, httpStatus);
    }

}
