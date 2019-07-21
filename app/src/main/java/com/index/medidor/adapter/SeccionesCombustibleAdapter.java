package com.index.medidor.adapter;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SeccionesCombustibleAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> titulosList = new ArrayList<>();

    public SeccionesCombustibleAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String titulo){

        fragmentList.add(fragment);
        titulosList.add(titulo);
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titulosList.get(position);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return titulosList.size();
    }
}
