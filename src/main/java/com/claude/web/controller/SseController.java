package com.claude.web.controller;

import com.claude.web.dto.NotificationEvent;
import com.claude.web.service.SseEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/claude-api")
public class SseController {

    private static final Logger logger = LoggerFactory.getLogger(SseController.class);

    private final SseEventService sseEventService;
    private final ObjectMapper objectMapper;
    private final ExecutorService executor;

    public SseController(SseEventService sseEventService, ObjectMapper objectMapper) {
        this.sseEventService = sseEventService;
        this.objectMapper = objectMapper;
        this.executor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "sse-emitter-");
            t.setDaemon(true);
            return t;
        });
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events() {
        SseEmitter emitter = new SseEmitter(0L); // No timeout
        final AtomicBoolean completed = new AtomicBoolean(false);

        executor.submit(() -> {
            try {
                // Send initial ready event
                emitter.send(SseEmitter.event()
                    .name("ready")
                    .data("{\"ok\":true}"));

                // Subscribe to events
                sseEventService.createEventStream()
                    .subscribe(
                        event -> {
                            if (completed.get()) {
                                return;
                            }
                            try {
                                if ("ping".equals(event.getMethod())) {
                                    emitter.send(": ping\n\n", MediaType.TEXT_PLAIN);
                                } else {
                                    String data = objectMapper.writeValueAsString(event);
                                    emitter.send(SseEmitter.event()
                                        .data(data));
                                }
                            } catch (IOException e) {
                                logger.debug("Failed to send SSE event, client likely disconnected");
                                if (completed.compareAndSet(false, true)) {
                                    emitter.completeWithError(e);
                                }
                            } catch (IllegalStateException e) {
                                // Emitter already completed
                                logger.debug("SSE emitter already completed, skipping event");
                                completed.set(true);
                            }
                        },
                        error -> {
                            logger.error("SSE stream error", error);
                            if (completed.compareAndSet(false, true)) {
                                emitter.completeWithError(error);
                            }
                        },
                        () -> {
                            logger.debug("SSE stream completed");
                            if (completed.compareAndSet(false, true)) {
                                emitter.complete();
                            }
                        }
                    );
            } catch (Exception e) {
                logger.error("SSE setup error", e);
                if (completed.compareAndSet(false, true)) {
                    emitter.completeWithError(e);
                }
            }
        });

        emitter.onCompletion(() -> {
            completed.set(true);
            logger.debug("SSE connection completed");
        });
        emitter.onTimeout(() -> {
            completed.set(true);
            logger.debug("SSE connection timed out");
        });
        emitter.onError(e -> {
            completed.set(true);
            logger.debug("SSE connection error: {}", e.getMessage());
        });

        return emitter;
    }
}
