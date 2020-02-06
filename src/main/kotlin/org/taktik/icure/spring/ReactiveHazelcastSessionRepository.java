package org.taktik.icure.spring;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.IMap;
import org.springframework.session.MapSession;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.Session;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

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
     *
     * @param defaultMaxInactiveInterval the number of seconds that the {@link Session}
     *                                   should be kept alive between client requests.
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
                this.sessions.removeAsync(session.getOriginalId()).andThen(
                        new ExecutionCallback<MapSession>() {
                            @Override
                            public void onResponse(MapSession response) {
                                sink.success();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                sink.error(t);
                            }
                        }
                );
            }
            this.sessions.setAsync(session.getId(), new MapSession(session)).andThen(
                    new ExecutionCallback<Void>() {
                        @Override
                        public void onResponse(Void response) {
                            sink.success();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            sink.error(t);

                        }
                    }
            );
        });
    }

    @Override
    public Mono<MapSession> findById(String id) {
        return Mono.create((MonoSink<MapSession> sink) -> {
            this.sessions.getAsync(id).andThen(
                    new ExecutionCallback<MapSession>() {
                        @Override
                        public void onResponse(MapSession response) {
                            sink.success(response);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            sink.error(t);
                        }
                    }
            );
        })
                .filter((session) -> !session.isExpired())
                .map(MapSession::new)
                .switchIfEmpty(deleteById(id).then(Mono.empty()));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.create(sink -> this.sessions.removeAsync(id).andThen(
                new ExecutionCallback<MapSession>() {
                    @Override
                    public void onResponse(MapSession response) {
                        sink.success();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        sink.error(t);
                    }
                }
        ));
    }
}
