package com.example.project2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.project2.Adapter.InventoryAdapter;
import com.example.project2.Model.InventoryDBHelper;
import com.example.project2.Model.ItemModel;
import com.example.project2.R;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {
    private RecyclerView listView;
    InventoryDBHelper inventoryDBHelper;
    InventoryAdapter inventoryAdapter;
    SharedPreferences sp;
    private static final int SMS_PERMISSION_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        listView = findViewById(R.id.itemListView);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        inventoryDBHelper = new InventoryDBHelper(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(), LinearLayout.VERTICAL);
        listView.addItemDecoration(dividerItemDecoration);
        sp = getSharedPreferences("USER_PHONE_NUMBER", Context.MODE_PRIVATE);
        loadItemList();
    }




    // A method to load all the inventory items in a list
    private void loadItemList() {
        ArrayList<ItemModel> itemList = inventoryDBHelper.getAllItems();
        inventoryAdapter = new InventoryAdapter(this, itemList);
        inventoryAdapter.notifyDataSetChanged();
        listView.setAdapter(inventoryAdapter);
    }

    @Override
    protected void onStart(){
        super.onStart();
        loadItemList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        Drawable icon1 = menu.getItem(0).getIcon();
        icon1.mutate();
        Drawable icon2 = menu.getItem(1).getIcon();
        icon2.mutate();
        return super.onCreateOptionsMenu(menu);
    }

    public void checkPermission(String permission, int requestCode){
        if(ContextCompat.checkSelfPermission(HomePage.this, permission) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(HomePage.this, new String[]{permission}, requestCode);
        }else{
            Toast.makeText(HomePage.this, "Permission already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults){

        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);
        if(requestCode == SMS_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(!sp.contains("PHONE_NUMBER")){
                    LinearLayout linearLayout = new LinearLayout(HomePage.this);
                    float scale = getResources().getDisplayMetrics().density;
                    int dpAsPixels = (int) (20 * scale + 0.5f);

                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                    final EditText editText = new EditText(getApplicationContext());
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    linearLayout.addView(editText);
                    final AlertDialog dialog = new AlertDialog.Builder(HomePage.this)
                            .setView(linearLayout)
                            .setTitle("Enter your phone number")
                            .setPositiveButton("Save", null) //Set to null. We override the onclick
                            .create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {

                            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    if(!editText.getText().toString().isEmpty()){
                                        if(editText.getText().toString().length() == 10){
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("PHONE_NUMBER", editText.getText().toString());
                                            editor.putBoolean("RESPONSE", true);
                                            editor.commit();
                                            dialog.dismiss();
                                        }else{
                                            editText.setFocusable(true);
                                            Toast.makeText(getApplicationContext(), "The phone number should be 10-digit long", Toast.LENGTH_SHORT).show();
                                        }

                                    }else{
                                        Toast.makeText(getApplicationContext(), "Please enter your phone number.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    dialog.show();

                }else{
                    Toast.makeText(getApplicationContext(), "The phone number exists in the system.", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(HomePage.this, "Permission to send SMS has been denied.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getTitle().equals("Notification")){
          checkPermission(Manifest.permission.SEND_SMS, SMS_PERMISSION_CODE);
        }else{
            Intent intent = new Intent(HomePage.this, AddEditActivity.class);
            intent.putExtra("Add", "Add");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
}
}
