package my.vista.com.handheld.UI.CustomControl;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import my.vista.com.handheld.Entity.BaseEntity;

/**
 * Created by hp on 22/9/2016.
 */
class CustomSpinnerAdapter extends ArrayAdapter<BaseEntity> {

    private Context context;
    private ArrayList<BaseEntity> datas;

    public CustomSpinnerAdapter(Context context, int textViewResourceId,
                       ArrayList<BaseEntity> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.datas = values;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public BaseEntity getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(datas.get(position).Text);

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(datas.get(position).Text);

        return label;
    }
}