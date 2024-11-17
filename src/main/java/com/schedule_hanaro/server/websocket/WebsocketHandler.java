package com.schedule_hanaro.server.websocket;

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
		try  {
			session.sendMessage(
				new TextMessage("웹소켓 연결 성공"));
		} catch (Exception e) {
			// log.error(e.getMessage());
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		for (WebSocketSession s : sessions) {
			s.sendMessage(new TextMessage("Hello!" + payload));
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status)throws Exception{
		sessions.remove(session);
	}

}
