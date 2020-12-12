package in.programy.noqpartner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noqpartner.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class historyOrderSummary extends AppCompatActivity {

    String totalAmount;
    String totalQuantity;
    String totalItem;
    String time;
    String orderNo;
    String orderId;
    String date;
    String canteenName;
    String orderType;
    String userUid;
    ArrayList<String>itemName;
    ArrayList<String> itemPrice;
    ArrayList<String> itemQuantity;
    FirebaseFirestore db;
    MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order_summary);

        db = FirebaseFirestore.getInstance();
        Intent intent= getIntent();
        totalAmount = intent.getStringExtra("total amount");
        orderId = intent.getStringExtra("order id");
        orderNo = intent.getStringExtra("order no");
        orderType = intent.getStringExtra("order type");

//        ListView summaryListView = findViewById(R.id.summaryListView);
//        adapter = new MainAdapter(getApplicationContext(),itemQuantity,itemName,itemPrice);
//        summaryListView.setAdapter(adapter);

        if(orderType.equals("in q")){
                Button completeButton = findViewById(R.id.completeButton);
            completeButton.setVisibility(View.VISIBLE);
        }

        itemName = new ArrayList<>();
        itemPrice = new ArrayList<>();
        itemQuantity = new ArrayList<>();

        db.collection("Restaurants").document(homeActivity.type)
                .collection(homeActivity.shopName).document("orders").collection(orderType).document(orderId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(historyOrderSummary.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            totalQuantity =((String) documentSnapshot.get("total quantity"));
                            totalItem = ((String)documentSnapshot.get("total item"));
                            time = (String) documentSnapshot.get("time");
                            date = (String) documentSnapshot.get("date");
                            userUid = (String) documentSnapshot.get("user uid");
                            canteenName = (String) documentSnapshot.get("canteen name");
                            itemName.addAll((ArrayList)documentSnapshot.get("item name"));
                            itemPrice.addAll((ArrayList)documentSnapshot.get("price"));
                            itemQuantity.addAll((ArrayList)documentSnapshot.get("quantity"));
                           // adapter.notifyDataSetChanged();
                            TextView canteenNameTextView = findViewById(R.id.canteenNameTextView);
                            TextView orderIdTextView = findViewById(R.id.orderIdTextView);
                            TextView orderNoTextView = findViewById(R.id.orderNoTextView);
                            TextView itemQuantityTextView = findViewById(R.id.itemQuantityTextView);

                            canteenNameTextView.setText(canteenName);
                            orderIdTextView.setText("Id: "+orderId);
                            itemQuantityTextView.setText("Item: "+totalItem+"       Quantity: "+totalQuantity+"     Total: "+totalAmount);
                            orderNoTextView.setText("No: "+orderNo+"  Date & Time: "+date+" "+time);

                            ListView summaryListView = findViewById(R.id.summaryListView);
                            adapter = new MainAdapter(historyOrderSummary.this,itemQuantity,itemName,itemPrice);
                            summaryListView.setAdapter(adapter);

                        }
                        else {
                          //  Toast.makeText(historyOrderSummary.this, "No data to Show !!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    class MainAdapter extends BaseAdapter{
        Context context;
        ArrayList<String> quantityList;
        ArrayList<String> itemList;
        ArrayList<String> priceList;
        LayoutInflater layoutInflater;


        MainAdapter(Context context, ArrayList<String> quantityList,ArrayList<String> itemList,ArrayList<String>priceList){
            this.context = context;
            this.itemList = itemList;
            this.quantityList  = quantityList;
            this.priceList = priceList;
            Log.i("resutl","entered");
        }


        @Override
        public int getCount() {
            return itemPrice.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(layoutInflater == null){
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if(convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_order_summary_item,null);
            }


                TextView itemTextView = convertView.findViewById(R.id.itemTextView);
                TextView quantityTextView = convertView.findViewById(R.id.quantityTextView);
                TextView priceTextView = convertView.findViewById(R.id.priceTextView);
                TextView amountTextView = convertView.findViewById(R.id.amountTextView);

                itemTextView.setText(itemList.get(position));
                quantityTextView.setText(quantityList.get(position));
                priceTextView.setText(priceList.get(position));
                amountTextView.setText(String.valueOf(Integer.valueOf(priceList.get(position))*Integer.valueOf(quantityList.get(position))));

            return convertView;
        }
    }


    public void Complete(View view){
        Map<String,Object> mp = new HashMap<>();
        mp.put("canteen name",canteenName);
        mp.put("date",date);
        mp.put("order id",orderId);
        mp.put("order no",orderNo);
        mp.put("time",time);
        mp.put("total amount",totalAmount);
        mp.put("total item",totalItem);
        mp.put("total quantity",totalQuantity);
        mp.put("item name",itemName);
        mp.put("price",itemPrice);
        mp.put("quantity",itemQuantity);
        mp.put("user uid",userUid);

        db.collection("Restaurants").document(homeActivity.type)
                .collection(homeActivity.shopName).document("orders")
                .collection("history").document(orderId).set(mp);

        db.collection("Restaurants").document(homeActivity.type)
                .collection(homeActivity.shopName).document("orders")
                .collection("in q").document(orderId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       Log.i("result","deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("error","deleting failed");
                    }
                });

        Map<String,String>m = new HashMap<>();
        m.put("order id",orderId);
        m.put("canteen name",canteenName.toLowerCase());
        m.put("type",homeActivity.type.toLowerCase());
        m.put("total amount",totalAmount);
        m.put("order no",orderNo);
        m.put("date",date);

        db.collection("users").document(userUid).collection("history").document(orderId).set(m);

        db.collection("users").document(userUid).collection("in q").document(orderId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("result","deleted");
                    }
                });
    }
}
