package com.zoroga.hello.dice;

public class DicePairRoll {

	private Dice dice1 = new Dice();
	private Dice dice2 = new Dice();
	private int total;

	public void rollDice() {
		dice1.roll();
		dice2.roll();
		total = dice1.getValue() + dice2.getValue();
	}

	public Dice getDice1() {
		return dice1;
	}

	public Dice getDice2() {
		return dice2;
	}

	public int getTotal() {
		return total;
	}
}
