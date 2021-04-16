package uk.gov.hmcts.reform.judicialapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories
@SpringBootApplication
@EnableCaching
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class Application  {
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
    /*
    select jup.title, jup.known_as, jup.surname, jup.email_id, jup.elinks_id, jup.object_id    from judicial_user_profile jup
where full_name like '%Mag%' or title like '%Mag%' or email_id like '%Mag%'
     */
}
