package eu.profinit.education.flightlog.person.user;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import eu.profinit.education.flightlog.common.exceptions.ExternalSystemException;

@Component
@Profile("!stub")
public class ClubDatabaseDaoImpl implements ClubDatabaseDao {

    private final RestClient restClient;

    // TODO 3.2: načtěte property integration.clubDb.baseUrl z application.properties (hint: CsvExportServiceImpl)
    public ClubDatabaseDaoImpl( 
        @Value("${integration.clubDb.baseUrl}") String clubDbBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(clubDbBaseUrl)
                .build();
        
    }

    @Override
    public List<User> getUsers() {
        User[] userList;
        try {
            // TODO 3.3: implementujte tělo volání endpointu ClubDB pomocí REST client get() metody
            userList = restClient.get()
                    .uri("/club/user")
                    .retrieve()
                .body(User[].class);

        } catch (RuntimeException e) {
            throw new ExternalSystemException("Cannot get users from Club database, call resulted in exception.", e);
        }
        return Arrays.asList(userList);
    }
}
