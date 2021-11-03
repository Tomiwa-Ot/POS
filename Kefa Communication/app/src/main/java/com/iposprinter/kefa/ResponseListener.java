package com.iposprinter.kefa;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ResponseListener {

    void gotResponse(JSONObject object);
    void historyResponse(JSONArray obj);

}
