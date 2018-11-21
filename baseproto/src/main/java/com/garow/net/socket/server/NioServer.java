package com.garow.net.socket.server;

import java.io.IOException;

import com.garow.net.socket.server.config.ServerConfig;

public interface NioServer {
	public void start(final ServerConfig config) throws IOException;
}
