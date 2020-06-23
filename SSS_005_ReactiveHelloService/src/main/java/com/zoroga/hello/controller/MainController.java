package com.zoroga.hello.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zoroga.hello.dice.DicePairRoll;

import reactor.core.publisher.Mono;

@RestController
public class MainController {

	private DicePairRoll dice = new DicePairRoll();

	@GetMapping("/hello")
	Mono<String> getGreeting() {
		return Mono.just("Hello LinkedIn Learning! Never stop learning!");
	}

	@GetMapping("/roll")
	public Mono<DicePairRoll> getDiceRoll() {
		dice.rollDice();
		return Mono.just(dice);
	}
}
