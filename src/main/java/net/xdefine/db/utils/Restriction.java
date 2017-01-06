package net.xdefine.db.utils;

public class Restriction {
	
	public static Restriction eq(String field, String value) {
		return new Restriction();
	}

	public static Restriction eqField(String field, String value) {
		return new Restriction();
	}

	public static Restriction or(Restriction args) {
		return new Restriction();
	}

}
