package in.programy.noqpartner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.noqpartner.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    FirebaseFirestore db;
    ArrayList<String> orderNoArrayList;
    ArrayList<String> totalAmountArrayList;
    ArrayList<String> orderIDArrayList;
    ArrayList<String> dateArrayList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        orderNoArrayList = new ArrayList<>();
        totalAmountArrayList = new ArrayList<>();
        orderIDArrayList = new ArrayList<>();
        dateArrayList = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_history,container,false);

        ListView orderListView = v.findViewById(R.id.orderListView);
        final MainAdapter adapter = new MainAdapter(getContext(),orderNoArrayList,totalAmountArrayList,dateArrayList);
        orderListView.setAdapter(adapter);

        if(homeActivity.type != null && homeActivity.shopName!= null) {
            db.collection("Restaurants").document(homeActivity.type)
                    .collection(homeActivity.shopName).document("orders").collection("history").orderBy("date", Query.Direction.DESCENDING)
                    .orderBy("order no", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                      //  Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        if (doc.exists()) {
                            totalAmountArrayList.add((String)doc.get("total amount"));
                            orderNoArrayList.add((String) doc.get("order no"));
                            orderIDArrayList.add((String) doc.get("order id"));
                            dateArrayList.add((String)doc.get("date"));
                            adapter.notifyDataSetChanged();
                        }
                    }


                }
            });


            orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getContext(),historyOrderSummary.class);
                    intent.putExtra("total amount",totalAmountArrayList.get(position));
                    intent.putExtra("order no",orderNoArrayList.get(position));
                    intent.putExtra("order id",orderIDArrayList.get(position));
                    intent.putExtra("order type","history");
                    startActivity(intent);
                }
            });

        }

        return v;
    }

    class MainAdapter extends BaseAdapter {
        Context context;
        ArrayList<String> NoArrayList;
        ArrayList<String> amountArrayList;
        ArrayList<String> dateArrayList;
        LayoutInflater layoutInflater;

        MainAdapter(Context c,ArrayList<String> NoArrayList,ArrayList<String> amountArrayList,ArrayList<String>dateArrayList){
            this.context = c;
            this.NoArrayList = NoArrayList;
            this.amountArrayList = amountArrayList;
            this.dateArrayList = dateArrayList;
        }

        @Override
        public int getCount() {
            return orderNoArrayList.size();
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
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.list_history,null);
            }

            TextView orderNoTextView = convertView.findViewById(R.id.orderNoTextView);
            TextView priceTextView = convertView.findViewById(R.id.itemPriceTextView);
            TextView dateTextView = convertView.findViewById(R.id.dateTextView);
            dateTextView.setText(dateArrayList.get(position));
            orderNoTextView.setText(NoArrayList.get(position));
            priceTextView.setText(amountArrayList.get(position)+" INR");

            return convertView;
        }
    }
}
