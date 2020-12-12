package in.programy.noqpartner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.noqpartner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FoodMenuFragment extends Fragment {


    EditText categoryEditText;
    EditText nameEditText;
    EditText priceEditText;
    Switch switchButton;
    Dialog myDialog;
    ArrayList<String> categoryList;
    ArrayList<Object> list;
    FoodAdapter foodAdapter;
    ListView menuListView;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String shopName;
    String type;
    ArrayList<String> cat;
    ArrayList<String> prc;
    ArrayList<String> nm;
    ArrayList<Boolean> status;
    LinearLayout listHorizontalLayout;
    Drawable drawable;
    String tempCategory;

    Map<String, ArrayList> mp;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_food_menu,container,false);
        Button add = view.findViewById(R.id.addButton);
        menuListView = view.findViewById(R.id.menuListView);

        db = FirebaseFirestore.getInstance();
        categoryList = new ArrayList<>();
        list  = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        foodAdapter = new FoodAdapter(getContext(),list);
        menuListView.setAdapter(foodAdapter);


        shopName = homeActivity.shopName;
        type = homeActivity.type;
//        if(shopName== null || type== null){
//            getShopData();
//        }
        getShopData();

      //  db.collection("Restaurants").document(type).collection(shopName).document("menu").set(null);



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(-1);
            }
        });
        return view;
    }


    public void openDialog(final int index){

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater1 = getActivity().getLayoutInflater();
                View view1 = inflater1.inflate(R.layout.add_menu_dialog,null);
                categoryEditText = view1.findViewById(R.id.categoryEditText);
                nameEditText = view1.findViewById(R.id.nameEditText);
                priceEditText = view1.findViewById(R.id.priceEditText);
                switchButton = view1.findViewById(R.id.switchButton);
                if(index != -1) {
                    tempCategory = ((FoodItem) list.get(index)).getCategory();

                }

                       builder.setTitle("Add New to Menu")
                        .setView(view1)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(categoryEditText.length()>0 && nameEditText.length()>0 && priceEditText.length()>0){
                                    FoodItem foodItem = new FoodItem(categoryEditText.getText().toString(),nameEditText.getText().toString(),priceEditText.getText().toString(),switchButton.isChecked());

                                                        if(mp != null) {
                                                            if(index != -1){
                                                                String tempName = ((FoodItem)list.get(index)).getName();
                                                                int tempIndex = mp.get("name").indexOf(tempName);
                                                                mp.get("name").remove(tempIndex);
                                                                mp.get("price").remove(tempIndex);
                                                                mp.get("category").remove(tempIndex);
                                                                mp.get("status").remove(tempIndex);
                                                            }
                                                            mp.get("name").add(nameEditText.getText().toString());
                                                            mp.get("category").add(categoryEditText.getText().toString());
                                                            mp.get("price").add(priceEditText.getText().toString());
                                                            mp.get("status").add(switchButton.isChecked());
                                                            db.collection("Restaurants").document(type).collection(shopName).document("menu").set(mp);

                                                        }

                                                        else{
                                                            Map<String,ArrayList> mp1 = new HashMap<>();
                                                            ArrayList<String> arrayList1 = new ArrayList<>(Arrays.asList(categoryEditText.getText().toString()));
                                                            ArrayList<String> arrayList2 = new ArrayList<>(Arrays.asList(nameEditText.getText().toString()));
                                                            ArrayList<String> arrayList3 = new ArrayList<>(Arrays.asList(priceEditText.getText().toString()));
                                                            ArrayList<Boolean> arrayList4 = new ArrayList<>(Arrays.asList(switchButton.isChecked()));
                                                            mp1.put("category",arrayList1);
                                                            mp1.put("name",arrayList2);
                                                            mp1.put("price",arrayList3);
                                                            mp1.put("status",arrayList4);
                                                            db.collection("Restaurants").document(type).collection(shopName).document("menu").set(mp1);
                                                        }
                                    if(categoryList.contains(categoryEditText.getText().toString())){
                                        if(index == -1){
                                            list.add(list.indexOf(categoryEditText.getText().toString())+1,foodItem);
                                        }
                                        else{
                                            if(tempCategory.equals(categoryEditText.getText().toString())) {
                                                list.set(index, foodItem);
                                                Log.i("info","no 1");
                                            }
                                            else{

                                                list.remove(index);
                                                if(list.size() == index){
                                                    categoryList.remove(list.get(index-1));
                                                    list.remove(index-1);
                                                    Log.i("info","no 2");
                                                }
                                                else if(foodAdapter.getItemViewType(index)== 1){
                                                    categoryList.remove(list.get(index-1));
                                                    list.remove(index-1);
                                                    Log.i("info","no 3");
                                                }
                                                list.add(list.indexOf(categoryEditText.getText().toString())+1,foodItem);
                                            }
                                        }
                                    }
                                    else{
                                        categoryList.add(categoryEditText.getText().toString());
                                        list.add(categoryEditText.getText().toString());
                                        list.add(foodItem);

                                        if(index != -1){
                                            categoryList.remove(((FoodItem)list.get(index)).getCategory());
                                            list.remove(index);
                                            if(foodAdapter.getItemViewType(index)== 1){
                                                list.remove(index-1);
                                            }
                                        }
                                    }
                                    foodAdapter.notifyDataSetChanged();
                                }
                                else{
                                    Toast.makeText(getContext(),"Any portion can't blank",Toast.LENGTH_SHORT).show();
                                }


                                Intent intent = new Intent(getContext(),homeActivity.class);
                                startActivity(intent);
                                intent.putExtra("menuNo",1);
                            }
                        })
                        .setNegativeButton("Cancel",null);


                if(index == -1){
                    builder.show();
                }
                else{
                    categoryEditText.setText(((FoodItem)list.get(index)).getCategory());
                    priceEditText.setText(((FoodItem)list.get(index)).getPrice());
                    nameEditText.setText(((FoodItem)list.get(index)).getName());
                    switchButton.setChecked(((FoodItem)list.get(index)).getStatus());
                    builder.show();
                }

    }


    public void getShopData() {
                        DocumentReference doc = db.collection("Restaurants").document(type)
                                .collection(shopName).document("menu");


                        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Toast.makeText(getContext(),"Menu load failed",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (snapshot != null && snapshot.exists()) {
                                    mp = (Map) snapshot.getData();

                                    cat = mp.get("category");
                                    prc = mp.get("price");
                                    nm = mp.get("name");
                                    status = mp.get("status");
                                    for (int i = 0; i < prc.size(); i++) {
                                        FoodItem foodItem = new FoodItem(cat.get(i), nm.get(i), prc.get(i),status.get(i));
                                        if (categoryList.contains(cat.get(i))) {
                                            list.add(list.indexOf(cat.get(i)) + 1, foodItem);
                                        } else {
                                            list.add(cat.get(i));
                                            list.add(foodItem);
                                            categoryList.add(cat.get(i));
                                        }

                                        foodAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    Toast.makeText(getContext(),"no Menu to load",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

    }


    public class FoodItem{
        private String category;
        private String name;
        private  String price;
        private  boolean sts;

        FoodItem(String category,String name, String price,boolean sts){
            this.category = category;
            this.name = name;
            this.price = price;
            this.sts = sts;

        }

        public String getCategory(){
            return category;
        }

        public void setCategory(String category){
            this.category = category;
        }

        public boolean getStatus(){
            return sts;
        }

        public void setStatus(boolean status){
            this.sts = status;
        }

        public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }
        public String getPrice(){
            return price;
        }

        public void setPrice(String price){
            this.price = price;
        }


    }

    public class FoodAdapter extends BaseAdapter{
        Context context;
        ArrayList<Object> list;
        private static final int FOOD_ITEM = 0;
        private static final int HEADER = 1;
        private LayoutInflater layoutInflater;

        FoodAdapter(Context context,ArrayList<Object>list){
            this.list = list;
            this.context = context;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if(list.get(position) instanceof FoodItem){
                return FOOD_ITEM;
            }
            else{
                return HEADER;
            }
        }

        @Override
        public int getCount() {
            return list.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(layoutInflater == null){
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }


            if(convertView == null){
                switch (getItemViewType(position)) {
                    case FOOD_ITEM:
                        convertView = layoutInflater.inflate(R.layout.list_food_item, null);
                        break;
                    case HEADER:
                        convertView = layoutInflater.inflate(R.layout.list_header, null);
                        break;
                }
            }
            switch (getItemViewType(position)){
                case FOOD_ITEM:
                    final TextView priceTextView = convertView.findViewById(R.id.priceTextView);
                    final TextView nameTextView = convertView.findViewById(R.id.nameTextView);
                    priceTextView.setText(((FoodItem)list.get(position)).getPrice());
                    nameTextView.setText(((FoodItem)list.get(position)).getName());

                    if(((FoodItem)list.get(position)).getStatus()){
                        priceTextView.setBackgroundColor(Color.WHITE);
                        nameTextView.setBackgroundColor(Color.WHITE);
                    }
                    else{
                        priceTextView.setBackgroundColor(Color.GRAY);
                        nameTextView.setBackgroundColor(Color.GRAY);
                    }


                    ImageButton modifyImageButton = convertView.findViewById(R.id.modifyImageButton);
                    modifyImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openDialog(position);
                        }
                    });
                    break;
                case HEADER:
                    TextView headerTextView = convertView.findViewById(R.id.headerTextView);
                    headerTextView.setText(list.get(position).toString());
                    break;
            }

            return convertView;
        }
    }

}
