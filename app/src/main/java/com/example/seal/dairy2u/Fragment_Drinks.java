package com.example.seal.dairy2u;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class Fragment_Drinks extends Fragment {
    GridView g;

    public Fragment_Drinks() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_layout, container, false);

        g = (GridView) v.findViewById(R.id.grid);
        g.setAdapter(new GridView_Adapter(getActivity()));

        return v;

    }


}
