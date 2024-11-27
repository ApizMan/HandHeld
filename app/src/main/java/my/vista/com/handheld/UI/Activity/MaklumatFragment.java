package my.vista.com.handheld.UI.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Business.TrustAllCertificates;
import my.vista.com.handheld.Business.VolleySingleton;
import my.vista.com.handheld.Data.DbLocal;
import my.vista.com.handheld.Data.SettingsHelper;
import my.vista.com.handheld.R;
import my.vista.com.handheld.UI.CustomControl.CustomAlertDialog;

/**
 * Created by hp on 24/7/2016.
 */
public class MaklumatFragment extends Fragment {
    ProgressDialog mProgressDialog;
    int retry = 0;

    public MaklumatFragment() {
    }

    public static MaklumatFragment newInstance() {
        MaklumatFragment fragment = new MaklumatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maklumat, container, false);

        final TextInputLayout layVehicleMake = (TextInputLayout)rootView.findViewById(R.id.layTrafikVehicleMake);
        final TextInputLayout layVehicleModel = (TextInputLayout) rootView.findViewById(R.id.layTrafikVehicleModel);
        final EditText etVehicleMake = (EditText) rootView.findViewById(R.id.etTrafikVehicleMake);
        final Spinner spinnerTrafikModel = (Spinner) rootView.findViewById(R.id.spinnerTrafikModel);
        final EditText etVehicleModel = (EditText) rootView.findViewById(R.id.etTrafikVehicleModel);

        Spinner spinnerTrafikJenama = (Spinner) rootView.findViewById(R.id.spinnerTrafikJenama);
        List<String> list = DbLocal.GetListForSpinner(CacheManager.mContext, "VEHICLE_MAKE");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.insert("--Sila Pilih--", 0);
        dataAdapter.insert("LAIN-LAIN", dataAdapter.getCount());
        spinnerTrafikJenama.setAdapter(dataAdapter);
        spinnerTrafikJenama.setSelection(CacheManager.SummonIssuanceInfo.VehicleMakePos);
        spinnerTrafikJenama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                layVehicleMake.setVisibility(View.GONE);
                etVehicleMake.setText("");

                List<String> list = new ArrayList<String>();
                spinnerTrafikModel.getEmptyView();

                CacheManager.SummonIssuanceInfo.VehicleMakePos = parent.getSelectedItemPosition();

