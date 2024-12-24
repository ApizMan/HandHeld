package my.vista.com.handheld.UI.Activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Data.DbLocal;
import my.vista.com.handheld.Entity.BaseEntity;
import my.vista.com.handheld.Data.SettingsHelper;
import my.vista.com.handheld.R;

/**
 * Created by hp on 24/7/2016.
 */
public class KesalahanFragment extends Fragment {
    ArrayList<BaseEntity> listSection = new ArrayList<BaseEntity>();

    public KesalahanFragment() {
    }

    public static KesalahanFragment newInstance() {
        KesalahanFragment fragment = new KesalahanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_kesalahan, container, false);

        final Spinner spinnerOffenceLocation = (Spinner)rootView.findViewById(R.id.spinnerTempatJalan);
        final TextInputLayout laySummonLocation = (TextInputLayout)rootView.findViewById(R.id.laySummonLocation);
        final EditText etSummonLocation = (EditText)rootView.findViewById(R.id.etSummonLocation);
        final Spinner spinnerOffenceSection = (Spinner)rootView.findViewById(R.id.spinnerSeksyenKaedah);
        final EditText etKesalahan = (EditText)rootView.findViewById(R.id.etKesalahan);
        final Spinner spinnerOffenceAct = (Spinner)rootView.findViewById(R.id.spinnerundangundang);

        List<String> list = new ArrayList<String>();
        list = DbLocal.GetOneFieldListForSpinner(CacheManager.mContext, "SHORT_DESCRIPTION","OFFENCE_ACT");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.insert("--Sila Pilih--", 0);
        spinnerOffenceAct.setAdapter(dataAdapter);
        spinnerOffenceAct.setSelection(CacheManager.SummonIssuanceInfo.OffenceActPos);
        spinnerOffenceAct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                listSection = new ArrayList<BaseEntity>();
                listSection = DbLocal.GetListForOffenceSectionSpinnerToObject(CacheManager.mContext, parent.getSelectedItem().toString());
                List<String> list = new ArrayList<String>();

                spinnerOffenceSection.getEmptyView();

                list = DbLocal.GetListForOffenceSectionSpinner(CacheManager.mContext, parent.getSelectedItem().toString());
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dataAdapter.insert("--Sila Pilih--", 0);
                spinnerOffenceSection.setAdapter(dataAdapter);

                if(list.size() > CacheManager.SummonIssuanceInfo.OffenceSectionPos)
                    spinnerOffenceSection.setSelection(CacheManager.SummonIssuanceInfo.OffenceSectionPos);
                else
                    spinnerOffenceSection.setSelection(0);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerOffenceSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(pos>0)
                {
                    try
                    {
                        BaseEntity entity = listSection.get(pos - 1);
                        List<String> list = DbLocal.GetListForOffenceSectionCodeSpinnerNew(CacheManager.mContext, entity.Code, spinnerOffenceAct.getSelectedItem().toString());
                        etKesalahan.setText(list.get(2));
                    }
                    catch (Exception e) {
                        etKesalahan.setText("");
                    }
                }
                else
                {
                    etKesalahan.setText("");
                }

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinnerOffenceLocationArea = (Spinner)rootView.findViewById(R.id.spinnerZon);

        list = new ArrayList<String>();
        list = DbLocal.GetOneFieldListForSpinner(CacheManager.mContext, "DESCRIPTION", "OFFENCE_AREA", "DESCRIPTION");
        dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.insert("--Sila Pilih--", 0);
        spinnerOffenceLocationArea.setAdapter(dataAdapter);
        spinnerOffenceLocationArea.setSelection(CacheManager.SummonIssuanceInfo.OffenceLocationAreaPos);
        spinnerOffenceLocationArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                List<String> list = new ArrayList<String>();

                spinnerOffenceLocation.getEmptyView();

                list = DbLocal.GetListForOffenceLocationSpinner(CacheManager.mContext, parent.getSelectedItem().toString());
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dataAdapter.insert("--Sila Pilih--", 0);
                dataAdapter.insert("LAIN-LAIN", dataAdapter.getCount());
                spinnerOffenceLocation.setAdapter(dataAdapter);

                if(list.size() > CacheManager.SummonIssuanceInfo.OffenceLocationPos)
                    spinnerOffenceLocation.setSelection(CacheManager.SummonIssuanceInfo.OffenceLocationPos);
                else
                    spinnerOffenceLocation.setSelection(0);

                spinnerOffenceLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        laySummonLocation.setVisibility(View.GONE);
                        etSummonLocation.setText("");
                        if (pos == parent.getCount() - 1) {
                            laySummonLocation.setVisibility(View.VISIBLE);
                            etSummonLocation.setText(CacheManager.SummonIssuanceInfo.SummonLocation);
                        }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return rootView;
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
        Spinner sOffenceAct = (Spinner)getView().findViewById(R.id.spinnerundangundang);
        CacheManager.SummonIssuanceInfo.OffenceActPos = sOffenceAct.getSelectedItemPosition();
        if(sOffenceAct.getSelectedItemPosition() > 0)
        {
            CacheManager.SummonIssuanceInfo.OffenceAct = sOffenceAct.getSelectedItem().toString();
        }
        else
        {
            CacheManager.SummonIssuanceInfo.OffenceAct = "";
        }

