package rip.orbit.nebula.util;

import com.google.gson.JsonObject;

public interface JsonDeserializer<T> {

	T deserialize(JsonObject object);

}
