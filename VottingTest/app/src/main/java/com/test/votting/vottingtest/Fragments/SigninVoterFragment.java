package com.test.votting.vottingtest.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.test.votting.vottingtest.HelperCLass;
import com.test.votting.vottingtest.Main2Activity;
import com.test.votting.vottingtest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SigninVoterFragment extends Fragment {

    EditText nationalID,password;
    HelperCLass helperCLass;
    SharedPreferences.Editor editor;
    TextView signin;
    boolean singInStatus;
    ProgressDialog progressDialog;
    public SigninVoterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_signin_voter, container, false);
        signin=(TextView)v.findViewById(R.id.signin);

        helperCLass=new HelperCLass(getActivity());
        nationalID=(EditText)v.findViewById(R.id.nationalID);
        password=(EditText)v.findViewById(R.id.password);
        helperCLass.getEditor().putString("MyPrivate","");
        helperCLass.getEditor().putString("MyAddress","");
        helperCLass.getEditor().commit();



//        helperCLass.getEditor().clear();
//        helperCLass.getEditor().commit();


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFunc();
            }
        });

        return v;
    }



    public void loginFunc() {
        if(nationalID.getText().toString().isEmpty())
            Toast.makeText(getActivity(), "NationalID is required", Toast.LENGTH_SHORT).show();
        else if(password.getText().toString().isEmpty())
            Toast.makeText(getActivity(), "Password is required", Toast.LENGTH_SHORT).show();
        else {
            progressDialog=helperCLass.getProgress("Signin","Please wait");
            progressDialog.show();
            LongOperation longOperation=new LongOperation();
            longOperation.execute("");


        }

    }

    class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(singInStatus==false)
            {
                Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();

            }
            progressDialog.dismiss();

        }

        @Override
        protected String doInBackground(String... params) {

            try {


                if( HelperCLass.voters.checkIdAndPassword(nationalID.getText().toString(),password.getText().toString()).send().equals("0"))
                {
                    singInStatus=false;
                }
                else
                {
                    singInStatus=true;

                    editor=helperCLass.getEditor();
                    editor.putString("nationalID",nationalID.getText().toString());
                 //   String result[]=HelperCLass.voters.checkIdAndPassword(nationalID.getText().toString(),password.getText().toString()).send().split("//");
//                    editor.putString("MyPrivate",result[0]);
//                    editor.putString("MyAddress",result[1]);
                    editor.putString("MyAddress",HelperCLass.voters.checkIdAndPassword(nationalID.getText().toString(),password.getText().toString()).send());
                    editor.commit();
                    startActivity(new Intent(getActivity(), Main2Activity.class));
                    getActivity().finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }



            return null;
        }

    }












}

