package de.riiqz.xnme.minigames.core.message;

import com.google.common.collect.Maps;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.message.IMessageJsonManager;
import de.riiqz.xnme.minigames.core.data.FileBuilder;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;

public class MessageJsonManager implements IMessageJsonManager {

	private final String MESSAGE_JSON_FILE_NAME = "messages.json";
	
	public Map<String, String> loadMessages() {
		JSONObject jsonObject = FileBuilder.loadJSONObject(MESSAGE_JSON_FILE_NAME, false);
		return loadStringMapFromJsonObject(jsonObject);
	}
	
	@SuppressWarnings("unchecked")
	public void addMessageToJson(String identifier, String message) {
		File file = Paths.get(MiniGame.getJavaPlugin().getDataFolder().getAbsolutePath(), MESSAGE_JSON_FILE_NAME).toFile();
		JSONObject jsonObject = FileBuilder.loadJSONObject(MESSAGE_JSON_FILE_NAME, false);

		try {
			jsonObject.put(identifier, message);

			try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
				jsonObject.writeJSONString(writer);
				writer.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, String> loadStringMapFromJsonObject(JSONObject jsonObject) {
		Map<String, String> stringHashMap = Maps.newHashMap();
		
		for(Object object : jsonObject.keySet()) {
			stringHashMap.put(object.toString(), jsonObject.get(object).toString());
		}
		return stringHashMap;
	}

}
