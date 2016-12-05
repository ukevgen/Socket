import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by ukevgen on 06.11.2016.
 */
public class Weather {

    private static final String URL = "http://api.openweathermap.org/data/2.5/find?" +
            "q=Dnipropetrovsk" +
            "&APPID=6549ddea7eea1f5e33c18d552b0c2837" +
            "&units=metric";
    private static final String LIST = "list";
    private static final String MAIN = "main";
    private static final String TEMP = "temp";
    private static final String HUMIDITY = "humidity";

    OkHttpClient client = new OkHttpClient();

    /**
     * Get current temp from api
     */
    public String getTemp() {
        JsonObject object = init();
        if (object != null)
            return String.valueOf(object.get(MAIN).getAsJsonObject().get(TEMP).getAsDouble());
        else
            return TEMP;
    }

    /**
     * Get humidity temp from api
     */
    public String getHumidity() {
        JsonObject object = init();
        if (object != null)
            return String.valueOf(object.get(MAIN).getAsJsonObject().get(HUMIDITY).getAsDouble());
        else
            return HUMIDITY;
    }

    /**
     * Parse object
     */
    private JsonObject init() {
        String response = null;
        JsonObject nextObject = null;
        try {
            response = run(URL);
            JsonParser parser = new JsonParser();
            JsonObject mainObject = parser.parse(response).getAsJsonObject();
            JsonArray array = mainObject.getAsJsonArray(LIST);
            nextObject = array.get(0).getAsJsonObject();
        } catch (IOException e) {
            System.out.println("Incorrect url");
        }
        return nextObject;
    }

    /**
     * Create request
     */
    private String run(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
