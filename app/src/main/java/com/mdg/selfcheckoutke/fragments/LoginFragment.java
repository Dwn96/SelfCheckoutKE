package com.mdg.selfcheckoutke.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mdg.selfcheckoutke.Config;
import com.mdg.selfcheckoutke.MainActivity;
import com.mdg.selfcheckoutke.R;
import com.mdg.selfcheckoutke.User;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    private static final String URL = Config.URL + "users/signin";
    Button buttonlogin;
    EditText email, password;
    TextView error;
    String Email, Password;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        email = view.findViewById(R.id.login_input_email);
        password = view.findViewById(R.id.login_input_password);
        buttonlogin = view.findViewById(R.id.btn_login);
        buttonlogin.setOnClickListener(this);
        error = view.findViewById(R.id.textview_error);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if(view == buttonlogin){
            try {
                Email = email.getText().toString().trim();
                Password = password.getText().toString().trim();
                JSONObject dataJSON = new JSONObject();
                dataJSON.put("email", Email);
                dataJSON.put("password", Password);

                try {

                    Log.e("JSON data to be sent: ", dataJSON.toString());
                    //send data
                    final User user = new User(getContext());
                    final Context ctx = getContext();
                    RequestQueue req = Volley.newRequestQueue(getContext());
                    JsonObjectRequest objectRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            URL,
                            dataJSON,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.e("Rest Response", response.toString());
                                        JSONArray userDataArray = response.getJSONArray("user");
                                        JSONObject userData = userDataArray.getJSONObject(0);
                                        user.loginUser(userData.getString("_id"), userData.getString("username"),
                                                userData.getString("email"), response.getString("token"));
                                        Log.e("token", user.getToken());
                                        Toast.makeText(ctx, "Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent success = new Intent(ctx, MainActivity.class);
                                        startActivity(success);
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError err) {
                                    Log.e("Rest Response", err.toString());
                                    error.setText("Login failed.\n"+err.toString());
                                }
                            }
                    );
                    req.add(objectRequest);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
