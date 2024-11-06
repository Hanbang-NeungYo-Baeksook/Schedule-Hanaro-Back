package com.schedule_hanaro.server.WebsocketTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebsocketHandler extends TextWebSocketHandler {
	private final ObjectMapper mapper;

	private final Set<WebSocketSession> sessions = new HashSet<>();

	private final Map<Long, Set<WebSocketSession>> scheduleSessionMap = new HashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session)throws Exception{
		sessions.add(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message)throws Exception{
		String payload = message.getPayload();
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status)throws Exception{
		sessions.remove(session);
	}

}
