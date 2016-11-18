package org.apache.cordova.globalization;

import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.LOG;
import org.json.JSONException;
import org.json.JSONObject;

public class GlobalizationError extends Exception {
    public static final String FORMATTING_ERROR = "FORMATTING_ERROR";
    public static final String PARSING_ERROR = "PARSING_ERROR";
    public static final String PATTERN_ERROR = "PATTERN_ERROR";
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
    private static final long serialVersionUID = 1;
    int error;

    public GlobalizationError() {
        this.error = 0;
    }

    public GlobalizationError(String s) {
        this.error = 0;
        if (s.equalsIgnoreCase(FORMATTING_ERROR)) {
            this.error = 1;
        } else if (s.equalsIgnoreCase(PARSING_ERROR)) {
            this.error = 2;
        } else if (s.equalsIgnoreCase(PATTERN_ERROR)) {
            this.error = 3;
        }
    }

    public String getErrorString() {
        String msg = "";
        switch (this.error) {
            case CordovaResourceApi.URI_TYPE_FILE /*0*/:
                return UNKNOWN_ERROR;
            case CordovaResourceApi.URI_TYPE_ASSET /*1*/:
                return FORMATTING_ERROR;
            case LOG.VERBOSE /*2*/:
                return PARSING_ERROR;
            case LOG.DEBUG /*3*/:
                return PATTERN_ERROR;
            default:
                return msg;
        }
    }

    public int getErrorCode() {
        return this.error;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("code", getErrorCode());
            obj.put("message", getErrorString());
        } catch (JSONException e) {
        }
        return obj;
    }
}
