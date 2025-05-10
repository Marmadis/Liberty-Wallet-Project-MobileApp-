package com.frontend.libertywallet.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.frontend.libertywallet.Entity.NotificationItem;
import com.frontend.libertywallet.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationItem> notificationList;

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
