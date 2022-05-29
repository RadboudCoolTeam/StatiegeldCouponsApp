package io.github.textrecognisionsample.model.web;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class WebUserJsonSerializer implements JsonSerializer<WebUser> {

    @Override
    public JsonElement serialize(WebUser src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("id", context.serialize(src.id));
        object.add("name", context.serialize(src.name));
        object.add("email", context.serialize(src.email));
        object.add("password", context.serialize(src.password));
        object.add("passwordHash", context.serialize(src.passwordHash));

        return object;
    }
}
