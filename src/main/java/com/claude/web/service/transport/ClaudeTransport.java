package com.claude.web.service.transport;

import reactor.core.publisher.Flux;

/**
 * Abstraction for the physical transport layer to the Claude Agent.
 * Implementations can use SSH, WebSocket, local process, etc.
 */
public interface ClaudeTransport {

    /**
     * Establish the connection to the remote app-server.
     */
    void connect() throws Exception;

    /**
     * Tear down the connection.
     */
    void disconnect();

    /**
     * Check if the transport is currently connected.
     */
    boolean isConnected();

    /**
     * Send a single JSON-RPC line (including trailing newline handling is up to the transport).
     */
    void send(String line) throws Exception;

    /**
     * Receive incoming JSON-RPC lines from the app-server.
     * The flux terminates when the connection is lost.
     */
    Flux<String> receive();

    /**
     * Dispose any resources (executors, threads) held by the transport.
     */
    default void dispose() {
        disconnect();
    }
}
