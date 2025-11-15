package eu.profinit.education.flightlog.airplane;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AirplaneController {
    private final AirplaneService flightService;

    @GetMapping("/airplane")
    public List<AirplaneTo> getClubAirplanes() {
        return flightService.getClubAirplanes();
    }
}