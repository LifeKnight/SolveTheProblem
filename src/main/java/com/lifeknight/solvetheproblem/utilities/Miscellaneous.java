package com.lifeknight.solvetheproblem.utilities;

import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.util.EnumChatFormatting.*;

public class Miscellaneous {
	public static int getRandomIntBetweenRange(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static boolean getRandomTrueOrFalse() {
		return getRandomIntBetweenRange(0, 1) == 1;
	}

	public static EnumChatFormatting getEnumChatFormatting(String formattedName) {
		switch (formattedName) {
			case "Red":
				return RED;
			case "Gold":
				return GOLD;
			case "Yellow":
				return YELLOW;
			case "Green":
				return GREEN;
			case "Aqua":
				return AQUA;
			case "Blue":
				return BLUE;
			case "Light Purple":
				return LIGHT_PURPLE;
			case "Dark Red":
				return DARK_RED;
			case "Dark Green":
				return DARK_GREEN;
			case "Dark Aqua":
				return DARK_AQUA;
			case "Dark Blue":
				return DARK_BLUE;
			case "Dark Purple":
				return DARK_PURPLE;
			case "White":
				return WHITE;
			case "Gray":
				return GRAY;
			case "Dark Gray":
				return DARK_GRAY;
		}
		return BLACK;
	}

	public static <T> T getRandomElement(List<T> elements) {
		return elements.get(getRandomIntBetweenRange(0, elements.size() - 1));
	}

	public static int getRandomIntegerThatIsntAnother(int min, int max, List<Integer> previousIndexes) {
		int randomIndex = getRandomIntBetweenRange(min, max);
		if (!previousIndexes.contains(randomIndex)) return randomIndex;
		return getRandomIntegerThatIsntAnother(min, max, previousIndexes);
	}
}
