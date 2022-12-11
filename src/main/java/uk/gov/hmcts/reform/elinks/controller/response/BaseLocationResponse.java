package uk.gov.hmcts.reform.elinks.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("all")
public class BaseLocationResponse {

    private String baseLocationId;

    private String courtType;

    private String courtName;

    private String circuit;

    private String area_of_expertise;


}
