package com.haiemdavang.AnrealShop.tech.poolRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(String trackingId) {
        SseEmitter emitter = new SseEmitter(60000L);
        emitters.put(trackingId, emitter);

        emitter.onCompletion(() -> emitters.remove(trackingId));
        emitter.onTimeout(() -> emitters.remove(trackingId));
        emitter.onError((e) -> emitters.remove(trackingId));

        return emitter;
    }

    public void sendResult(String trackingId, Object result) {
        SseEmitter emitter = emitters.get(trackingId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("checkout-result")
                        .data(result));
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(trackingId);
            }
        }
    }
}