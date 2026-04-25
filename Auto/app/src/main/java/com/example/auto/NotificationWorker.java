package com.example.auto;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NotificationWorker extends Worker {

    private static final String LOCAL_HOST = "http://192.168.8.2";
    private String username;
    Handler mainHandler = new Handler(Looper.getMainLooper());
    Notifications notification = new Notifications();
    Context context;
    String today = LocalDate.now().toString();
    String three_days_from_today = LocalDate.now().plusDays(3).toString();
    String one_week_from_today = LocalDate.now().plusWeeks(1).toString();
    String title;
    String message;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        username = getInputData().getString("username");
        context = getApplicationContext();

        get_cars_for_user(username);

        notification.show_notification(context, "Test 2", "message");
        Log.d("NotificationWorker_doWork", "Notificaion 1");

        return Result.success();
    }

    public void check_and_send(String model_platenumber, String _end_date, String type){
        Boolean send_notification;
        if(_end_date.equals(today)){
            title = String.format("%s is ending today", type);
            send_notification = true;
        } else if(_end_date.equals(three_days_from_today)){
            title = String.format("%s will end on %s", type, three_days_from_today);
            send_notification = true;
        } else if(_end_date.equals(one_week_from_today)){
            title = String.format("%s will end on %s", type, one_week_from_today);
            send_notification = true;
        } else send_notification = false;
        if(send_notification) {
            message = String.format("[%s]", model_platenumber);
            notification.show_notification(context, title, message);
        }
    }
    public void get_cars_for_user(String username) {
        String[] field = new String[1];
        field[0] = "username";
        String[] data = new String[1];
        data[0] = username;
        PutData putData = new PutData(LOCAL_HOST.concat("/DatabaseAuto/get_cars_for_user.php"), "POST", field, data);
        if (putData.startPut()) {
            if (putData.onComplete()) {
                String result = putData.getResult();
                try {
                    JSONArray array = new JSONArray(result);
                    List<Car> cars = new ArrayList<>();
                    for(int i = 0; i < array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        Car car = new Car(
                                object.getString("image"),
                                object.getString("model"),
                                object.getString("platenumber"),
                                object.getString("insurance"),
                                object.getString("vignette"),
                                object.getString("inspection")
                            );
                        cars.add(car);
                        check_and_send(car.get_model_plus_platenumber(),car.get_insurance(),"Insurance");
                        check_and_send(car.get_model_plus_platenumber(),car.get_vignette(),"Vignette");
                        check_and_send(car.get_model_plus_platenumber(),car.get_inspection(),"Inspection");
                        Log.d("NotificationWorker","get_cars_for_user() | car.platenumber = " + car.platenumber);
                    }
                } catch (Exception e) {
                }
            }
        }
    }
}
