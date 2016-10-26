package ecommerce.prakhar.example.com.e_commerce.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ecommerce.prakhar.example.com.e_commerce.R;

/**
 * Created by Prakhar Gupta on 13/10/2016.
 */

public class Fragment5 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment5, container, false);
        return view;
    }
}
