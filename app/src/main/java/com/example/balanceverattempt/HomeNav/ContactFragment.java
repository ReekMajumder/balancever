package com.example.balanceverattempt.HomeNav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.balanceverattempt.HomeNav.DrawerLocker;
import com.example.balanceverattempt.R;

public class ContactFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }
}
