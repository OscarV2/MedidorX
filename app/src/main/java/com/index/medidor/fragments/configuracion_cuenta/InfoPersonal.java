package com.index.medidor.fragments.configuracion_cuenta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoPersonal.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoPersonal#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoPersonal extends Fragment {

    private EditText edtNombresApellidos;
    private EditText edtCorreo;
    private EditText edtTelefono;
    private EditText edtPaisCiudad;
    private EditText edtPassword;

    private Button btnGuardarCambios;

    private SharedPreferences myPreferences;

    private ImageView imgUsuario;

    private MainActivity mainActivity;

    private OnFragmentInteractionListener mListener;

    final int FROM_STORAGE = 2;

    public InfoPersonal() {
        // Required empty public constructor
    }

    public InfoPersonal(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoPersonal.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoPersonal newInstance(String param1, String param2) {

        return new InfoPersonal();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info_personal, container, false);

        edtCorreo = v.findViewById(R.id.edt_ip_correo);
        edtNombresApellidos = v.findViewById(R.id.edt_ip_nombres_apellidos);
        edtPaisCiudad = v.findViewById(R.id.edt_ip_pais_ciudad);
        edtPassword = v.findViewById(R.id.edt_ip_password);
        edtTelefono = v.findViewById(R.id.edt_ip_numero_telefono);
        imgUsuario = v.findViewById(R.id.img_info_usuario);
        btnGuardarCambios = v.findViewById(R.id.btn_ip_guardar_cambios);

        btnGuardarCambios.setOnClickListener(v1 -> guardarCambios());

        if (mainActivity != null){
            myPreferences = mainActivity.getMyPreferences();

            edtCorreo.setText(myPreferences.getString("email",""));
            edtTelefono.setText(myPreferences.getString("celular",""));
            edtPaisCiudad.setText(myPreferences.getString("paisCiudad",""));
            String nombres = myPreferences.getString("nombres","");
            String apellidos = myPreferences.getString("apellidos","");

            String imagePath = myPreferences.getString("fotoUsuario","");

            mainActivity.setImageInfoPersonal(imagePath, imgUsuario);
            if (imagePath != null && imagePath.length() > 1){

                    Bitmap bitmap = decodeBase64(imagePath);
                    imgUsuario.setImageBitmap(bitmap);

            }else{
                imgUsuario.setImageResource(R.mipmap.ic_launcher);
            }

            edtNombresApellidos.setText(nombres + " " + apellidos);
        }

        imgUsuario.setOnClickListener(v1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK);

            intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");

            Toast.makeText(mainActivity, "Select from Library", Toast.LENGTH_SHORT).show();

            startActivityForResult(Intent.createChooser(intent, "Select File"), FROM_STORAGE);


        });
        return v;
    }

    private void guardarCambios(){

        SharedPreferences.Editor editor = myPreferences.edit();

        editor.putString("email", edtCorreo.getText().toString());
        editor.putString("paisCiudad", edtPaisCiudad.getText().toString());
        editor.putString("celular", edtTelefono.getText().toString());

        editor.apply();

        Toast.makeText(mainActivity, "Cambios guardados exitosamente.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == FROM_STORAGE){

                Uri selectedImageUri = data.getData();

                try {
                    if (selectedImageUri != null){


                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(mainActivity.getContentResolver(),
                                selectedImageUri);
                        imgUsuario.setImageBitmap(bitmap);
                        SharedPreferences.Editor editor = myPreferences.edit();

                        editor.putString("fotoUsuario", bitmapToBase64(bitmap));
                        editor.apply();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private String bitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte[] arr = baos.toByteArray();
        return Base64.encodeToString(arr, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0,   decodedByte.length);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
