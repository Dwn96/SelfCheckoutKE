package com.mdg.selfcheckoutke;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mdg.selfcheckoutke.fragments.LoginFragment;
import com.mdg.selfcheckoutke.fragments.RegisterFragment;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RegisterFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener{
    TextView barRes, nav_username, nav_email;
    User user;
    Order order;
    private static final String SINGLE_PRODUCT_URL = Config.URL+"products/";
    private static final String ORDERS_URL = Config.URL+"orders/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = new User(this);
        if(user.isLoggedIn()){
            NavigationView nav = findViewById(R.id.nav_view);
            View headerView = nav.getHeaderView(0);
            nav_username = headerView.findViewById(R.id.nav_username);
            nav_email = headerView.findViewById(R.id.nav_email);
            nav_username.setText(user.getUsername());
            nav_email.setText(user.getemail());
        }
        barRes = findViewById(R.id.barcode_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        scan();
    }

    private void scan(){
        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan Product Bar Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            } else {
                if(Config.isNetworkAvailable(this)) {
                    String productId = result.getContents();
                    barRes.setText(productId);
                    final Context ctx = this;
                    try {
                        RequestQueue req = Volley.newRequestQueue(this);
                        JsonObjectRequest objectRequest = new JsonObjectRequest(
                                Request.Method.GET,
                                SINGLE_PRODUCT_URL+result.getContents(),
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(final JSONObject response) {
                                        barRes.setText(response.toString());
                                        try {
                                            final JSONObject prod = response.getJSONObject("product");
                                            //Alert dialog querrying for the quantity
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                            alert.setCancelable(false);
                                            alert.setTitle("Info");
                                            final EditText qty = new EditText(ctx);
                                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.MATCH_PARENT
                                            );
                                            qty.setLayoutParams(lp);
                                            alert.setMessage("Item: "+ prod.getString("name") +
                                                    "\nPrice: "+ prod.getString("price")+
                                                    "\nEnter quantity. Pressing Cancel will not add this product to your order");
                                            alert.setView(qty);
                                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //Toast.makeText(getApplicationContext(), qty.getText(), Toast.LENGTH_SHORT).show();
                                                    final int reqHTTP;
                                                    final String finURL;
                                                    if(user.isNewOrder()) {
                                                        reqHTTP = Request.Method.POST;
                                                        finURL = ORDERS_URL;
                                                        user.createOrder();

                                                    } else {
                                                        reqHTTP = Request.Method.PATCH;
                                                        finURL = ORDERS_URL+user.getCurrId();
                                                    }
                                                    try{
                                                        int tot = user.getCURRTOT() + (Integer.parseInt(prod.getString("price"))*Integer.parseInt(qty.getText().toString().trim()));
                                                        user.addTotal(tot);
                                                        Log.e("Total", String.valueOf(user.getCURRTOT()));
                                                        JSONObject job = new JSONObject();
                                                        job.put("productId", prod.getString("_id"));
                                                        job.put("quantity", qty.getText().toString().trim());
                                                        job.put("user_id", user.getID());
                                                        job.put("total_price", user.getCURRTOT());
                                                        Log.e("String to send: ", job.toString());
                                                        RequestQueue rst = Volley.newRequestQueue(ctx);
                                                        JsonObjectRequest objectRequest = new JsonObjectRequest(
                                                                reqHTTP,
                                                                finURL,
                                                                job,
                                                                new Response.Listener<JSONObject>() {
                                                                    @Override
                                                                    public void onResponse(JSONObject response) {
                                                                        try {
                                                                            barRes.setText(response.getString("message"));
                                                                            JSONObject order = response.getJSONObject("createdOrder");
                                                                            user.addOrderId(order.getString("_id"));
                                                                            Log.e("Id: ", user.getCurrId());

                                                                        }catch (Exception e){
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                },
                                                                new Response.ErrorListener() {
                                                                    @Override
                                                                    public void onErrorResponse(VolleyError error) {
                                                                        barRes.setText(error.toString());
                                                                    }
                                                                });
                                                        rst.add(objectRequest);
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    alert.setCancelable(true);
                                                }
                                            });
                                            alert.show();
                                        }catch (Exception ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError err) {
                                        Log.e("Rest Response", err.toString());
                                        barRes.setText("Failed.\n"+err.toString());
                                    }
                                }
                        );
                        req.add(objectRequest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(this, "No internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
           scan();
        } else if (id == R.id.nav_register) {
            fragment = new RegisterFragment();
        } else if (id == R.id.nav_login) {
            fragment = new LoginFragment();
        } else if (id == R.id.nav_tools) {
            Intent check = new Intent(this, CheckoutActivity.class);
            startActivity(check);
        }else if (id == R.id.nav_products){
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            final Context ctx = this;
            build.setCancelable(true);
            build.setTitle("Warning. Are you sure?");
            build.setMessage("Delete current Order? Touch anywhere to dismiss");
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        RequestQueue rst = Volley.newRequestQueue(ctx);
                        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.DELETE,
                                ORDERS_URL + user.getCurrId(),
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        user.completeOrder();
                                        try {
                                            barRes.setText(response.getString("message"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("Volley error: ", error.toString());
                                    }
                                });
                        rst.add(objectRequest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            build.show();
        }

        if(fragment !=null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            ft.commit();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
