package com.juegodefinitivo.autobook.api;

import com.juegodefinitivo.autobook.api.dto.ActionRequest;
import com.juegodefinitivo.autobook.api.dto.AutoplayRequest;
import com.juegodefinitivo.autobook.api.dto.BookView;
import com.juegodefinitivo.autobook.api.dto.GameStateResponse;
import com.juegodefinitivo.autobook.api.dto.ImportBookRequest;
import com.juegodefinitivo.autobook.api.dto.StartGameRequest;
import com.juegodefinitivo.autobook.api.dto.TelemetryEventRequest;
import com.juegodefinitivo.autobook.api.dto.TelemetrySummaryResponse;
import com.juegodefinitivo.autobook.service.GameFacadeService;
import com.juegodefinitivo.autobook.service.TelemetryService;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameFacadeService service;
    private final TelemetryService telemetryService;

    public GameController(GameFacadeService service, TelemetryService telemetryService) {
        this.service = service;
        this.telemetryService = telemetryService;
    }

    @PostConstruct
    public void init() {
        service.bootstrapSamples();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/books")
    public List<BookView> listBooks() {
        return service.listBooks();
    }

    @PostMapping("/books/import")
    public BookView importBook(@RequestBody ImportBookRequest request) {
        return service.importBook(request.path());
    }

    @PostMapping("/game/start")
    public GameStateResponse startGame(@RequestBody StartGameRequest request) {
        return service.startGame(request.playerName(), request.bookPath());
    }

    @GetMapping("/game/{sessionId}")
    public GameStateResponse getState(@PathVariable String sessionId) {
        return service.getState(sessionId);
    }

    @PostMapping("/game/{sessionId}/action")
    public GameStateResponse action(@PathVariable String sessionId, @RequestBody ActionRequest request) {
        return service.applyAction(sessionId, request.action(), request.answerIndex(), request.itemId());
    }

    @PostMapping("/game/{sessionId}/autoplay")
    public GameStateResponse autoplay(@PathVariable String sessionId, @RequestBody(required = false) AutoplayRequest request) {
        AutoplayRequest safeRequest = request == null ? new AutoplayRequest(null, null, null) : request;
        return service.applyAutoplay(sessionId, safeRequest.ageBand(), safeRequest.readingLevel(), safeRequest.maxSteps());
    }

    @PostMapping("/telemetry/events")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void telemetry(@RequestBody TelemetryEventRequest request) {
        telemetryService.record(request);
    }

    @GetMapping("/telemetry/summary")
    public TelemetrySummaryResponse telemetrySummary() {
        return telemetryService.summary();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> onBadRequest(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public void onError(Exception ex) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", ex);
    }
}
