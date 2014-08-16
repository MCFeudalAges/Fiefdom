package org.nowireless.factions.adapter;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;




import org.nowireless.factions.RegionAccess;

import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.xlib.gson.JsonDeserializationContext;
import com.massivecraft.massivecore.xlib.gson.JsonDeserializer;
import com.massivecraft.massivecore.xlib.gson.JsonElement;
import com.massivecraft.massivecore.xlib.gson.JsonObject;
import com.massivecraft.massivecore.xlib.gson.JsonParseException;
import com.massivecraft.massivecore.xlib.gson.JsonSerializationContext;
import com.massivecraft.massivecore.xlib.gson.JsonSerializer;

public class RegionBoardMapAdapter implements JsonDeserializer<Map<PS, RegionAccess>>, JsonSerializer<Map<PS, RegionAccess>> {
	private static RegionBoardMapAdapter i = new RegionBoardMapAdapter();

	public static RegionBoardMapAdapter get() {
		return i;
	}


	@Override
	public Map<PS, RegionAccess> deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		Map<PS, RegionAccess> ret = new ConcurrentSkipListMap<PS, RegionAccess>();
		JsonObject jsonObject = json.getAsJsonObject();

		for(Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String[] chunkCoordParts = entry.getKey().split("[,\\s]+");
			int chunkX = Integer.parseInt(chunkCoordParts[0]);
			int chunkZ = Integer.parseInt(chunkCoordParts[1]);
			PS chunk = PS.valueOf(chunkX, chunkZ);

			RegionAccess regionAccess = context.deserialize(entry.getValue(), RegionAccess.class);

			ret.put(chunk, regionAccess);

		}
		return ret;
	}

	@Override
	public JsonElement serialize(Map<PS, RegionAccess> src, Type typeOfSrc,
			JsonSerializationContext context) {

		JsonObject ret = new JsonObject();

		for(Entry<PS, RegionAccess> entry : src.entrySet()) {
			PS ps = entry.getKey();
			RegionAccess regionAccess = entry.getValue();

			ret.add(ps.getChunkX().toString() + "," + ps.getChunkZ().toString(), context.serialize(regionAccess, RegionAccess.class));
		}
		return ret;
	}
}
