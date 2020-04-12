package com.android.coronahack.heedcustomer.helpers;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.coronahack.heedcustomer.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NoteViewHolder> {

    private Context context;
    private List<GetNotification> getNotifications;

    AlertDialog.Builder builder;
    AlertDialog alertDialog = null;

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
//                    GetNotification getNotification = (GetNotification) v.getTag();
//                    Toast.makeText(v.getContext(), getNotification.getType()+" -> "+getNotification.getShopName(), Toast.LENGTH_SHORT).show();
                    if (statusText.getText().equals("NOT APPROVED")) {
                        Toast.makeText(context, "Your request is still not approved. Please wait for approval!", Toast.LENGTH_SHORT).show();
                    } else {
                        setupPayment();
                    }
                }
            });
        }
    }

    private void setupPayment() {
        ViewGroup viewGroup = ((Activity) context).findViewById(android.R.id.content);
        View approveView = LayoutInflater.from(context).inflate(R.layout.payment_page, viewGroup, false);
        builder = new AlertDialog.Builder(context);
        builder.setView(approveView);
        alertDialog = builder
                .setCancelable(true)
                .create();
        alertDialog.show();

        ImageView gPay, phonePe, payTm;
        Button volunteerRequest;

        gPay = approveView.findViewById(R.id.gPay);
        phonePe = approveView.findViewById(R.id.phonePe);
        payTm = approveView.findViewById(R.id.payTm);

        volunteerRequest = approveView.findViewById(R.id.volunteerButton);

        volunteerRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1 = builder1.setTitle("VOLUNTEER REQUEST")
                        .setMessage("A nearby volunteer has been pinged! Once you complete your payment, your volunteer will get your order in the specified time slot."+"\n"+"If payment is not completed before your time slot, delivery will be cancelled."+"\n"+"If you are found with no disability / if you aren't a aged person, your act will be reported!");
                AlertDialog dialog = builder1.create();
                dialog.show();
            }
        });
    }
}
