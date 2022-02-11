package com.android.productlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.productlist.Database.Product;
import com.android.productlist.Database.ProductDatabase;

public class ViewEntryActivity extends AppCompatActivity {

    TextView productEditTV, productIdTV, productNameTV, productDescTV, productPriceTV, productLocationTV;

    ProductDatabase productDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        String product_name = getIntent().getStringExtra("product_name");
        int uid = getIntent().getIntExtra("uid",0);
        getSupportActionBar().setTitle(product_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productEditTV = findViewById(R.id.tv_prodedit);
        productNameTV = findViewById(R.id.tv_productname);
        productDescTV = findViewById(R.id.tv_productdesc);
        productPriceTV = findViewById(R.id.tv_productprice);
        productLocationTV = findViewById(R.id.tv_provloc);

        productDatabase = Room.databaseBuilder(getApplicationContext(),ProductDatabase.class,"product-database").allowMainThreadQueries().build();

        Product allproducts = productDatabase.productDao().loadAllByProductids(uid);

        productNameTV.setText(String.valueOf(allproducts.getProductname()));
        productDescTV.setText(String.valueOf(allproducts.getProductdescription()));
        productPriceTV.setText(String.valueOf(allproducts.getProductprice()));

        productLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEntryActivity.this,MapsActivity.class);
                intent.putExtra("movable","false");
                intent.putExtra("product_name",product_name);

                System.out.println("latitude"+ allproducts.getLat());
                intent.putExtra("latitude",allproducts.getLat());
                intent.putExtra("longitude",allproducts.getLng());
                startActivity(intent);
            }
        });

        productEditTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEntryActivity.this,ProductEntryActivity.class);
                intent.putExtra("uid",uid);
                intent.putExtra("product_name",product_name);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}