package com.lifeknight.modbase.utilities;

import com.lifeknight.modbase.gui.hud.EnhancedHudText;
import com.lifeknight.modbase.variables.LifeKnightCycle;
import com.lifeknight.modbase.variables.LifeKnightNumber;
import net.minecraft.util.EnumChatFormatting;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static com.lifeknight.modbase.gui.hud.EnhancedHudText.textToRender;
import static net.minecraft.util.EnumChatFormatting.*;

public class Miscellaneous {
	public static int getRandomIntBetweenRange(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static double getRandomDoubleBetweenRange(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	public static String getCurrentDateString() {
		return new SimpleDateFormat("MM/dd/yyyy").format(System.currentTimeMillis());
	}

	public static String getCurrentTimeString() {
		return new SimpleDateFormat("hh:mm:ss a").format(System.currentTimeMillis());
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

	public static void createEnhancedHudTextDefaultPropertyVariables() {
		new LifeKnightNumber.LifeKnightFloat("Default Text Scale", "HUD", 1.0F, 0.1F, 5.0F) {
			@Override
			public void onSetValue() {
				for (EnhancedHudText enhancedHudText : textToRender) {
					enhancedHudText.setScale(this.getValue());
				}
			}
		}.setiCustomDisplayString(objects -> {
			float value = (float) objects[0];
			return "Scale: " + value * 100 + "%";
		});;
		new LifeKnightCycle("Default Separator", "HUD", Arrays.asList(" > ", ": ", " | ", " - ")) {
			@Override
			public void onValueChange() {
				for (EnhancedHudText enhancedHudText : textToRender) {
					enhancedHudText.setSeparator(this.getValue());
				}
			}

			@Override
			public String getCustomDisplayString() {
				return "Default Separator:" + YELLOW + (this.getCurrentValueString().equals(":") ? " :" : this.getCurrentValueString());
			}
		};
		new LifeKnightCycle("Default Prefix Color", "HUD", Arrays.asList(
				"Red",
				"Gold",
				"Yellow",
				"Green",
				"Aqua",
				"Blue",
				"Light Purple",
				"Dark Red",
				"Dark Green",
				"Dark Aqua",
				"Dark Blue",
				"Dark Purple",
				"White",
				"Gray",
				"Dark Gray",
				"Black"
		), 12) {
			@Override
			public void onValueChange() {
				for (EnhancedHudText enhancedHudText : textToRender) {
					enhancedHudText.setPrefixColor(this.getValue());
				}
			}

			@Override
			public String getCustomDisplayString() {
				return "Default Prefix Color: " + Miscellaneous.getEnumChatFormatting(this.getCurrentValueString()) + this.getCurrentValueString();
			}
		};
		new LifeKnightCycle("Default Content Color", "HUD", Arrays.asList(
				"Red",
				"Gold",
				"Yellow",
				"Green",
				"Aqua",
				"Blue",
				"Light Purple",
				"Dark Red",
				"Dark Green",
				"Dark Aqua",
				"Dark Blue",
				"Dark Purple",
				"White",
				"Gray",
				"Dark Gray",
				"Black"
		), 12) {
			@Override
			public void onValueChange() {
				for (EnhancedHudText enhancedHudText : textToRender) {
					enhancedHudText.setContentColor(this.getValue());
				}
			}

			@Override
			public String getCustomDisplayString() {
				return "Default Content Color: " + Miscellaneous.getEnumChatFormatting(this.getCurrentValueString()) + this.getCurrentValueString();
			}
		};
	}
}
