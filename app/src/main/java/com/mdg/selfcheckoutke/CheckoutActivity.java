package com.mdg.selfcheckoutke;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class CheckoutActivity extends AppCompatActivity {

    User user;
    private static final String ORDERS_URL = Config.URL+"orders/";
    Order order;
    TextView orderNo, prodsNo;
    LinearLayout ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        user = new User(this);
        order = new Order(this);
        orderNo = findViewById(R.id.order_number_checkout);
        prodsNo = findViewById(R.id.products_checkout);
        ll = findViewById(R.id.checkout_layout);
        RequestQueue rst = Volley.newRequestQueue(this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                ORDERS_URL + "order/"+user.getCurrId(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject res2 = response.getJSONObject("order");
                            Log.e("VOlley response: ", response.toString());
                            order.setTotalPrice(Integer.parseInt(res2.getString("total_price")));
                            order.setOId(res2.getString("_id"));
                            order.setProducts(res2.getJSONArray("products"));
                            orderNo.setText("Order Number: "+order.getOId());


                            //order.displayOrders(ll);
                        }catch(Exception e){
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

        for (int i = 0; i < order.getProducts().size(); i++){
            Product product = order.getProducts().get(i);
            String text = "Name: "+product.getProductName()+"\n"+
                    "Quantity: "+product.getQuantity()+"\n"+
                    "Price Per Unit: "+product.getPrice()+"\n";
            prodsNo.append(text);
        }

    }
}
