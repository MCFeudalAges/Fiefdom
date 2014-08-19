package org.nowireless.factions.empire;

public enum Result {
	
	PROCEED(true),
	NO(false),
	NO_NOT_ENOUGH_POWER(false),
	NO_ALLREADY_OWNED(false),
	NO_CANT_BE_CLAIMED(false),
	NO_IS_EMPIRE(false),
	NO_DEFAULT_FACTION(false),
	NO_YOU_ARE_EMPIRE(false),
	NO_NO_CHANGE(false),
	NO_NOT_IMPLEMENTED_YET(false)
	;
	
	private final boolean good;
	private Result(boolean good) {
		this.good = good;
	}
	public boolean isGood() {
		return this.good;
	}
}
