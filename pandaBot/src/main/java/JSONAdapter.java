import org.json.JSONArray;
import org.json.JSONObject;

public class JSONAdapter {
    public JSONAdapter(String jsonArray) {
        JSONObject jsonObject = new JSONObject(jsonArray);
        String stringField = jsonObject.get("Field name").toString();
        int integerField = Integer.parseInt(jsonObject.get("integerField").toString());
        float floatField = Float.parseFloat(jsonObject.get("floatField").toString());
        float roundedFloatField = (float) Math.round(Float.parseFloat(jsonObject.get("floatField").toString())) * 100 / 100; //float that rounded to 2 digits
    }
}
