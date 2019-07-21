package com.index.medidor.fragments.combustible;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.index.medidor.R;
import com.index.medidor.activities.MainActivity;
import com.index.medidor.adapter.SeccionesCombustibleAdapter;
import com.index.medidor.utils.Constantes;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CombustibleTabs.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CombustibleTabs#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CombustibleTabs extends Fragment {

    private OnFragmentInteractionListener mListener;

    private AppBarLayout appBar;
    private TabLayout tabs;
    private MainActivity mainActivity;

    public CombustibleTabs() {
        // Required empty public constructor
    }

    public CombustibleTabs(MainActivity m) {
        // Required empty public constructor
        this.mainActivity = m;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CombustibleTabs.
     */
    // TODO: Rename and change types and number of parameters
    public static CombustibleTabs newInstance(String param1, String param2) {
        CombustibleTabs fragment = new CombustibleTabs();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_combustible_tabs, container, false);

        if (Constantes.ROTATION == 0) {

            View parent = (View)container.getParent();
            if (appBar == null){

                appBar = parent.findViewById(R.id.app_bar);
                tabs = new TabLayout(Objects.requireNonNull(getActivity()));
                tabs.setTabTextColors(Color.parseColor("#FFFFFF"),Color.parseColor("#FFFFFF"));
                tabs.setSelectedTabIndicatorColor(Color.parseColor("#4A86E8"));
                tabs.setSelectedTabIndicator(R.drawable.custom_indicator);
                appBar.addView(tabs);

                ViewPager viewPager = vista.findViewById(R.id.vp_combustible);
                llenarViewPager(viewPager);
                viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }


                });
                tabs.setupWithViewPager(viewPager);
        }else{
                Constantes.ROTATION = 1;
            }

        }
        return vista;
    }

    private void llenarViewPager(ViewPager viewPager) {

        SeccionesCombustibleAdapter adapter = new SeccionesCombustibleAdapter(getFragmentManager());
        adapter.addFragment(new IngresadoFragment(this.mainActivity), "Ingresado");
        adapter.addFragment(new RendimientoFragment(), "Rendimiento");

        viewPager.setAdapter(adapter);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Constantes.ROTATION == 0){
            appBar.removeView(tabs);

        }
    }
}
