package com.lifeknight.modbase.utilities;

import java.util.regex.Pattern;

public class Logic {

	public static boolean isWithinRange(int a, int b, int range) {
		return (b > a - range && b < a + range) || (a > b - range && a < b + range);
	}
}
