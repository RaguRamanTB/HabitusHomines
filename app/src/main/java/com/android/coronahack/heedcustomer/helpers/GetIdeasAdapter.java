package com.android.coronahack.heedcustomer.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.coronahack.heedcustomer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GetIdeasAdapter extends RecyclerView.Adapter<GetIdeasAdapter.IdeaViewHolder> {

    Context context;
    List<GetIdeas> getIdeas;
    DatabaseReference referenceIdeas;
    ValueEventListener valueEventListener;
    String contentIdea, ideaBy;
    AlertDialog.Builder builder;
    AlertDialog alertDialog = null;

    public GetIdeasAdapter(Context context, List<GetIdeas> getIdeas) {
        this.context = context;
        this.getIdeas = getIdeas;
    }

    @NonNull
    @Override
    public GetIdeasAdapter.IdeaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.idea_item, parent, false);
        return new IdeaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GetIdeasAdapter.IdeaViewHolder holder, int position) {
        holder.itemView.setTag(getIdeas.get(position));
        GetIdeas gi = getIdeas.get(position);
        holder.ideasTitleText.setText(gi.getTitleIdea());
    }

    @Override
    public int getItemCount() {
        return getIdeas.size();
    }

    public class IdeaViewHolder extends RecyclerView.ViewHolder {

        public TextView ideasTitleText;

        public IdeaViewHolder(@NonNull View itemView) {
            super(itemView);
            ideasTitleText = itemView.findViewById(R.id.titleGiven);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                getContentAndBy(ideasTitleText.getText().toString());
                }
            });
        }
    }

    private void getContentAndBy(final String title) {
        referenceIdeas = FirebaseDatabase.getInstance().getReference().child("ideas");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    GetIdeas getIdeas = dss.getValue(GetIdeas.class);
                    assert getIdeas != null;
                    if (getIdeas.getTitleIdea().equals(title)) {
                        contentIdea = getIdeas.getContentIdea();
                        ideaBy = getIdeas.getIdeaBy();
                        builder = new AlertDialog.Builder(context);
                        builder.setTitle("CONTENT AND GIVEN BY");
                        builder.setMessage(contentIdea + "\n\n" + "Idea by : " + ideaBy.toUpperCase())
                                .setCancelable(true);
                        alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        referenceIdeas.addValueEventListener(valueEventListener);
    }
}
