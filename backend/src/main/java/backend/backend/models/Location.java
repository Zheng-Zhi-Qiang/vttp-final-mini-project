package backend.backend.models;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Location {
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public static Location toLocation(Document doc){
        Location loc = new Location();
        loc.setLatitude(doc.get("location", Document.class).getDouble("lat"));
        loc.setLongitude(doc.get("location", Document.class).getDouble("lng"));
        return loc;
    }

    public static Location toLocation(JsonObject locData){
        Location loc = new Location();
        loc.setLatitude(Double.parseDouble(locData.get("lat").toString()));
        loc.setLongitude(Double.parseDouble(locData.get("lng").toString()));
        return loc;
    }

    public JsonObject toJson(){
        return Json.createObjectBuilder()
                    .add("lat", latitude)
                    .add("lng",longitude)
                    .build();
    }
}
