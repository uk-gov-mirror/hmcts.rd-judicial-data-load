package uk.gov.hmcts.reform.elinks.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerJobStatusResponse {

    @JsonProperty
    private int  statusCode;

    @JsonProperty("id")
    private String id;

    @JsonProperty("publishing_status")
    private String jobStatus;

    @JsonProperty("sidam_id")
    private List<String> sidamIds;

}
