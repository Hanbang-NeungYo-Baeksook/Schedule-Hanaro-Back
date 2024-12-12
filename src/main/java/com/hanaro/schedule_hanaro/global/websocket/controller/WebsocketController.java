package com.hanaro.schedule_hanaro.global.websocket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class WebsocketController {

	@MessageMapping("/hello")
	@SendTo("/topic/greeting")
	public String greeting(String message) {
		log.info("웹소켓 hello 컨트롤러 진입");
		return message + "reply";
	}
}
