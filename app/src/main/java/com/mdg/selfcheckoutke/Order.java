package com.mdg.selfcheckoutke;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Order {
    private String id;
    private ArrayList<Product> productsArr;
    private int totalPrice;
    private Context ctx;
    private static final String ORDERS_URL = Config.URL+"products/";

    public Order(Context context) {
        this.id = "";
        this.productsArr = new ArrayList<Product>();
        this.totalPrice = 0;
        ctx = context;
    }

    public void setOId(String id){ this.id = id;}
    public void setProducts(JSONArray products){
        try {
            for (int i = 0; i < products.length(); i++) {
                JSONObject jsonObject = products.getJSONObject(i);
                String id = jsonObject.getString("product");
                final Product prod = new Product();
                prod.setQuantity(Integer.parseInt(jsonObject.getString("quantity")));
                prod.setPid(id);
                RequestQueue rst = Volley.newRequestQueue(ctx);
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                        ORDERS_URL + id,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("Product: ", response.toString());
                                try {
                                    JSONObject jo = response.getJSONObject("product");
                                    prod.setProductName(jo.getString("name"));
                                    prod.setPrice(Integer.parseInt(jo.getString("price")));

                                    productsArr.add(prod);

                                    Log.e("prod", prod.getProductName());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Product: ", error.toString());
                            }
                        });
                rst.add(objectRequest);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setTotalPrice(int tp){ this.totalPrice = tp;}

    public String getOId(){ return this.id;}
    public ArrayList<Product> getProducts(){ return this.productsArr;}
    public int getTotalPrice(){ return this.totalPrice;}

    public void displayOrders(LinearLayout ll){
        for(int i = 0; i < this.getProducts().size(); i++){
            Product pro = this.getProducts().get(i);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView tv = new TextView(ctx);
            tv.setText(pro.getProductName());
            tv.setLayoutParams(layoutParams);
            TextView tv1 = new TextView(ctx);
            tv1.setText(String.valueOf(pro.getQuantity()));
            tv1.setLayoutParams(layoutParams);
            TextView tv2 = new TextView(ctx);
            tv2.setText(String.valueOf(pro.getPrice()));
            tv.setLayoutParams(layoutParams);
            ll.addView(tv);
            ll.addView(tv1);
            ll.addView(tv2);
        }
    }

}
