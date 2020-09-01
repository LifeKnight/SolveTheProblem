package com.lifeknight.solvetheproblem.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Text {

	public static List<String> returnStartingEntries(String[] strings, String input, boolean ignoreCase) {
		if (input == null || input.isEmpty()) return Arrays.asList(strings);
		List<String> result = new ArrayList<>();
			for (String string : strings) {
				if (ignoreCase) {
					if (string.toLowerCase().startsWith(input.toLowerCase())) result.add(string);
				} else {
					if (string.startsWith(input)) result.add(string);
				}
			}
		return result;
	}

	public static String removeAllPunctuation(String text) {
		return text.replaceAll("\\W", "");
	}

	public static String removeFormattingCodes(String input) {
		return input.replaceAll("[" + '\u00A7' + "][\\w]", "");
	}

    public static String formatCapitalization(String input, boolean keepFirstCapitalized) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = input.length() - 1; i > 0; i--) {
            char toInsert;
            char previousChar = input.charAt(i - 1);
            if (previousChar == Character.toUpperCase(previousChar)) {
                toInsert = Character.toLowerCase(input.charAt(i));
            } else {
                toInsert = input.charAt(i);
            }
            stringBuilder.insert(0, toInsert);
        }

        return stringBuilder.insert(0, keepFirstCapitalized ? input.charAt(0) : Character.toLowerCase(input.charAt(0))).toString();
    }

    public static String scramble(String input) {
	    List<Integer> indexesUsed = new ArrayList<>();
	    StringBuilder result = new StringBuilder();

	    while (result.length() != input.length()) {
            int randomIndex = Miscellaneous.getRandomIntegerThatIsntAnother(0, input.length() - 1, indexesUsed);
            result.append(input.charAt(randomIndex));
            indexesUsed.add(randomIndex);
        }
	    return result.toString();
    }

    public static String formatTimeFromMilliseconds(long milliseconds, int count) {
        long days;
        long hours;
        long minutes;
        long seconds;
        long millisecondsLeft = milliseconds;
        days = millisecondsLeft / 86400000;
        millisecondsLeft %= 86400000;
        hours = millisecondsLeft / 3600000;
        millisecondsLeft %= 3600000;
        minutes = millisecondsLeft / 60000;
        millisecondsLeft %= 60000;
        seconds = millisecondsLeft / 1000;
        millisecondsLeft %= 1000;

        StringBuilder result = new StringBuilder();

        if (days > 0 && count >= 4) {
            result.append(days).append(":");
            result.append(appendTime(hours)).append(":");
        } else if (count >= 3) {
            result.append(hours).append(":");
        }


        if (count >= 2) result.append(appendTime(minutes)).append(":");

        if (count >= 1) result.append(appendTime(seconds)).append(".");

        result.append(formatMilliseconds(millisecondsLeft));

        return result.toString();
    }

    private static String appendTime(long timeValue) {
        StringBuilder result = new StringBuilder();
        if (timeValue > 9) {
            result.append(timeValue);
        } else {
            result.append("0").append(timeValue);
        }
        return result.toString();
    }

    private static String formatMilliseconds(long milliseconds) {
        String asString = String.valueOf(milliseconds);

        if (asString.length() == 1) {
            return "00" + milliseconds;
        } else if (asString.length() == 2) {
            return "0" + milliseconds;
        }
        return asString;
    }

}
