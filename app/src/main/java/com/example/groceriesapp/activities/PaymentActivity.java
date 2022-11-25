package com.example.groceriesapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.example.groceriesapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class PaymentActivity extends AppCompatActivity {

    private Button button;
    private EditText editTextNameCard,editTextNumber,editTextDate,edittextCard;
    private RadioButton radioButton2,radioButton;
    private ImageButton backBtn;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        backBtn=findViewById(R.id.backBtn);
        editTextNameCard=findViewById(R.id.editTextNameCard);
        editTextNumber=findViewById(R.id.editTextNumber);
        editTextDate=findViewById(R.id.editTextDate);
        edittextCard=findViewById(R.id.edittextCard);
        radioButton2=findViewById(R.id.radioButton2);
        radioButton=findViewById(R.id.radioButton);

        firebaseAuth = FirebaseAuth.getInstance();

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        payDb();


    }
    public String cardNumber,cardName,cardCvc,cardDate,payment="";
    private void payDb(){
        cardName = editTextNameCard.getText().toString().trim();
        cardNumber = edittextCard.getText().toString().trim();
        cardCvc = editTextNumber.getText().toString().trim();
        cardDate = editTextDate.getText().toString().trim();
        if (radioButton.isChecked()){
            payment="Card";
        }
        if (radioButton2.isChecked()){
            payment="Cash";
        }
    }

}