package com.index.medidor.retrofit;

import com.index.medidor.utils.Constantes;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MedidorApiAdapter {


    private static SmartBillApiServices API_SERVICE;


    public static SmartBillApiServices getApiService() {

        if (API_SERVICE == null){

            OkHttpClient client = new OkHttpClient.Builder().build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constantes.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            API_SERVICE = retrofit.create(SmartBillApiServices.class);
        }

        return API_SERVICE;
    }
}
