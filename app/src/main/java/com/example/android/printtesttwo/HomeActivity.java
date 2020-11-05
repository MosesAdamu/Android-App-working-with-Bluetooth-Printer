package com.example.android.printtesttwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class HomeActivity extends Activity {

    ImageView logoImage;
    TextView hometitle;
    Button btnDuplicate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        hometitle =  (TextView) findViewById(R.id.text_home_title);
        logoImage = (ImageView)findViewById(R.id.phedlogoo);
        btnDuplicate = (Button) findViewById(R.id.btn_duplicate_bill);


        btnDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DuplicateBill.class);
                startActivity(intent);
            }
        });



    }

}
