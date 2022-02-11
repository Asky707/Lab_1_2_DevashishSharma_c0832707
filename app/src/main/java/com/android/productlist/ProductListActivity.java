package com.android.productlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.productlist.Adapters.productAdapter;
import com.android.productlist.Database.Product;
import com.android.productlist.Database.ProductDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    LinearLayout product_Ll;
    FloatingActionButton addProductButton;
    SearchView productSearchView;
    ConstraintLayout parentContraint;

    RecyclerView productRV;
    productAdapter productAda;
    ProductDatabase productDatabase;

    List<String> product_name = new ArrayList<String>();
    List<Double> product_price = new ArrayList<Double>();
    List<Integer> uid = new ArrayList<Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        //Initializing all Views and ViewGroups
        product_Ll = findViewById(R.id.ll_product);
        addProductButton = findViewById(R.id.fab_addproduct);
        productSearchView = findViewById(R.id.sv_product);
        parentContraint = findViewById(R.id.cl_parent);

        productRV = findViewById(R.id.rv_product);

        product_Ll.setVisibility(View.GONE); //hiding product layout

        productDatabase = Room.databaseBuilder(getApplicationContext(), ProductDatabase.class,"product-database").allowMainThreadQueries().build();

        populatedata();

        System.out.println(productDatabase.productDao().getAll());

        product_Ll.setVisibility(View.VISIBLE);
        getSupportActionBar().setTitle("Products"); // setting activity's appbar title

        loadProduct();


        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductListActivity.this,ProductEntryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ProductListActivity.this);
        alert.setTitle("Exit ?");
        alert.setMessage("Do you want to exit this Application ?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.create().show();
    }

    public void loadProduct()
    {
        // initializing room database
        productDatabase = Room.databaseBuilder(getApplicationContext(),ProductDatabase.class,"product-database").allowMainThreadQueries().build();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        // getting all product names from room database
        product_name = productDatabase.productDao().getallproducts();

        // getting all products descriptions from room database
        product_price = productDatabase.productDao().getallprodprice();

        // getting all product ids from room database
        uid = productDatabase.productDao().getallids();

        // initializing Product Adapter
        productAda = new productAdapter(product_name,product_price,uid);

        // setting layout manager to Product recyclerview
        productRV.setLayoutManager(layoutManager);

        // setting adapter to Product recyclerview
        productRV.setAdapter(productAda);

        // setting search functionality to Product recyclerview
        productSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                productAda.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAda.getFilter().filter(newText);
                return false;
            }
        });



        //deleting product on swipe
        ItemTouchHelper.SimpleCallback itscproduct = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                TextView tv_title = viewHolder.itemView.findViewById(R.id.tv_title);
                TextView tv_subtitle = viewHolder.itemView.findViewById(R.id.tv_subtitle);
                TextView tv_prodid = viewHolder.itemView.findViewById(R.id.tv_prodid);
                int produid = Integer.parseInt(tv_prodid.getText().toString());
                String prodname = tv_title.getText().toString();
                Double prodprice = Double.valueOf(tv_subtitle.getText().toString().replace("Price : $",""));

                Product product = productDatabase.productDao().loadAllByProductids(produid);

                productDatabase.productDao().deleteproductbyuid(produid);

                product_name.remove(prodname);
                product_price.remove(prodprice);
                uid.remove(Integer.valueOf(produid));

                final int index = viewHolder.getAdapterPosition();

                productAda.notifyItemRemoved(index);

                Snackbar.make(parentContraint,"Product Deleted: "+prodname,5000).setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productDatabase.productDao().insertProduct(product);
                        product_name.add(index,prodname);
                        product_price.add(index,prodprice);
                        uid.add(index,produid);

                        productAda.notifyItemInserted(index);

                    }
                }).show();

            }
        };

        ItemTouchHelper ithprod = new ItemTouchHelper(itscproduct);
        ithprod.attachToRecyclerView(productRV); // attaching itemtouchhelper to product recyclerview
    }

    public void populatedata() {

        SharedPreferences pref = getSharedPreferences("pref_first", MODE_PRIVATE);
        int first = pref.getInt("first", 1);

        if (first == 1) {
            List<Product> products = new ArrayList<>();

            Product product = new Product(1, "Brazilian Dry Oil",
                    "Brazilian Dry Oil is a fast-absorbing, lightweight oil that can be used daily to smooth",
                    26.0, 53.534444, -113.490278);
            products.add(product);

            product = new Product(2	,"Ionic Color Lock"	," Lock in week one color vibrancy for all shades, colors, and blonde tones.",
                    38.0	,45.424722,	-75.695);
            products.add(product);

            product = new Product(3	,"Professional Styling"	,"Smooth or style with this versatile, lightweight, ergonomic styling iron. Far infrared heat penetra",
                    175.0,49.260833,	-123.113889);
            products.add(product);

            product = new Product(4	,"Hydrate Conditioning Masque"	,"AVOCADO OILAmong the healthiest natural ingredients on the planet, avocado oil is beneficial for hair",
                    20.0,43.741667	,-79.373333);
            products.add(product);

            product = new Product(5	,"Smooth Conditioner"	,"AVOCADO OILAmong the healthiest natural ingredients on the planet, avocado oil is beneficial for hair"	,
                    20.0,49.884444,	-97.146389);
            products.add(product);

            product = new Product(6,	"Clean Shampoo"	,"AVOCADO OILAmong the healthiest natural ingredients on the planet, avocado oil is beneficial for hair"	,
                    50.0,53.534444	,-113.490278);
            products.add(product);

            product = new Product(	7,	"Instant Damage Correction Shampoo","The exclusive LEAF & FLOWER CBD Corrective Complex elicits an entourage effect by combining key cann.",
                    37.0,45.424722,	-75.695);
            products.add(product);

            product = new Product(8,	"SPECIAL SHAMPOO"	,"Special ingredients and tea tree oil rid hair of impurities and leave hair full of vitality and lust."	,
                    15.0,43.741667,	-79.373333);
            products.add(product);

            product = new Product(9,	"MOISTURE SHAMPOO",	" Nourish tresses with lightweight moisture while gently cleansing. This breakthrough formula envelo.."
                    ,50.0,49.260833,	-123.113889);
            products.add(product);

            product = new Product(10,	"Bombshell Shine Mist"	,"Steal the spotlight in an instant with this weightless, superfine mist. Provides frizz and flyaway .",
                    10.0	,49.884444,-97.146389);
            products.add(product);

            productDatabase.productDao().insertProducts(products);

            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("first",0);
            editor.apply();
        }
    }
}