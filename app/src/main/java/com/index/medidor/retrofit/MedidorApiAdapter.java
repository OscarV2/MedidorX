package com.index.medidor.retrofit;

import com.index.medidor.utils.Constantes;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.index.medidor.utils.Constantes.TIMEOUT;

public class MedidorApiAdapter {

    private static SmartBillApiServices API_SERVICE;

    public static SmartBillApiServices getApiService() {

        if (API_SERVICE == null){
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constantes.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            API_SERVICE = retrofit.create(SmartBillApiServices.class);
        }
        return API_SERVICE;
    }
}
