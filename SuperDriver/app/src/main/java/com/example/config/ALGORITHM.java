package com.example.config;

public enum ALGORITHM {
	newRecords(1), historyHistoryRecords(2);
	private final int value;

	private ALGORITHM(int value) {
		this.value = value;
	}
	
    public int value() {
        return this.value;
    }
}