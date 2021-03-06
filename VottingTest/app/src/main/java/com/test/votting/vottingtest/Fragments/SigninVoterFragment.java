package com.test.votting.vottingtest.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.test.votting.vottingtest.HelperCLass;
import com.test.votting.vottingtest.InternetConnection;
import com.test.votting.vottingtest.Main2Activity;
import com.test.votting.vottingtest.R;
import com.test.votting.vottingtest.RegistrationActivity;

import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

/**
 * A simple {@link Fragment} subclass.
 */
public class SigninVoterFragment extends Fragment {

    String [] times;

    EditText nationalID,password;
    HelperCLass helperCLass;
    SharedPreferences.Editor editor;
    TextView signin;
    String signInStatus="0x0000000000000000000000000000000000000002";
    ProgressDialog progressDialog;
    public SigninVoterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_signin_voter, container, false);

        RegistrationActivity.registrationTitle.setText(getActivity().getResources().getString(R.string.SIGNIN));

        signin=(TextView)v.findViewById(R.id.signin);

        helperCLass=new HelperCLass(getActivity());
        if(helperCLass.getSharedPreferences().getString("Lang","").equals("ar")) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            params.leftMargin=60;
            signin.setLayoutParams(params);

        }

        nationalID=(EditText)v.findViewById(R.id.nationalID);
        password=(EditText)v.findViewById(R.id.password);
     //   helperCLass.getEditor().clear().commit();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( InternetConnection.ifConnect(getActivity()))
                    loginFunc();
                else
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.Nointernetconnection), Toast.LENGTH_SHORT).show();
            }
        });
        HelperCLass.myYear="";
        HelperCLass.arrayList.clear();

        return v;
    }



    public void loginFunc() {
        if(nationalID.getText().toString().isEmpty())
            Toast.makeText(getActivity(),  getActivity().getResources().getString(R.string.NationalIDisrequired), Toast.LENGTH_SHORT).show();
        else if(password.getText().toString().isEmpty())
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.Passwordisrequired), Toast.LENGTH_SHORT).show();
        else {
            progressDialog=helperCLass.getProgress(getActivity().getResources().getString(R.string.Signin),getActivity().getResources().getString(R.string.Pleasewait));
            progressDialog.show();
            LongOperationCheck longOperation=new LongOperationCheck();
            longOperation.execute("");


        }

    }

    class LongOperationCheck extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if(signInStatus.equals("0x0000000000000000000000000000000000000002"))
            {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.LoginFailed), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
            else
            {
              new LongOperation().execute("");
            }

        }

        @Override
        protected String doInBackground(String... params) {


            try {



                HelperCLass.voters = HelperCLass.voters.load(HelperCLass.voterAddress,HelperCLass.web3,HelperCLass.credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                HelperCLass.candidates = HelperCLass.candidates.load(HelperCLass.candidateAddress,HelperCLass.web3,HelperCLass.credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                HelperCLass.judgment = HelperCLass.judgment.load(HelperCLass.judgmentAddress,HelperCLass.web3,HelperCLass.credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                HelperCLass.mainContract = HelperCLass.mainContract.load(HelperCLass.mainAddress,HelperCLass.web3,HelperCLass.credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

                signInStatus=HelperCLass.mainContract.checkIdAndPasswordVoter(nationalID.getText().toString(),password.getText().toString()).send();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }
    class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

                editor=helperCLass.getEditor();
                editor.putString("nationalID",nationalID.getText().toString());
                editor.putString("MyAddress",signInStatus);
                editor.putString("city",HelperCLass.myCity);
                editor.commit();

            new LongOperationgetPrivateKey().execute("");
        }

        @Override
        protected String doInBackground(String... params) {


            try {
                HelperCLass.myYear=HelperCLass.mainContract.getVoterYear(signInStatus).send();
                HelperCLass.myCity= HelperCLass.mainContract.getVoterCity(signInStatus).send();
                HelperCLass.myBD=HelperCLass.mainContract.getVoterDateOfBirth(signInStatus).send();
                HelperCLass.myName=HelperCLass.mainContract.getVoterName(signInStatus).send();
                HelperCLass.threshouldFlag=HelperCLass.mainContract.getThresholdFlag().send();
                HelperCLass.fromDate=HelperCLass.mainContract.getStartDate().send();
                times=HelperCLass.mainContract.getPeriod().send().split("-");
                HelperCLass.fromTime=times[0];
                HelperCLass.toTime=times[1];
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }



    class LongOperationgetPrivateKey extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            HelperCLass.voters = HelperCLass.voters.load(HelperCLass.voterAddress,HelperCLass.web3,HelperCLass.ChangeCredentials(s), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            HelperCLass.candidates = HelperCLass.candidates.load(HelperCLass.candidateAddress,HelperCLass.web3,HelperCLass.ChangeCredentials(s), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            HelperCLass.judgment = HelperCLass.judgment.load(HelperCLass.judgmentAddress,HelperCLass.web3,HelperCLass.ChangeCredentials(s), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            HelperCLass.mainContract = HelperCLass.mainContract.load(HelperCLass.mainAddress,HelperCLass.web3,HelperCLass.ChangeCredentials(s), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);


            startActivity(new Intent(getActivity(), Main2Activity.class));
            getActivity().finish();
            progressDialog.dismiss();

        }

        @Override
        protected String doInBackground(String... params) {

            try {
               HelperCLass.privateKey=HelperCLass.mainContract.getPrivateKey(signInStatus).send();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return HelperCLass.privateKey;
        }

    }

}


