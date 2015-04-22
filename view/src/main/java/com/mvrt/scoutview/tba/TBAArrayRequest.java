package com.mvrt.scoutview.tba;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

/**
 * Created by Akhil on 4/15/2015.
 */
public class TBAArrayRequest extends JsonArrayRequest{

    public static final String URL = "http://www.thebluealliance.com/api/v2/";
    public static final String X_TBA_ID = "frc115:scouting-admin:v01.5";
    public static final String URL_SUFFIX = "?X-TBA-App-Id=" + X_TBA_ID;

    public TBAArrayRequest(String path, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener){
        super(URL + path + URL_SUFFIX, listener, errorListener);
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, "UTF-8");
            new String(response.data);
            return Response.success(new JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        }catch(UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch(JSONException je) {
            return Response.error(new ParseError(je));

        }
    }

}
