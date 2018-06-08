package com.app.studentessentials.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.studentessentials.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUtilityCalculator extends Fragment {

    EditText edt_previous_meter_reading, edt_latest_meter_reading, edt_cost_per_kwh, edt_standing_charges, edt_days, edt_vat;
    TextView edt_total_kwh, edt_total_standing_charges, edt_total_vat, edt_total_charges;
    Button btn_calculate;
    View view;

    public FragmentUtilityCalculator() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment_utility_calculator, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // Inflate the layout for this fragment
        initilizeView();

        edt_vat.setText("5");

        btn_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fomeValidation()){
                    int total_KWH = Integer.parseInt(edt_latest_meter_reading.getText().toString()) - Integer.parseInt(edt_previous_meter_reading.getText().toString());

                    Float standing_charges = Float.parseFloat(edt_standing_charges.getText().toString()) * Float.parseFloat(edt_days.getText().toString());

                    Float value = (total_KWH* (Float.parseFloat(edt_cost_per_kwh.getText().toString().trim()))) + standing_charges;
                    Float vat = (float)(value*(Float.parseFloat(edt_vat.getText().toString().trim())/100.0f));
                    Float total_charges = (total_KWH* (Float.parseFloat(edt_cost_per_kwh.getText().toString().trim()))) + standing_charges+vat;

                    edt_total_kwh.setText(Integer.toString(total_KWH));
                    edt_total_standing_charges.setText(Float.toString(standing_charges));
                    edt_total_charges.setText(Float.toString(total_charges));
                    edt_total_vat.setText(Float.toString(vat));
                }
            }
        });

        return view;
    }

    public void  initilizeView(){
        edt_previous_meter_reading = (EditText) view.findViewById(R.id.edt_previous_meter_reading);
        edt_latest_meter_reading = (EditText) view.findViewById(R.id.edt_latest_meter_reading);
        edt_cost_per_kwh = (EditText) view.findViewById(R.id.edt_cost_per_kwh);
        edt_standing_charges = (EditText) view.findViewById(R.id.edt_standing_charges);
        edt_days = (EditText) view.findViewById(R.id.edt_days);
        edt_vat = (EditText) view.findViewById(R.id.edt_vat);

        edt_total_kwh = (TextView) view.findViewById(R.id.edt_total_kwh);
        edt_total_standing_charges = (TextView) view.findViewById(R.id.edt_total_standing_charges);
        edt_total_vat = (TextView) view.findViewById(R.id.edt_total_vat);
        edt_total_charges = (TextView) view.findViewById(R.id.edt_total_charges);

        btn_calculate = (Button) view.findViewById(R.id.btn_calculate);
    }

    public boolean fomeValidation()
    {
        boolean flag = true;
        if(!edt_previous_meter_reading.getText().toString().trim().equals("")){
            if(!edt_latest_meter_reading.getText().toString().trim().equals("")){
                if(!edt_cost_per_kwh.getText().toString().trim().equals("")){
                    if(!edt_standing_charges.getText().toString().trim().equals("")){
                        if(!edt_days.getText().toString().trim().equals("")){
                            if(!edt_vat.getText().toString().trim().equals("")){

                            }
                            else{ flag = false;
                                Toast.makeText(getActivity(), "Please enter Vat..!", Toast.LENGTH_SHORT).show(); }
                        }
                        else{ flag = false;
                            Toast.makeText(getActivity(), "Please enter Days..!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        flag = false;
                        Toast.makeText(getActivity(), "Please enter Standing charges..!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    flag = false;
                    Toast.makeText(getActivity(), "Please enter cost per KWH..!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                flag = false;
                Toast.makeText(getActivity(), "Please enter latest Meter Reading..!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            flag = false;
            Toast.makeText(getActivity(), "Please enter previous Meter Reading..!", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }



}
