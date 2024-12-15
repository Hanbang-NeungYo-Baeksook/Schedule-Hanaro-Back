package com.hanaro.schedule_hanaro.global.websocket.handler;

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

	// @Override
	// protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	// 	String payload = message.getPayload();
	// 	for (WebSocketSession s : sessions) {
	// 		s.sendMessage(new TextMessage("Hello!" + payload));
	// 	}
	// }
	//
	// @Override
	// public void afterConnectionClosed(WebSocketSession session, CloseStatus status)throws Exception{
	// 	sessions.remove(session);
	// }

	////////////////////////////////////

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		try {
			String payload = message.getPayload();
			Map data = mapper.readValue(payload, Map.class);

			if ("subscribe".equals(data.get("action"))) {
				Long topicId = Long.valueOf(String.valueOf(data.get("topicId")));
				scheduleSessionMap.computeIfAbsent(topicId, k -> new HashSet<>()).add(session);
				session.sendMessage(new TextMessage("구독 완료: " + topicId));
			} else {
				session.sendMessage(new TextMessage("알 수 없는 액션: " + data.get("action")));
			}
		} catch (Exception e) {
			session.sendMessage(new TextMessage("잘못된 요청 형식: " + e.getMessage()));
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		// session이 종료된 경우 구독 목록에서 제거
		scheduleSessionMap.values().forEach(sessions -> sessions.remove(session));
	}

	// 특정 주제를 구독하는 클라이언트들에게 메시지 전송
	public void notifySubscribers(Long topicId, String message) {
		if (scheduleSessionMap.containsKey(topicId)) {
			for (WebSocketSession session : scheduleSessionMap.get(topicId)) {
				try {
					session.sendMessage(new TextMessage(message));
				} catch (Exception e) {
					// 에러 처리 (로깅 등)
				}
			}
		}
	}

}
