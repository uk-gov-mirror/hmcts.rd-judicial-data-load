
package uk.gov.hmcts.reform.judicialapi.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Id;
import java.io.Serializable;

@Entity(name = "judicial_user_profile")
@NoArgsConstructor
@Getter
@Setter
public class JudicialUserProfile implements Serializable {


    @Id
    private String elinks_id;

    @Column(name = "object_id")
    private String object_id;

    @Column(name = "title")
    private String title;

    @Column(name = "known_as")
    private String known_as;

    @Column(name = "full_name")
    private String full_name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email_id")
    private String email_id;
}

