package com.lifeknight.modbase.mod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.lifeknight.modbase.utilities.Chat;
import com.lifeknight.modbase.variables.*;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.lifeknight.modbase.mod.Core.modId;

public class Configuration {
	private JsonObject configurationAsJsonObject = new JsonObject();

	public Configuration() {
		if (configExists()) {
			updateVariablesFromConfiguration();
		}
		updateConfigurationFromVariables();
	}

	private void updateVariablesFromConfiguration() {
		getConfigurationContent();
		for (int i = 0; i < LifeKnightVariable.getVariables().size(); i++) {
			LifeKnightVariable variable = LifeKnightVariable.getVariables().get(i);
			if (variable.isStoreValue()) {
				try {
					if (variable instanceof LifeKnightBoolean) {
						((LifeKnightBoolean) variable).setValue(configurationAsJsonObject.getAsJsonObject(variable.getGroupForConfiguration()).get(variable.getNameForConfiguration()).getAsBoolean());
					} else if (variable instanceof LifeKnightString) {
						((LifeKnightString) variable).setValue(configurationAsJsonObject.getAsJsonObject(variable.getGroupForConfiguration()).get(variable.getNameForConfiguration()).getAsString());
					} else if (variable instanceof LifeKnightNumber) {
						((LifeKnightNumber) variable).setValue(configurationAsJsonObject.getAsJsonObject(variable.getGroupForConfiguration()).get(variable.getNameForConfiguration()).getAsNumber());
					} else if (variable instanceof LifeKnightList<?>) {
						((LifeKnightList<?>) variable).setValueFromJsonArray(configurationAsJsonObject.getAsJsonObject(variable.getGroupForConfiguration()).get(variable.getNameForConfiguration()).getAsJsonArray());
					} else if (variable instanceof LifeKnightCycle) {
						((LifeKnightCycle) variable).setCurrentValue(configurationAsJsonObject.getAsJsonObject(variable.getGroupForConfiguration()).get(variable.getNameForConfiguration()).getAsInt());
					} else if (variable instanceof LifeKnightObject) {
						((LifeKnightObject) variable).setValueFromJsonObject(configurationAsJsonObject.getAsJsonObject(variable.getGroupForConfiguration()).get(variable.getNameForConfiguration()).getAsJsonObject());
					} else if (variable instanceof LifeKnightObjectList) {
						((LifeKnightObjectList) variable).setValueFromJsonArray(configurationAsJsonObject.getAsJsonObject(variable.getGroupForConfiguration()).get(variable.getNameForConfiguration()).getAsJsonArray());
					}
				} catch (Exception e) {
					e.printStackTrace();
					Chat.queueChatMessageForConnection(EnumChatFormatting.RED + "An error occurred while extracting the value of \"" + variable.getName() + "\" from the config; the value will be interpreted as " + variable.getValue() + ".");
				}
			}
		}
	}

	public void updateConfigurationFromVariables() {
		JsonObject configAsJsonReplacement = new JsonObject();

		List<String> groups = new ArrayList<>();

		for (LifeKnightVariable variable : LifeKnightVariable.getVariables()) {
			if (!groups.contains(variable.getGroupForConfiguration()) && variable.isStoreValue()) {
				groups.add(variable.getGroupForConfiguration());
			}
		}

		for (String group : groups) {
			JsonObject jsonObject = new JsonObject();
			for (LifeKnightVariable variable : LifeKnightVariable.getVariables()) {
				if (variable.isStoreValue() && variable.getGroupForConfiguration().equals(group)) {
					if (variable instanceof LifeKnightBoolean) {
						jsonObject.add(variable.getNameForConfiguration(), new JsonPrimitive(((LifeKnightBoolean) variable).getValue()));
					} else if (variable instanceof LifeKnightString) {
						jsonObject.add(variable.getNameForConfiguration(), new JsonPrimitive(((LifeKnightString) variable).getValue()));
					} else if (variable instanceof LifeKnightNumber) {
						jsonObject.add(variable.getNameForConfiguration(), new JsonPrimitive(((LifeKnightNumber) variable).getValue()));
					} else if (variable instanceof LifeKnightList<?>) {
						jsonObject.add(variable.getNameForConfiguration(), ((LifeKnightList<?>) variable).toJsonArray());
					} else if (variable instanceof LifeKnightCycle) {
						jsonObject.add(variable.getNameForConfiguration(), new JsonPrimitive(((LifeKnightCycle) variable).getValue()));
					} else if (variable instanceof LifeKnightObject) {
						jsonObject.add(variable.getNameForConfiguration(), ((LifeKnightObject) variable).getAsJsonObject());
					} else if (variable instanceof LifeKnightObjectList) {
						jsonObject.add(variable.getNameForConfiguration(), ((LifeKnightObjectList) variable).toJsonArray());
					}
				}
			}
			configAsJsonReplacement.add(group, jsonObject);
		}

		configurationAsJsonObject = configAsJsonReplacement;

		writeToConfigurationFile();
	}

	private boolean configExists() {
		try {
			return !new File("config/" + modId + ".json").createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void writeToConfigurationFile() {
		try {
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("config/" + modId + ".json"), StandardCharsets.UTF_8));

			writer.write(configurationAsJsonObject.toString());

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not write in config");
		}
	}

	private void getConfigurationContent() {
		try {
			Scanner reader = new Scanner(new File("config/" + modId + ".json"));
			StringBuilder configContent = new StringBuilder();

			while (reader.hasNextLine()) {
				configContent.append(reader.nextLine()).append(System.getProperty("line.separator"));
			}

			reader.close();

			configurationAsJsonObject = new JsonParser().parse(configContent.toString()).getAsJsonObject();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not read");
		}
	}

	public JsonObject getConfigurationAsJsonObject() {
		return configurationAsJsonObject;
	}
}
