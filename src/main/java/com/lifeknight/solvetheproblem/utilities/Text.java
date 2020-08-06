package com.lifeknight.solvetheproblem.utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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

	public static int countWords(String msg) {
		int count = 0;
		for (int x = 0; x < msg.length(); x++) {
			if (msg.charAt(x) == ' ') {
				count++;
			}
		}
		return ++count;
	}
	
	public static String removeAll(String msg, String rmv) {
		msg = msg.replaceAll(rmv, "");
		return msg;
	}

	public static String removeFormattingCodes(String input) {
		return input.replaceAll("[" + '\u00A7' + "][\\w]", "");
	}

	public static String multiplyString(String string, int times) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < times; i++) {
			result.append(string);
		}
		return result.toString();
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

	public static String parseTextToIndexOfTextAfter(String text, String firstIndexText, String secondIndexText) {
        if (text.contains(firstIndexText) && text.contains(secondIndexText)) {
			return text.substring((firstIndexText.indexOf(firstIndexText) + firstIndexText.length() + 1), (text.indexOf(secondIndexText) - 1));
        }
        return null;
	}

	public static String shortenDouble(double value, int decimalDigits) {
		String asString = String.valueOf(value);
		int wholeDigits = asString.substring(0, asString.indexOf(".")).length();
		return new DecimalFormat(multiplyString("#", wholeDigits) + "." + multiplyString("#", decimalDigits)).format(value);
	}

    public static boolean equalsAny(String text, String[] strings, boolean ignoreCase) {
        for (String string : strings) {
            if (ignoreCase) {
                if (string.equalsIgnoreCase(text)) return true;
            } else {
                if (string.equals(text)) return true;
            }
        }
        return false;
    }

    public static boolean containsAny(String text, String[] strings, boolean ignoreCase) {
        for (String string : strings) {
            if (ignoreCase) {
                if (text.toLowerCase().contains(string.toLowerCase())) return true;
            } else {
                if (text.contains(string)) return true;
            }
        }
        return false;
    }

    public static boolean startsWithAny(String text, String[] strings, boolean ignoreCase) {
        for (String string : strings) {
            if (ignoreCase) {
                if (text.toLowerCase().startsWith(string.toLowerCase())) return true;
            } else {
                if (text.startsWith(string)) return true;
            }
        }
        return false;
    }

    public static boolean endsWithAny(String text, String[] strings, boolean ignoreCase) {
        for (String string : strings) {
            if (ignoreCase) {
                if (text.toLowerCase().endsWith(string.toLowerCase())) return true;
            } else {
                if (text.endsWith(string)) return true;
            }
        }
        return false;
    }

    public static boolean containsLetters(String input) {
        return Pattern.compile("[a-zA-Z]").matcher(input).find();
    }

    public static String scramble(String input) {
	    List<Integer> indexesUsed = new ArrayList<>();
	    StringBuilder result = new StringBuilder();

	    while (result.length() != input.length()) {
            int randomIndex = getRandomNumberThatIsntAnother(0, input.length() - 1, indexesUsed);
            result.append(input.charAt(randomIndex));
            indexesUsed.add(randomIndex);
        }
	    return result.toString();
    }

    private static int getRandomNumberThatIsntAnother(int min, int max, List<Integer> previousIndexes) {
	    int randomIndex = Miscellaneous.getRandomIntBetweenRange(min, max);
	    if (!previousIndexes.contains(randomIndex)) return randomIndex;
	    return getRandomNumberThatIsntAnother(min, max, previousIndexes);
    }
}
