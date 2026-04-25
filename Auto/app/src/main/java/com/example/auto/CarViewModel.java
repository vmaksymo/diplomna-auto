package com.example.auto;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CarViewModel extends ViewModel {
    private static final String LOCAL_HOST = "http://192.168.8.2";
    private String username;
    private final MutableLiveData<Car> car_data = new MutableLiveData<>();
    public LiveData<Car> get_car_data() {
        return car_data;
    }
    private final MutableLiveData<List<Car>> car_list = new MutableLiveData<>();
    public LiveData<List<Car>> get_car_list(){
        return car_list;
    }
    public void set_username(String username){
        this.username = username;
    }

    public void get_data(String platenumber) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = new String[1];
            field[0] = "platenumber";
            String[] data = new String[1];
            data[0] = platenumber;

            PutData putData = new PutData(LOCAL_HOST.concat("/DatabaseAuto/get_car_data.php"), "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    try {
                        JSONObject object = new JSONObject(result);
                        String image = object.getString("image");
                        String model = object.getString("model");
                        String insurance = object.getString("insurance");
                        String vignette = object.getString("vignette");
                        String inspection = object.getString("inspection");

                        // ✅ update LiveData
                        car_data.postValue(new Car(image, model, platenumber, insurance, vignette, inspection));
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    public void insert_data(String model, String platenumber, String insurance, String vignette, String inspection){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = new String[5];
            field[0] = "model";
            field[1] = "platenumber";
            field[2] = "insurance";
            field[3] = "vignette";
            field[4] = "inspection";
            String[] data = new String[5];
            data[0] = model;
            data[1] = platenumber;
            data[2] = insurance;
            data[3] = vignette;
            data[4] = inspection;

            PutData putData = new PutData(LOCAL_HOST.concat("/DatabaseAuto/insert_car_data.php"), "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    Log.d("InsertingCarChecker","insert_data() | result = " + result);
                    if (result.equals("Car Add Success")) {
                        tie_car_to_user(username, platenumber);
                    }
                }
            }
        });
    }


    public void update_data(String model, String platenumber, String insurance, String vignette, String inspection){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = new String[5];
            field[0] = "model";
            field[1] = "platenumber";
            field[2] = "insurance";
            field[3] = "vignette";
            field[4] = "inspection";
            String[] data = new String[5];
            data[0] = model;
            data[1] = platenumber;
            data[2] = insurance;
            data[3] = vignette;
            data[4] = inspection;

            PutData putData = new PutData(LOCAL_HOST.concat("/DatabaseAuto/update_car_data.php"), "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    Log.d("UpdatingCarChecker","update_data() | result = " + result);
                }
            }
        });
    }
    public void delete_data(String platenumber){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = new String[1];
            field[0] = "platenumber";
            String[] data = new String[1];
            data[0] = platenumber;

            PutData putData = new PutData(LOCAL_HOST.concat("/DatabaseAuto/delete_car_data.php"), "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    Log.d("DeletingCarChecker","delete_data() | result = " + result);
                }
            }
        });
    }

    public void insert_image(String image, String platenumber){
        if(image != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                String[] field = new String[2];
                field[0] = "image";
                field[1] = "platenumber";
                String[] data = new String[2];
                data[0] = image;
                data[1] = platenumber;

                PutData putData = new PutData(LOCAL_HOST.concat("/DatabaseAuto/insert_car_image.php"), "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        Log.d("InsertingCarImageChecker","insert_image() | result = " + result);
                    }
                }
            });
        }
    }

    public void remove_image(String platenumber){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = new String[1];
            field[0] = "platenumber";
            String[] data = new String[1];
            data[0] = platenumber;

            PutData putData = new PutData(LOCAL_HOST.concat("/DatabaseAuto/remove_car_image.php"), "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    Log.d("RemovingCarImageChecker","remove_image() | result = " + result);
                }
            }
        });
    }

    public void tie_car_to_user(String username, String platenumber){
        Handler handler = new Handler();
        handler.post(() -> {
            String[] field = new String[2];
            field[0] = "username";
            field[1] = "platenumber";
            String[] data = new String[2];
            data[0] = username;
            data[1] = platenumber;
            PutData putData = new PutData(LOCAL_HOST.concat("/DatabaseAuto/add_car_for_user.php"), "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                }
            }
        });
    }

    public void get_cars_for_user(String username) {
        Handler handler = new Handler();
        handler.post(() -> {
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
                            Log.d("GetCarDataChecker","get_cars_for_user() | car.platenumber = " + car.platenumber);
                        }
                        Log.d("GetCarDataChecker","get_cars_for_user() | cars.size() = " + cars.size());
                        car_list.postValue(cars);

                    } catch (Exception e) {
                    }
                }
            }
        });
    }




}
