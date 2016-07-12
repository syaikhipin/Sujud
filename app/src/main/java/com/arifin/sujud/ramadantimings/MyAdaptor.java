package com.arifin.sujud.ramadantimings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.arifin.sujud.R;
import com.arifin.sujud.universal.Constant;


import java.util.ArrayList;
import java.util.List;

public class MyAdaptor extends ArrayAdapter<Constant> {

    Context context;
    LayoutInflater inflater;
    List<Constant> worldpopulationlist = null;
    private final List<Constant> values;
    private ArrayList<Constant> arraylist;

    public MyAdaptor(Context context, ArrayList<Constant> search2) {
        // TODO Auto-generated constructor stub
        super(context, 0, search2);
        this.context = context;
        this.values = search2;
        this.worldpopulationlist = search2;
        this.arraylist = new ArrayList<Constant>();
        this.arraylist.addAll(worldpopulationlist);
    }

    @Override
    public int getCount() {
        return worldpopulationlist.size();
    }
    @Override
    public Constant getItem(int position) {
        return worldpopulationlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder viewHolder ;
        int a = position;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = inflater.inflate(R.layout.ramadan_times_row_adapter, null);

            viewHolder=new ViewHolder();

            viewHolder.date=(TextView)view.findViewById(R.id.dateRamadan);
            viewHolder.sehar=(TextView)view.findViewById(R.id.seharRamadan);
            viewHolder.iftar=(TextView)view.findViewById(R.id.aftarRamadan);
//            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String seh=values.get(position).getSuhoor();
        String ifta=values.get(position).getAftar();
        String date=values.get(position).getDate();
        viewHolder.date.setText(date);
        viewHolder.sehar.setText(seh);
        viewHolder.iftar.setText(ifta);
        return view;
    }
    private static class ViewHolder {
        public TextView date;
        public TextView sehar;
        public TextView iftar;

    }
}
