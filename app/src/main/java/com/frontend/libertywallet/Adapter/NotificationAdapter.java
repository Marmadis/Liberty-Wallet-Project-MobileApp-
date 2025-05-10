package com.frontend.libertywallet.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.frontend.libertywallet.Entity.NotificationItem;
import com.frontend.libertywallet.R;
import com.frontend.libertywallet.fragment.FaqPopupDialogFragment;
import com.frontend.libertywallet.service.ForceLogOut;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private SharedPreferences prefs;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client  = new OkHttpClient();
    private List<NotificationItem> notificationList;

    private Context context;

    public NotificationAdapter(Context context, List<NotificationItem> list) {
        this.context = context;
        this.notificationList = list;
    }
    public NotificationAdapter(List<NotificationItem> list) {
        this.notificationList = list;
    }
    public void setNotifications(List<NotificationItem> newNotifications) {
        this.notificationList.clear();
        this.notificationList.addAll(newNotifications);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, totalAmount, months, monthlyPayment, nextPayment;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);


            title = itemView.findViewById(R.id.title);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            months = itemView.findViewById(R.id.months);
            monthlyPayment = itemView.findViewById(R.id.monthlyPayment);
            nextPayment = itemView.findViewById(R.id.nextPayment);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder holder, int position) {
        NotificationItem item = notificationList.get(position);

        Date date = item.getDate(); // допустим, у тебя уже Date

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.MONTH, 1);

        Date nextMonthDate = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String formattedDate = sdf.format(nextMonthDate);

        holder.title.setText("Loan (installment plan -"+item.getName()+")");
        holder.totalAmount.setText("Total amount:"+item.getGeneralSum()+" ₸");
        holder.months.setText(item.getCurrentNumberOfMonths()+"/"+item.getNumberOfMonths()+" months");
        holder.monthlyPayment.setText(item.getMonthSum()+" ₸ - monthly payment");
        holder.nextPayment.setText("Next payment:"+formattedDate);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isCompleted());


        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                int currentPos = holder.getAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    holder.checkBox.setBackgroundColor(Color.GREEN);
                    sendPayment(item);
                    notificationList.remove(currentPos);
                    notifyItemRemoved(currentPos);
                }
            }
        });

    }
    private void sendPayment(NotificationItem item){
        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        String paymentId = item.getId();
        String token = prefs.getString("access_token", null);

        String BASE_URL = "http://10.0.2.2:9090/payment/update/" + paymentId;
        try {
            JSONObject json = new JSONObject();
            int current = Integer.parseInt(item.getNumberOfMonths()) + 1;
            json.put("NumberOfMonths", current);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            new Thread(() -> {
                try {
                    Response response = client.newCall(request).execute();

                    if (response.code() == 403) {
                        ForceLogOut.forceLogout(context);
                    }

                    if (response.isSuccessful()) {
                        if (context instanceof android.app.Activity) {
                            ((android.app.Activity) context).runOnUiThread(() ->
                                    Toast.makeText(context, "Payment added successfully", Toast.LENGTH_SHORT).show()
                            );
                        }
                    } else {
                        if (context instanceof android.app.Activity) {
                            ((android.app.Activity) context).runOnUiThread(() ->
                                    Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show()
                            );
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "IOException! ", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            }).start();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSON Error" + e, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void addItem(NotificationItem item) {
        notificationList.add(item);
        notifyItemInserted(notificationList.size() - 1);
    }
}