                list = DbLocal.GetListForVehicleModelSpinner(CacheManager.mContext, parent.getSelectedItem().toString());
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, list);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dataAdapter.insert("--Sila Pilih--", 0);
                dataAdapter.insert("LAIN-LAIN", dataAdapter.getCount());
                spinnerTrafikModel.setAdapter(dataAdapter);
                spinnerTrafikModel.setSelection(CacheManager.SummonIssuanceInfo.VehicleModelPos);
                spinnerTrafikModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        layVehicleModel.setVisibility(View.GONE);
                        etVehicleModel.setText("");
                        if (pos == parent.getCount() - 1) {
                            layVehicleModel.setVisibility(View.VISIBLE);
                            etVehicleModel.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleModel);
                        }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                if (pos == parent.getCount() - 1) {
                    layVehicleMake.setVisibility(View.VISIBLE);
                    etVehicleMake.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleMake);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinnerTrafikJenisBadan = (Spinner) rootView.findViewById(R.id.spinnerTrafikJenisBadan);
        list = new ArrayList<String>();
        list = DbLocal.GetListForSpinner(CacheManager.mContext, "VEHICLE_TYPE");
        dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.insert("--Sila Pilih--", 0);
        spinnerTrafikJenisBadan.setAdapter(dataAdapter);

        Spinner spinnerTrafikWarna = (Spinner) rootView.findViewById(R.id.spinnerTrafikWarna);
        list = new ArrayList<String>();
        list = DbLocal.GetListForSpinner(CacheManager.mContext, "VEHICLE_COLOR");
        dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.insert("--Sila Pilih--", 0);
        spinnerTrafikWarna.setAdapter(dataAdapter);

        EditText txtCtrlId = (EditText) rootView.findViewById(R.id.etTrafikNoKenderaan);
        txtCtrlId.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
                String filtered_str = s.toString();
                if (filtered_str.matches(".*[^A-Z^0-9].*")) {
                    filtered_str = filtered_str.replaceAll("[^A-Z^0-9]", "");
                    s.clear();
                    s.insert(0, filtered_str);
                }
            }
        });

        Button btncapture = (Button) rootView.findViewById(R.id.btn_camera);
        btncapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();
                Intent i = new Intent(getActivity(), ImageActivity.class);
                startActivity(i);
            }
        });

        final EditText etVehicleNo = (EditText) rootView.findViewById(R.id.etTrafikNoKenderaan);
        Button btnCheck = (Button) rootView.findViewById(R.id.btn_check);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etVehicleNo.getText().toString().isEmpty()) {
                    retry = 0;
                    (getView().findViewById(R.id.tvTrafikMessage)).setVisibility(View.GONE);
                    CheckVehicleNo(etVehicleNo.getText().toString());

                    mProgressDialog = new ProgressDialog(v.getContext(), R.style.AppTheme_Dialog);
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setMessage("Checking...");
                    mProgressDialog.show();
                } else {
                    CustomAlertDialog.Show(getActivity(), "NO. KEND.", "Sila Isikan No. Kend.", 3);
                }
            }
        });

        return rootView;
    }

    private void CheckVehicleNo(final String vehicleNo) {
        String url = CacheManager.ServerKuantanURL + vehicleNo;
        TrustAllCertificates.trustAllHosts();

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() == 0) {
                                String status = vehicleNo +  "- Tiada Bayaran";
                                String statusCode = "null"; // Or extract meaningful data
                                onCheckingSuccess(status, statusCode);
                            } else {
                                JSONObject firstRecord = response.getJSONObject(0);
                                String endDate = firstRecord.getString("enddate");
                                String endTime = firstRecord.getString("endtime");
                                String status = vehicleNo + "- Berbayar (" + endDate + " " + endTime + ")";
                                String statusCode = "BAYAR"; // Or any meaningful status code
                                onCheckingSuccess(status, statusCode);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onCheckingFailed();
                        }
                        mProgressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(retry < 3) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            retry++;
                            CheckVehicleNo(vehicleNo);
                        }
                        else {
                            error.printStackTrace();
                            mProgressDialog.dismiss();
                            onCheckingFailed();
                        }
                    }
                });

        arrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this.getActivity()).addToRequestQueue(arrayRequest);
    }

    private void onCheckingSuccess(final String message, final String code) {
        try {
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                (getView().findViewById(R.id.tvTrafikMessage)).setVisibility(View.VISIBLE);
                ((TextView)getView().findViewById(R.id.tvTrafikMessage)).setTextColor(Color.parseColor("#EF4444"));

                if(code.equalsIgnoreCase("BAYAR")) {
                    ((TextView)getView().findViewById(R.id.tvTrafikMessage)).setTextColor(Color.parseColor("#44EF44"));
                }

                ((TextView)getView().findViewById(R.id.tvTrafikMessage)).setText(message);
                CacheManager.HasChecked = true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCheckingFailed() {
        Toast.makeText(this.getActivity(), "Semakan Gagal", Toast.LENGTH_LONG).show();
        //(getView().findViewById(R.id.tvTrafikMessage)).setVisibility(View.VISIBLE);
        //((TextView)getView().findViewById(R.id.tvTrafikMessage)).setText("Semakan Gagal");
    }

    private boolean fragmentResume=false;
    private boolean fragmentVisible=false;
    private boolean fragmentOnCreated=false;
    @Override
    public void setUserVisibleHint(boolean visible){
        super.setUserVisibleHint(visible);
        if (visible && isResumed()){   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
        }else  if (visible){        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        }
        else if(!visible && fragmentOnCreated){// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
            fragmentOnCreated = false;
            SaveData();
        }
    }

    public void SaveData() {
        CacheManager.SummonIssuanceInfo.VehicleNo = ((EditText)getView().findViewById(R.id.etTrafikNoKenderaan)).getText().toString();
        CacheManager.SummonIssuanceInfo.RoadTaxNo = ((EditText)getView().findViewById(R.id.etTrafikNoCukaiJalan)).getText().toString();
        CacheManager.SummonIssuanceInfo.SelectedVehicleMake = ((EditText)getView().findViewById(R.id.etTrafikVehicleMake)).getText().toString();
        CacheManager.SummonIssuanceInfo.SelectedVehicleModel = ((EditText)getView().findViewById(R.id.etTrafikVehicleModel)).getText().toString();

        Spinner sJenama = (Spinner)getView().findViewById(R.id.spinnerTrafikJenama);
        CacheManager.SummonIssuanceInfo.VehicleMakePos = sJenama.getSelectedItemPosition();
        if(sJenama.getSelectedItemPosition() > 0 && sJenama.getSelectedItemPosition() < sJenama.getCount() - 1)
        {
            CacheManager.SummonIssuanceInfo.VehicleMake = sJenama.getSelectedItem().toString();
        }
        else
        {
            CacheManager.SummonIssuanceInfo.VehicleMake = "";
        }

        Spinner sModel = (Spinner)getView().findViewById(R.id.spinnerTrafikModel);
        CacheManager.SummonIssuanceInfo.VehicleModelPos = sModel.getSelectedItemPosition();
        if(sModel.getSelectedItemPosition() > 0 && sModel.getSelectedItemPosition() < sModel.getCount() - 1)
        {
            CacheManager.SummonIssuanceInfo.VehicleModel = sModel.getSelectedItem().toString();
        }
        else
        {
            CacheManager.SummonIssuanceInfo.VehicleModel = "";
        }

        Spinner sJenisBadan = (Spinner)getView().findViewById(R.id.spinnerTrafikJenisBadan);
        CacheManager.SummonIssuanceInfo.VehicleTypePos = sJenisBadan.getSelectedItemPosition();
        if(sJenisBadan.getSelectedItemPosition() > 0)
        {
            CacheManager.SummonIssuanceInfo.VehicleType = sJenisBadan.getSelectedItem().toString();
        }
        else
        {
            CacheManager.SummonIssuanceInfo.VehicleType = "";
        }

        Spinner sWarna = (Spinner)getView().findViewById(R.id.spinnerTrafikWarna);
        CacheManager.SummonIssuanceInfo.VehicleColorPos = sWarna.getSelectedItemPosition();
        if(sWarna.getSelectedItemPosition() > 0)
        {
            CacheManager.SummonIssuanceInfo.VehicleColor = sWarna.getSelectedItem().toString();
        }
        else
        {
            CacheManager.SummonIssuanceInfo.VehicleColor = "";
        }

        CacheManager.SetCompoundAmount();
    }
}
