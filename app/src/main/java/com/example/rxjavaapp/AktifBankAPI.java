package com.example.rxjavaapp;

import com.google.gson.annotations.SerializedName;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AktifBankAPI {
    @Headers({
            "X-IBM-Client-Id: f4d5239e0de729e7de80554f6e2452bd",
            "Content-Type: application/json",
            "Accept: application/json"
    })
    @POST("Login")
    Observable<retrofit2.Response<LoginResponseModel>> loginRx(@Body LoginRequestModel loginRequest);

    class LoginRequestModel {
        @SerializedName("LANGUAGE")
        private String language;
        @SerializedName("CLIENT_SESSION_ID")
        private int clientSessionId;
        @SerializedName("DEVICE_ID")
        private String deviceId;

        public LoginRequestModel(String language, int clientSessionId, String deviceId) {
            this.language = language;
            this.clientSessionId = clientSessionId;
            this.deviceId = deviceId;
        }
    }

    class LoginResponseModel {
        @SerializedName("SESSION_ID")
        private String sessionId;

        public String getSessionId() {
            return sessionId;
        }
    }
}
