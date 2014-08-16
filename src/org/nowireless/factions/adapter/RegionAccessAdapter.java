package org.nowireless.factions.adapter;

import java.lang.reflect.Type;
import java.util.Set;

import org.nowireless.factions.RegionAccess;

import com.massivecraft.massivecore.xlib.gson.JsonDeserializationContext;
import com.massivecraft.massivecore.xlib.gson.JsonDeserializer;
import com.massivecraft.massivecore.xlib.gson.JsonElement;
import com.massivecraft.massivecore.xlib.gson.JsonObject;
import com.massivecraft.massivecore.xlib.gson.JsonParseException;
import com.massivecraft.massivecore.xlib.gson.JsonSerializationContext;
import com.massivecraft.massivecore.xlib.gson.JsonSerializer;
import com.massivecraft.massivecore.xlib.gson.reflect.TypeToken;

public class RegionAccessAdapter implements JsonDeserializer<RegionAccess>, JsonSerializer<RegionAccess> {
	public static final String ASSOCIATED_REGION = "associatedRegion";

	public static final Type SET_OF_STRING_TYPE = new TypeToken<Set<String>>(){}.getType();

	private static RegionAccessAdapter i = new RegionAccessAdapter();
	public static RegionAccessAdapter get() { return i; }

	@Override
	public JsonElement serialize(RegionAccess src, Type typeOfSrc, JsonSerializationContext context) {
		if(src == null) return null;
		JsonObject obj = new JsonObject();

		//obj.addProperty(HOST_OWNER_FACTION_ID, src.getOwnerFactionId());
		//obj.addProperty(REGION_CLAIM_STATE, src.isClaimale());
		obj.addProperty(ASSOCIATED_REGION, src.getAssociatedRegionID());

		return obj;
	}

	@Override
	public RegionAccess deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();

		String associatedRegionID = null;

		JsonElement element = null;
		element = obj.get("RegionID");
		if(element == null) element = obj.get(ASSOCIATED_REGION);
		associatedRegionID = element.getAsString();

		return RegionAccess.valueOf(associatedRegionID);
	}
}
