package uk.gov.hmcts.reform.elinks.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
public class SchedulerJobStatusResponse {


    @JsonProperty("id")
    private String id;

    @JsonProperty("publishing_status")
    private String jobStatus;

    @JsonProperty("sidam_id")
    private List<String> sidamIds;
    public SchedulerJobStatusResponse(String id, String jobStatus, List<String> sidamIds){
        this.id=id;
        this.jobStatus = jobStatus;
        this.sidamIds= sidamIds;
    }
}
