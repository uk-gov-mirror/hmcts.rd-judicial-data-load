package uk.gov.hmcts.reform.elinks.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LocationResponse {
    private String id;
    private String name;
    private String start_date;
    private String end_date;
    private String created_at;
    private String updated_at;

    //{"id":1,"name":"National","start_date":null,"end_date":null,"created_at":"2022-10-03T15:28:19Z","updated_at":"2022-10-03T15:28:19Z"}
}
