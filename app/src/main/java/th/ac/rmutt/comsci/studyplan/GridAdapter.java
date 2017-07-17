package th.ac.rmutt.comsci.studyplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by Puttapan on 11/7/2560.
 */

public class GridAdapter extends BaseAdapter{

    Context context;
    private final int[] images;
    View view;
    LayoutInflater layoutInflater;

    public GridAdapter(Context context, int[] images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){

            view = new View(context);
            view = layoutInflater.inflate(R.layout.view_plan_icon, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.icon_plan);
            imageView.setImageResource(images[position]);

        }
        return view;
    }
}
