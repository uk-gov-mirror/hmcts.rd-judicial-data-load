package uk.gov.hmcts.reform.elinks.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity(name = "base_location_type")
@Getter
@Setter
@NoArgsConstructor
public class BaseLocation {

    @Id
    @Column(name = "base_location_Id")
    private String baseLocationId;

    @Column(name = "court_name")
    @Size(max = 128)
    private String courtName;

    @Column(name = "court_type")
    @Size(max = 128)
    private String courtType;

    @Column(name = "circuit")
    @Size(max = 128)
    private String circuit;

    @Column(name = "area_of_expertise")
    @Size(max = 128)
    private String areaOfExpertise;

}
