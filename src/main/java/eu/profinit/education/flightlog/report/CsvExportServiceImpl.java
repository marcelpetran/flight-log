package eu.profinit.education.flightlog.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// TODO 4.2: Po přidání závislosti můžete importovat třídy pro práci s CSV
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import eu.profinit.education.flightlog.flight.Flight;
import eu.profinit.education.flightlog.flight.FlightRepository;
import eu.profinit.education.flightlog.common.exceptions.FlightLogException;

@Service
public class CsvExportServiceImpl implements CsvExportService {

    // TODO 4.4 Vhodné vlastnosti CSV souboru definujte jako konstanty

    private final FlightRepository flightRepository;

    private final String fileName;

    public CsvExportServiceImpl(FlightRepository flightRepository, @Value("${csv.export.flight.fileName}") String fileName) {
        this.flightRepository = flightRepository;
        this.fileName = fileName;
    }

    @Override
    public FileExportTo getAllFlightsAsCsv() {
        // TODO 4.3: Naimplementujte vytváření CSV.
        // Tip: můžete použít Apache Commons CSV - https://commons.apache.org/proper/commons-csv/ v příslušných pom.xml naleznete další komentáře s postupem
        List<Flight> flights = flightRepository.findAll(
            Sort.by(Sort.Order.asc("takeoffTime"),
              Sort.Order.asc("id")));
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
              Writer writer = new OutputStreamWriter(byteArrayOutputStream, Charset.forName("ASCII"));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
          // TODO print data to CSV
                csvPrinter.printRecord("ID", "Flight Type", "Takeoff Time", "Landing Time", "Pilot", "Co-Pilot", "Towing Flight ID");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (Flight flight : flights) {
                    String takeoffTime = flight.getTakeoffTime() != null ? flight.getTakeoffTime().format(formatter) : "";
                    String landingTime = flight.getLandingTime() != null ? flight.getLandingTime().format(formatter) : "";
                    String copilot = flight.getCopilot() != null ? flight.getCopilot().getFullName() : "";
                    String towingFlightId = flight.getTowingFlight() != null ? flight.getTowingFlight().getId().toString() : "";
                    csvPrinter.printRecord(
                        flight.getId(),
                        flight.getFlightType().name(),
                        takeoffTime,
                        landingTime,
                        flight.getPilot().getFullName(),
                        copilot,
                        towingFlightId
                    );  
              }

    }
}