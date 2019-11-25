package com.index.medidor.retrofit;

import com.index.medidor.model.Estaciones;
import com.index.medidor.model.MarcaCarros;
import com.index.medidor.model.ModeloCarros;
import com.index.medidor.model.Recorrido;
import com.index.medidor.model.Tanqueadas;
import com.index.medidor.model.Usuario;
import com.index.medidor.model.UsuarioHasModeloCarro;
import com.index.medidor.utils.Constantes;
import com.index.medidor.utils.ResponseServices;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SmartBillApiServices {

    @POST(Constantes.POST_LOGIN_USER)
    Call<Usuario> postLogin(@Header("Content-Type") String headerContentType,
                            @Body Usuario usuario);

    @POST(Constantes.POST_REGISTER_USER)
    Call<ResponseServices> postRegisterUser(@Header("strUsuario") String jsonUsuario,
                                            @Body String dummy);

    //ESTACIONES
    @GET(Constantes.GET_ALL_ESTACIONES)
    Call<List<Estaciones>> getEstaciones();

    @POST(Constantes.POST_REGISTER_STATION)
    Call<ResponseServices> postRegisterStation(@Header("Content-Type") String headerContentType,
                                                 @Body Estaciones estaciones);

    //TANQUEADAS
    @POST(Constantes.POST_REGISTRAR_TANQUEADA)
    Call<ResponseServices> postRegisterTanqueada(@Header("Content-Type") String headerContentType,
                                                 @Body Tanqueadas tanqueada);

    @GET(Constantes.GET_TANQUEADAS_BY_USER + "{id}")
    Call<List<Tanqueadas>> getTanqueadasByUser(@Path("id") String id);

    /**
     * USUARIO HAS MODELO CARROS
     */
    @POST(Constantes.POST_REGISTRAR_USUARIO_HAS_MODELO_CARRO + "{idMarca}" + "/" + "{linea}")
    Call<UsuarioHasModeloCarro> postRegisterUsuarioHasModeloCarro(@Header("Content-Type") String headerContentType,
                                                         @Path("idMarca") String idMarca,
                                                         @Path("linea") String linea,
                                          @Body UsuarioHasModeloCarro uhmc);

    @PUT(Constantes.PUT_UPDATE_USUARIO_HAS_MODELO_CARRO)
    Call<UsuarioHasModeloCarro> putUpdateUsuarioHasModeloCarro(@Header("Content-Type") String headerContentType,
                                                         @Body UsuarioHasModeloCarro uhmc);

    @GET(Constantes.GET_USUARIO_HAS_MODELO_CARROS_BY_ID_USER + "{idUsuario}")
    Call<List<UsuarioHasModeloCarro>> getUsuarioHasModeloCarros(@Path("idUsuario") String idUsuario);

    /**
     * MODELOS CARROS
     */
    @POST(Constantes.POST_REGISTER_MODELO_CARRO)
    Call<ModeloCarros> postRegisterModelo(@Header("Content-Type") String headerContentType,
                                                 @Body ModeloCarros modeloCarros);

    @GET(Constantes.GET_MODELOS_CARROS_BY_MARCA + "{idMarca}")
    Call<List<ModeloCarros>> getModelosCarrosByMarca(@Path("idMarca") String idMarca);

    /**
     * MARCAS CARROS
     */
    @GET(Constantes.GET_MARCAS_CARROS)
    Call<List<MarcaCarros>> getMarcasCarros();

    /**
     * RECORRIDOS
     */
    @POST(Constantes.POST_REGISTRAR_RECORRIDO)
    Call<String> postRegisterRecorrido(@Header("Content-Type") String headerContentType,
                                             @Body Recorrido recorrido);


    @POST(Constantes.POST_RECORRIDOS_BULK)
    Call<ResponseServices> postRecorridosBulk(@Header("Content-Type") String headerContentType,
                                       @Body List<Recorrido> recorridos);

}
