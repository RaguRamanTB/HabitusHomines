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
                        intent.putExtra("type", "page");
                        intent.putExtra("link", "https://www.who.int/emergencies/diseases/novel-coronavirus-2019/advice-for-public/when-and-how-to-use-masks");
                        c.startActivity(intent);
                        break;

                    case "myth-busters":
                        Intent intent1 = new Intent(c, ViewActivity.class);
                        intent1.putExtra("type", "page");
                        intent1.putExtra("link", "https://www.who.int/emergencies/diseases/novel-coronavirus-2019/advice-for-public/myth-busters");
                        c.startActivity(intent1);
                        break;

                    case "getting workplace ready":
                        Intent intent2 = new Intent(c, ViewActivity.class);
                        intent2.putExtra("type", "pdf");
                        intent2.putExtra("link", "https://www.who.int/docs/default-source/coronaviruse/advice-for-workplace-clean-19-03-2020.pdf");
                        c.startActivity(intent2);
                        break;

                    case "advocacy":
                        Intent intent3 = new Intent(c, ViewActivity.class);
                        intent3.putExtra("type", "page");
                        intent3.putExtra("link", "https://www.who.int/emergencies/diseases/novel-coronavirus-2019/advice-for-public/healthy-parenting");
                        c.startActivity(intent3);
                        break;

                    case "watch videos":
                        Intent intent4 = new Intent(c, ViewActivity.class);
                        intent4.putExtra("type", "page");
                        intent4.putExtra("link", "https://www.who.int/emergencies/diseases/novel-coronavirus-2019/advice-for-public/videos");
                        c.startActivity(intent4);
                        break;

                    case "technical guidance":
                        Intent intent5 = new Intent(c, ViewActivity.class);
                        intent5.putExtra("type", "page");
                        intent5.putExtra("link", "https://www.who.int/emergencies/diseases/novel-coronavirus-2019/technical-guidance");
                        c.startActivity(intent5);
                        break;

                    case "strategies, plans and operations":
                        Intent intent6 = new Intent(c, ViewActivity.class);
                        intent6.putExtra("type", "page");
                        intent6.putExtra("link", "https://www.who.int/emergencies/diseases/novel-coronavirus-2019/strategies-plans-and-operations");
                        c.startActivity(intent6);
                        break;

                    case "training and exercises":
                        Intent intent7 = new Intent(c, ViewActivity.class);
                        intent7.putExtra("type", "page");
                        intent7.putExtra("link", "https://www.who.int/emergencies/diseases/novel-coronavirus-2019/training");
                        c.startActivity(intent7);
                        break;

                    default:
                        Toast.makeText(c, menu.getGridText().toLowerCase(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }
}
