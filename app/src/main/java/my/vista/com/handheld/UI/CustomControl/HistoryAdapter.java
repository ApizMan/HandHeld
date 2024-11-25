package my.vista.com.handheld.UI.CustomControl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import my.vista.com.handheld.Entity.SummonIssuanceInfo;
import my.vista.com.handheld.R;

/**
 * Created by hp on 22/9/2016.
 */
/**
 * Created by hp on 14/4/2017.
 */
public class HistoryAdapter extends ArrayAdapter<SummonIssuanceInfo> {
    private final Context context;
    private final ArrayList<SummonIssuanceInfo> values;

    public HistoryAdapter(Context context, int resource, ArrayList<SummonIssuanceInfo> values) {
        super(context, resource, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return values.size();
    }

    @Override
    public SummonIssuanceInfo getItem(int position) {
        // TODO Auto-generated method stub
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View rowView = convertView;

        if(rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            rowView = inflater.inflate(R.layout.history_item, null);
        }

        TextView textFirst = (TextView) rowView.findViewById(R.id.first_line);
        TextView textSecond = (TextView) rowView.findViewById(R.id.second_line);
        TextView textThird = (TextView) rowView.findViewById(R.id.third_line);
        textFirst.setText(values.get(position).NoticeSerialNo);
        textSecond.setText(values.get(position).VehicleNo);
        //textThird.setText(values.get(position).);

        return rowView;
    }
}