package com.example.rxjavaapp;

// Diğer importlar
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.annotations.SerializedName;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://openapi.aktifbank.com.tr/api/dev/bill-payment/";
    private static final String CLIENT_ID = "client_id";
    private Disposable disposable;
    private AktifBankAPI aktifBankAPI; // aktifBankAPI değişkenini burada tanımlayalım

    // API Interface'i tanımla
    interface AktifBankAPI {
        @Headers({
                "X-IBM-Client-Id: client_id",
                "Content-Type: application/json",
                "Accept: application/json"
        })
        @POST("Login")
        Observable<LoginResponseModel> loginRx(@Body LoginRequestModel loginRequest);
    }

    // Yeni metodumuz: Login butonuna tıklandığında çalışacak metod
    public void onLoginButtonClicked(View view) {
        // Örnek veri
        String language = "yo";
        String clientSessionId = "8456899166994432";
        String deviceId = getDeviceId(); // Cihaz kimliğini alın
        LoginRequestModel loginRequest = new LoginRequestModel(language, clientSessionId, deviceId);

        // LOGIN isteğini RxJava ile asenkron olarak gönderin
        Observable<LoginResponseModel> observable = aktifBankAPI.loginRx(loginRequest);
        disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                    String sessionId = loginResponse.getSessionId();
                    Log.d("MainActivity", "SESSION_ID: " + sessionId);

                    // TextView'e SESSION_ID değerini göstermek için güncelleme yapalım
                    TextView sessionIdTextView = findViewById(R.id.session_id_textview);
                    sessionIdTextView.setText("SESSION ID: " + sessionId);
                }, throwable -> {
                    Log.e("MainActivity", "LOGIN isteği başarısız oldu. Hata: " + throwable.getMessage());
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrofit istemcisini oluşturun
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // API arayüzünü alın
        aktifBankAPI = retrofit.create(AktifBankAPI.class);
    }

    // Cihazın benzersiz kimliğini almak için
    private String getDeviceId() {
        // Cihazın benzersiz kimliğini alın
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Cihaz kimliğini SHA-256 formatına dönüştürün
        String hashedDeviceId = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(deviceId.getBytes());
            byte[] byteData = md.digest();
            hashedDeviceId = Base64.encodeToString(byteData, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hashedDeviceId;
    }

    // LOGIN isteği için kullanılacak model sınıfları
    static class LoginRequestModel {
        @SerializedName("LANGUAGE")
        private String language;
        @SerializedName("CLIENT_SESSION_ID")
        private String clientSessionId;
        @SerializedName("DEVICE_ID")
        private String deviceId;

        public LoginRequestModel(String language, String clientSessionId, String deviceId) {
            this.language = language;
            this.clientSessionId = clientSessionId;
            this.deviceId = deviceId;
        }
    }

    static class LoginResponseModel {
        @SerializedName("SESSION_ID")
        private String sessionId;

        public String getSessionId() {
            return sessionId;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Abonelikten kurtulun
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
