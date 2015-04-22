package com.example.scoutingadmin;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Akhil on 4/15/2015.
 */
public class TBAObjectRequest extends JsonObjectRequest{

    public static final String URL = "http://www.thebluealliance.com/api/v2/";
    public static final String X_TBA_ID = "frc115:scouting-admin:v01.5";
    public static final String URL_SUFFIX = "?X-TBA-App-Id=" + X_TBA_ID;

    public TBAObjectRequest(String path, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        super(URL + path + URL_SUFFIX, null, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, "UTF-8");
            new String(response.data);
            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        }catch(UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch(JSONException je) {
            return Response.error(new ParseError(je));

        }
    }

}
