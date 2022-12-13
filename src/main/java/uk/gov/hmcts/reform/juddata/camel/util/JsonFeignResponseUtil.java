package uk.gov.hmcts.reform.juddata.camel.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import uk.gov.hmcts.reform.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.juddata.exception.JudicialDataLoadException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ERROR_IN_PARSING_THE_FEIGN_RESPONSE;


@SuppressWarnings({"unchecked","HideUtilityClassConstructor"})
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonFeignResponseUtil {
    private static final ObjectMapper json = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static ResponseEntity<Object> toResponseEntity(Response response, TypeReference<?> reference) {
        Optional<Object> payload = Optional.empty();

        try {
            payload = Optional.of(json.readValue(response.body().asReader(Charset.defaultCharset()), reference));

        } catch (IOException ex) {
            log.error("error while reading the body", ex);
            throw new JudicialDataLoadException("Response parsing failed");
        }

        return new ResponseEntity<>(
            payload.orElse(null),
            convertHeaders(response.headers()),
            HttpStatus.valueOf(response.status()));
    }

    public static ResponseEntity<Object> toELinksResponseEntity(Response response, Object clazz) {
        Optional<Object>  payload = decode(response, clazz);

        return new ResponseEntity<>(
                payload.orElse("unknown"),
                convertHeaders(response.headers()),
                HttpStatus.valueOf(response.status()));
    }

    public static Optional<Object> decode(Response response, Object clazz) {
        try {
            return Optional.of(json.readValue(response.body().asReader(Charset.defaultCharset()),
                    (Class<Object>) clazz));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static MultiValueMap<String, String> convertHeaders(Map<String, Collection<String>> responseHeaders) {
        HttpHeaders responseEntityHeaders = new HttpHeaders();
        responseHeaders.entrySet().stream().forEach(e ->
            responseEntityHeaders.put(e.getKey(), new ArrayList<>(e.getValue())));
        return responseEntityHeaders;
    }


}

