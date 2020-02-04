package org.taktik.icure.spring;

import com.hazelcast.map.IMap;
import org.springframework.session.MapSession;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.Session;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class ReactiveHazelcastSessionRepository implements ReactiveSessionRepository<MapSession> {
    private Integer defaultMaxInactiveInterval;

    private final IMap<String, MapSession> sessions;

    public ReactiveHazelcastSessionRepository(IMap<String, MapSession> sessions) {
        if (sessions == null) {
            throw new IllegalArgumentException("sessions cannot be null");
        }
        this.sessions = sessions;
    }

    /**
     * If non-null, this value is used to override
     * {@link Session#setMaxInactiveInterval(Duration)}.
     * @param defaultMaxInactiveInterval the number of seconds that the {@link Session}
     * should be kept alive between client requests.
     */
    public void setDefaultMaxInactiveInterval(int defaultMaxInactiveInterval) {
        this.defaultMaxInactiveInterval = defaultMaxInactiveInterval;
    }


    @Override
    public Mono<MapSession> createSession() {
        return Mono.defer(() -> {
            MapSession result = new MapSession();
            if (this.defaultMaxInactiveInterval != null) {
                result.setMaxInactiveInterval(Duration.ofSeconds(this.defaultMaxInactiveInterval));
            }
            return Mono.just(result);
        });
    }

    @Override
    public Mono<Void> save(MapSession session) {
        return Mono.create(sink -> {
            if (!session.getId().equals(session.getOriginalId())) {
                this.sessions.removeAsync(session.getOriginalId()).whenCompleteAsync((response, throwable) -> {
                    if (throwable != null) {
                        sink.error(throwable);
                    } else {
                        sink.success();
                    }
                });
            }
            this.sessions.setAsync(session.getId(), new MapSession(session)).whenCompleteAsync((response, throwable) -> {
                if (throwable != null) {
                    sink.error(throwable);
                } else {
                    sink.success();
                }
            });
        });
    }

    @Override
    public Mono<MapSession> findById(String id) {
        return Mono.fromCompletionStage(this.sessions.getAsync(id))
                .filter((session) -> !session.isExpired())
                .map(MapSession::new)
                .switchIfEmpty(deleteById(id).then(Mono.empty()));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.create(sink -> this.sessions.removeAsync(id).whenCompleteAsync((response, throwable) -> {
            if (throwable != null) {
                sink.error(throwable);
            } else {
                sink.success();
            }
        }));
    }
}
