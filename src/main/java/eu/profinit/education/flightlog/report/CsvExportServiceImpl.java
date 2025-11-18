package eu.profinit.education.flightlog.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

  // TODO 4.4: Define suitable properties for the CSV file as constants
  private static final String[] HEADERS = { "Typ", "Immatrikulace", "Pilot", "Úkol", "Vzlet", "Přistání",
      "Doba letu (minutky)" };
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private final FlightRepository flightRepository;
  private final String fileName;

  public CsvExportServiceImpl(FlightRepository flightRepository,
      @Value("${csv.export.flight.fileName}") String fileName) {
    this.flightRepository = flightRepository;
    this.fileName = fileName;
  }

  @Override
  public FileExportTo getAllFlightsAsCsv() {
    List<Flight> flights = flightRepository.findAllByLandingTimeNotNullOrderByTakeoffTimeAscIdAsc();

    CSVFormat csvFormat = CSVFormat.Builder.create(CSVFormat.DEFAULT)
        .setHeader(HEADERS)
        .build();

    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter printer = new CSVPrinter(
            new OutputStreamWriter(out, StandardCharsets.UTF_8),
            csvFormat)) {

      for (Flight flight : flights) {
        printer.printRecord(
            flight.getAirplane().getSafeType(),
            flight.getAirplane().getSafeImmatriculation(),
            flight.getPilot().getFullName(),
            flight.getTask() != null ? flight.getTask().getValue() : "",
            flight.getTakeoffTime().format(DATE_FORMATTER),
            flight.getLandingTime() != null ? flight.getLandingTime().format(DATE_FORMATTER) : "",
            flight.getLandingTime() != null
                ? java.time.Duration.between(flight.getTakeoffTime(), flight.getLandingTime()).toMinutes()
                : "");
      }

      printer.flush();

      // Correct order: (fileName, content, contentType)
      return new FileExportTo(
          fileName,
          MediaType.TEXT_PLAIN,
          StandardCharsets.UTF_8.name(),
          out.toByteArray());

    } catch (IOException e) {
      throw new FlightLogException("Error while exporting Flight Log to CSV", e);
    }
  }
}