package com.android.coronahack.heedcustomer.helpers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.coronahack.heedcustomer.R;
import com.android.coronahack.heedcustomer.activities.ViewActivity;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

    Context c;
    ArrayList<GridItem> menuItems;

    public GridAdapter(Context c, ArrayList<GridItem> menuItems) {
        this.c = c;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(c).inflate(R.layout.item_gridlayout,
                    parent, false);
        }

        final GridItem menu = (GridItem) this.getItem(position);
        ImageView imageView = convertView.findViewById(R.id.grid_picture);
        TextView textView = convertView.findViewById(R.id.grid_text);
        textView.setSelected(true);
        textView.setText(menu.getGridText());
        imageView.setImageResource(menu.getGridPicture());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (menu.getGridText().toLowerCase()) {
                    case "when and how to use masks":
                        Intent intent = new Intent(c, ViewActivity.class);
                        intent.putExtra("link", "https://www.who.int/emergencies/diseases/novel-coronavirus-2019/advice-for-public/when-and-how-to-use-masks");
                        c.startActivity(intent);
                        break;

                    default:
                        Toast.makeText(c, menu.getGridText().toLowerCase(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }
}
