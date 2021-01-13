package com.example.project2.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.Model.InventoryDBHelper;
import com.example.project2.Model.ItemModel;
import com.example.project2.R;

public class AddEditActivity extends AppCompatActivity {
    TextView textView;
    EditText itemName;
    EditText itemCount;
    EditText itemDesc;
    Button saveButton;
    Button cancelButton;
    boolean isThereForAdd;
    InventoryDBHelper inventoryDBHelper;
    private ItemModel itemModelPassed;
    SharedPreferences sp;
    String itemNameText;
    String itemDescText;
    String itemCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        textView = findViewById(R.id.titleID);
        itemName = findViewById(R.id.itemNameID);
        itemCount = findViewById(R.id.itemCountID);
        itemDesc = findViewById(R.id.itemDescId);
        saveButton = findViewById(R.id.saveButtonId);
        cancelButton = findViewById(R.id.cancelButtonId);
        inventoryDBHelper = new InventoryDBHelper(this);
        Intent intent = getIntent();
        String intentValue = intent.getStringExtra("Add");

        sp = getSharedPreferences("USER_PHONE_NUMBER", Context.MODE_PRIVATE);
        // If the user has entered this page to add an item in the inventory
        if(intentValue != null && intentValue.equals("Add")){
            textView.setText("Add a New Inventory Item");
            isThereForAdd = true;
        }else{
            // If the user has entered this page to edit the item in the inventory
            isThereForAdd = false;
            itemModelPassed = (ItemModel) intent.getSerializableExtra("selected");
            itemName.setText(itemModelPassed.getItemName());
            itemDesc.setText(itemModelPassed.getItemDesc());
            itemCount.setText(String.valueOf(itemModelPassed.getItemCount()));
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isThereForAdd){
                    try{
                        itemNameText = itemName.getText().toString();
                        itemDescText = itemDesc.getText().toString();
                        itemCountText = itemCount.getText().toString();

                        if(itemNameText != "" && itemDescText != "" && itemCountText != ""){
                            try{

                                inventoryDBHelper.addItem(new ItemModel(itemNameText, itemDescText, Integer.valueOf(itemCountText)));
                                inventoryDBHelper.close();

                                if(Integer.valueOf(itemCountText) < 10) {

                                    sendSMS();
                                }
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                            Toast.makeText(getApplicationContext(), "The item has been added into the inventory.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Please fill in the blanks. All the fields are mandatory", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    try{
                        itemNameText = itemName.getText().toString();
                        itemDescText = itemDesc.getText().toString();
                        itemCountText = itemCount.getText().toString();

                        if(itemNameText != "" && itemDescText != "" && itemCountText != ""){
                            inventoryDBHelper.updateItem(itemModelPassed.getId(),new ItemModel(itemNameText, itemDescText, Integer.valueOf(itemCountText)));
                            inventoryDBHelper.close();
                            if(Integer.valueOf(itemCountText) < 10) {
                                sendSMS();
                            }
                            Toast.makeText(getApplicationContext(), "The item has been updated into the inventory.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Please fill in the blanks. All the fields are mandatory", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddEditActivity.this, HomePage.class);
                startActivity(intent);
            }
        });
    }

    public void sendSMS(){
        //Get the SmsManager instance and call the sendTextMessage method to send message
        String message = "The inventory is low for " + itemNameText+ ". You have only " +
                itemCountText + " available.";
        Intent intent = new Intent(getApplicationContext(), HomePage.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        SmsManager sms = SmsManager.getDefault();
        if (sp.contains("PHONE_NUMBER") && sp.contains("RESPONSE")) {
            if (sp.getBoolean("RESPONSE", true)) {
                sms.sendTextMessage(sp.getString("PHONE_NUMBER", ""), null, message, pi, null);
            }

        }
    }
}
