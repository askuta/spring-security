package com.zoroga.hello.dice;

import java.util.Random;

public class Dice {

	private int value;

	public void roll() {
		Random random = new Random();
		value = random.nextInt(6) + 1;
	}

	public int getValue() {
		return value;
	}
}
