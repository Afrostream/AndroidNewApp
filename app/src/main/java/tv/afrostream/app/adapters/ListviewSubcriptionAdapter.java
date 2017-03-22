package tv.afrostream.app.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tv.afrostream.app.R;
import tv.afrostream.app.models.ListPlansModel;

/**
 * Created by bahri on 31/01/2017.
 */


public class ListviewSubcriptionAdapter extends ArrayAdapter<ListPlansModel> {
    private final Context context;
    private final  ArrayList<ListPlansModel>  values;

    public ListviewSubcriptionAdapter(Context context, ArrayList<ListPlansModel> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listview_plans_row, parent, false);

        ListPlansModel nb= values.get(position);



        TextView txtname = (TextView) rowView.findViewById(R.id.txtname);
        TextView txtdescription = (TextView) rowView.findViewById(R.id.txtdescirption);
        TextView txtprice = (TextView) rowView.findViewById(R.id.txtprice);
        ImageView imagelogo = (ImageView) rowView.findViewById(R.id.icon);
        ImageView planlogo = (ImageView) rowView.findViewById(R.id.logoplan);

        txtname.setText(nb.getName());
        txtdescription.setText(nb.getDescription());

        if (!nb.getInternalPlanUuid().equals("coupon")) {
            Double aa = Double.parseDouble(nb.getAmountInCents()) / 100;

            txtprice.setText(aa.toString() + " " + nb.getCurrency());
        }else
        {
            txtprice.setVisibility(View.GONE);
            imagelogo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.coupon));
        }

        if (nb.getShowlogo())
            planlogo.setVisibility(View.VISIBLE);
        else
            planlogo.setVisibility(View.GONE);

        // change the icon for Windows and iPhone


        return rowView;
    }
}