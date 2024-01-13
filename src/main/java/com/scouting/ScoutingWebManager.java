package com.scouting;

import com.google.gson.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;


@Singleton
public class ScoutingWebManager {

    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String CONTENT_TYPE_JSON = "application/json";
    protected static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private ScoutingPlugin plugin;

    @Inject
    private ScoutingConfig config;

    @Inject
    private GsonBuilder gsonBuilder;

    private Logger logger = LoggerFactory.getLogger(ScoutingWebManager.class);

    protected void postEvents() {
        try {
            // Oracle cloud only handles 1 JSON object to be posted at a time
            // We could change this if needed, but in the vast majority of cases, only 1 event is likely to be uploaded
            // at a time
            for (EventData eventData : plugin.getEventsToUpload()) {
                Request r = new Request.Builder()
                        .url(config.postEventsEndpoint())
                        .addHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
                        .post(RequestBody.create(MEDIA_TYPE_JSON, getGson().toJson(eventData)))
                        .build();

                okHttpClient.newCall(r).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        logger.error("Failed to post event", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        try {
                            if (!response.isSuccessful())
                                logger.error("On post response error " + response.body().string());
                        }
                        catch (Exception e) {
                            logger.error("POST responded unsuccessful ", e);
                        }
                        finally {
                            response.close();
                        }
                    }
                });
            }
        } catch (Exception e) {
            logger.error("Outer catch block POST ", e);
        }
        plugin.getEventsToUpload().clear();
    }

    private Gson getGson() {
        return gsonBuilder.registerTypeAdapter(Instant.class, new InstantSecondsConverter()).create();
    }

    /**
     * Serializes/Deserializes {@link Instant} using {@link Instant#getEpochSecond()}/{@link Instant#ofEpochSecond(long)}
     */
    private static class InstantSecondsConverter implements JsonSerializer<Instant>, JsonDeserializer<Instant>
    {
        @Override
        public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.getEpochSecond());
        }

        @Override
        public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            return Instant.ofEpochSecond(json.getAsLong());
        }
    }
}