        Spinner sOffenceSection = (Spinner)getView().findViewById(R.id.spinnerSeksyenKaedah);
        CacheManager.SummonIssuanceInfo.OffenceSectionPos = sOffenceSection.getSelectedItemPosition();
        if(sOffenceSection.getSelectedItemPosition() > 0)
        {
            try
            {
                BaseEntity entity = listSection.get(sOffenceSection.getSelectedItemPosition() - 1);
                List<String> list = DbLocal.GetListForOffenceSectionCodeSpinnerNew(CacheManager.mContext, entity.Code, sOffenceAct.getSelectedItem().toString());

                CacheManager.SummonIssuanceInfo.OffenceActCode  = list.get(0);
                CacheManager.SummonIssuanceInfo.OffenceSectionCode  = list.get(1);
                CacheManager.SummonIssuanceInfo.OffenceSection = list.get(2);
                CacheManager.SummonIssuanceInfo.ResultCode = list.get(3);
            }
            catch (Exception e) {
                CacheManager.SummonIssuanceInfo.OffenceSection = "";
                CacheManager.SummonIssuanceInfo.OffenceActCode = "";
                CacheManager.SummonIssuanceInfo.OffenceSectionCode = "";
            }
        }
        else
        {
            CacheManager.SummonIssuanceInfo.OffenceSection = "";
            CacheManager.SummonIssuanceInfo.OffenceActCode = "";
            CacheManager.SummonIssuanceInfo.OffenceSectionCode = "";
        }

        EditText etSummonLocation = (EditText)getView().findViewById(R.id.etSummonLocation);
        CacheManager.SummonIssuanceInfo.SummonLocation = etSummonLocation.getText().toString();

        EditText tKesalahan = (EditText)getView().findViewById(R.id.etKesalahan);
        CacheManager.SummonIssuanceInfo.Offence = tKesalahan.getText().toString();

        Spinner sZon = (Spinner)getView().findViewById(R.id.spinnerZon);
        CacheManager.SummonIssuanceInfo.OffenceLocationAreaPos = sZon.getSelectedItemPosition();
        if(sZon.getSelectedItemPosition() > 0)
        {
            CacheManager.SummonIssuanceInfo.OffenceLocationArea = sZon.getSelectedItem().toString();
        }
        else
        {
            CacheManager.SummonIssuanceInfo.OffenceLocationArea = "";
        }

        Spinner sTempatJalan = (Spinner)getView().findViewById(R.id.spinnerTempatJalan);
        CacheManager.SummonIssuanceInfo.OffenceLocationPos = sTempatJalan.getSelectedItemPosition();
        if(sTempatJalan.getSelectedItemPosition() > 0 && sTempatJalan.getSelectedItemPosition() < sTempatJalan.getCount() - 1)
        {
            CacheManager.SummonIssuanceInfo.OffenceLocation = sTempatJalan.getSelectedItem().toString();
        }
        else
        {
            CacheManager.SummonIssuanceInfo.OffenceLocation = "";
        }

        EditText tButirLokasi = (EditText)getView().findViewById(R.id.etButiranLokasi);
        CacheManager.SummonIssuanceInfo.OffenceLocationDetails = tButirLokasi.getText().toString();

        EditText tPetak = (EditText)getView().findViewById(R.id.etPetak);
        CacheManager.SummonIssuanceInfo.PetakVehicle = tPetak.getText().toString();

        String delegate = "yy";
        String year = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
        SettingsHelper.CheckYear(CacheManager.mContext);

        CacheManager.SummonIssuanceInfo.NoticeSerialNo = SettingsHelper.HandheldCode +  year + SettingsHelper.getNoticeSerialNumber(CacheManager.mContext);

        while(DbLocal.IsNoticeExists(CacheManager.mContext, CacheManager.SummonIssuanceInfo.NoticeSerialNo)) {
            SettingsHelper.IncrementSerialNumber(CacheManager.mContext);
            CacheManager.SummonIssuanceInfo.NoticeSerialNo = SettingsHelper.HandheldCode +  year + SettingsHelper.getNoticeSerialNumber(CacheManager.mContext);
        }

        CacheManager.SetCompoundAmount();

        RadioButton radioYes = (RadioButton)getView().findViewById(R.id.radioYes);
        CacheManager.SummonIssuanceInfo.IsClamping = String.valueOf(radioYes.isChecked());

        EditText etNotes = (EditText)getView().findViewById(R.id.etNotes);
        CacheManager.SummonIssuanceInfo.Notes = etNotes.getText().toString();
    }
}
