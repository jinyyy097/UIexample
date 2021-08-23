package com.example.uiexample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uiexample.databinding.FragmentDetailBinding;
import com.example.uiexample.databinding.FragmentLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;


public class DetailFragment extends Fragment {

    private ActivityResultLauncher callback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == Activity.RESULT_OK)
                {
                    getActivity().setIntent(result.getData());
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.backToFragment();
                }
            });
    private FragmentDetailBinding binding;
    private  String packagename = "com.example.jsonexample";
    private JSONObject jsonObject;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (getArguments() != null)
        {
            try {
                jsonObject = new JSONObject(getArguments().getString("json"));
                binding.nameview.setText(jsonObject.getString("BIZPLC_NM"));
                binding.latview.setText(jsonObject.getString("REFINE_WGS84_LAT"));
                binding.lngview.setText(jsonObject.getString("REFINE_WGS84_LOGT"));
                binding.addressview.setText(jsonObject.getString("REFINE_ROADNM_ADDR"));
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ComponentName cn = new ComponentName(packagename,packagename+".MainActivity");

                Intent intent = new Intent(Intent.ACTION_MAIN);

                intent.addCategory(Intent.CATEGORY_LAUNCHER);

                intent.setComponent(cn);
                intent.putExtra("json",jsonObject.toString());


                callback.launch(intent);

            }
        });
        return view;
    }
}