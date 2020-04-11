package com.android.coronahack.heedcustomer.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.coronahack.heedcustomer.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NoteViewHolder> {

    private Context context;
    private List<GetNotification> getNotifications;

    public NotificationAdapter(Context context, List<GetNotification> getNotifications) {
        this.context = context;
        this.getNotifications = getNotifications;
    }

    @NonNull
    @Override
    public NotificationAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NoteViewHolder holder, int position) {
        holder.itemView.setTag(getNotifications.get(position));
        GetNotification gn = getNotifications.get(position);
        holder.typeText.setText(gn.getType());
        holder.shopNameText.setText(gn.getShopName());
        holder.statusText.setText(gn.getStatus());
        holder.timeSlotText.setText(gn.getTimeSlot());
    }

    @Override
    public int getItemCount() {
        return getNotifications.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        public TextView typeText, shopNameText, statusText, timeSlotText;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            typeText = itemView.findViewById(R.id.typeText);
            shopNameText = itemView.findViewById(R.id.shopNameText);
            statusText = itemView.findViewById(R.id.requestText);
            timeSlotText = itemView.findViewById(R.id.timeSlotText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetNotification getNotification = (GetNotification) v.getTag();
                    Toast.makeText(v.getContext(), getNotification.getType()+" -> "+getNotification.getShopName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
