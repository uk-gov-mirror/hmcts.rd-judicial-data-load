package uk.gov.hmcts.reform.juddata.camel.util;

import com.fasterxml.jackson.core.type.TypeReference;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import uk.gov.hmcts.reform.elinks.response.ElinkLocationResponse;
import uk.gov.hmcts.reform.juddata.client.IdamClient;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonFeignResponseUtilTest {
    private Response responseMock; //mocked as builder has private access
    private Response.Body bodyMock; //mocked as Body is an interface in Feign.Response
    private Reader readerMock; //mocked as it is an abstract class from Java.io
    final int statusCode = 200;

    @BeforeEach
    public void setUp() throws IOException {
        responseMock = mock(Response.class);
        bodyMock = mock(Response.Body.class);
        readerMock = mock(Reader.class);

        when(responseMock.body()).thenReturn(bodyMock);
        when(responseMock.body().asReader(Charset.defaultCharset())).thenReturn(readerMock);
        when(responseMock.status()).thenReturn(statusCode);
    }

    @Test
    void testToResponseEntityThrowError() throws IOException {
        when(bodyMock.asReader(Charset.defaultCharset())).thenThrow(IOException.class);
        assertThrows(Exception.class, () ->  JsonFeignResponseUtil.toResponseEntity(this.responseMock,
                new TypeReference<List<IdamClient.User>>() {}));
    }


    @Test
    void testDecode() {
        Map<String, Collection<String>> header = new HashMap<>();
        Collection<String> list = new ArrayList<>();
        header.put("content-encoding", list);
        String body = "{\"results\":[{\"id\":1,\"name\":\"National\",\"start_date\":null,\"end_date\":null,"
                +
                "\"created_at\":\"2022-10-03T15:28:19Z\",\"updated_at\":\"2022-10-03T15:28:19Z\"}]}";
        Response response = Response.builder().status(200).reason("OK").headers(header)
                .body(body, UTF_8).request(mock(Request.class)).build();
        Optional<Object> createUserProfileResponseOptional = JsonFeignResponseUtil.decode(response,
                ElinkLocationResponse.class);

        assertThat(createUserProfileResponseOptional).isNotEmpty();
    }

    @Test
    void test_Decode_fails_with_ioException() {
        Map<String, Collection<String>> header = new HashMap<>();
        Collection<String> list = new ArrayList<>();
        header.put("content-encoding", list);

        Response.Body bodyMock = mock(Response.Body.class);
        Response response = Response.builder().status(200).reason("OK").headers(header).body(bodyMock)
                .request(mock(Request.class)).build();



        Optional<Object> createUserProfileResponseOptional = JsonFeignResponseUtil.decode(response,
                ElinkLocationResponse.class);
        assertThat(createUserProfileResponseOptional).isEmpty();
    }

    @Test
    void test_convertHeaders() {
        Map<String, Collection<String>> header = new HashMap<>();
        Collection<String> list = new ArrayList<>(Arrays.asList("gzip", "request-context", "x-powered-by",
                "content-length"));
        header.put("content-encoding", list);

        MultiValueMap<String, String> responseHeader = JsonFeignResponseUtil.convertHeaders(header);
        assertThat(responseHeader).isNotEmpty();

        Collection<String> emptylist = new ArrayList<>();
        header.put("content-encoding", emptylist);
        MultiValueMap<String, String> responseHeader1 = JsonFeignResponseUtil.convertHeaders(header);

        assertThat(responseHeader1.get("content-encoding")).isEmpty();
    }

    @Test
    void test_toResponseEntity_with_payload_not_empty() {
        Map<String, Collection<String>> header = new HashMap<>();
        Collection<String> list = new ArrayList<>(Arrays.asList("a", "b"));
        header.put("content-encoding", list);

        String body = "{\"results\":[{\"id\":1,\"name\":\"National\",\"start_date\":null,\"end_date\":null,"
                +
                "\"created_at\":\"2022-10-03T15:28:19Z\",\"updated_at\":\"2022-10-03T15:28:19Z\"}]}";
        Response response = Response.builder().status(200).reason("OK").headers(header)
                .body(body, UTF_8).request(mock(Request.class)).build();

        ResponseEntity entity = JsonFeignResponseUtil.toELinksResponseEntity(response, ElinkLocationResponse.class);
        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode().value()).isEqualTo(200);
        assertThat(entity.getHeaders()).isNotEmpty();
    }

    @Test
    void test_privateConstructor() throws Exception {
        Constructor<JsonFeignResponseUtil> constructor = JsonFeignResponseUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance((Object[]) null);
    }

}