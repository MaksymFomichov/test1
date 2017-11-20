package com.gmail.fomichov.m.drillingmagazine.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.database.QueryProcessing;
import com.gmail.fomichov.m.drillingmagazine.geology.Excavation;
import com.gmail.fomichov.m.drillingmagazine.geology.ObjectGeology;
import com.gmail.fomichov.m.drillingmagazine.utils.MyDateUtils;

import java.text.ParseException;
import java.util.List;

public class RecyclerObjectsAdapter extends RecyclerView.Adapter<RecyclerObjectsAdapter.ViewHolder> {
    private List<ObjectGeology> listItems;
    private static OnItemClickListener listener;
    private static RecyclerExcavationAdapter.OnLongClickListener listenerLong;
    private Context context;

    public RecyclerObjectsAdapter(List<ObjectGeology> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public void setListItems(List<ObjectGeology> listItems) {
        this.listItems = listItems;
    }

    // создаем новые представления (вызывается менеджером макета)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_obj, parent, false); // создаем новый вид
        return new ViewHolder(v);
    }

    // заменяет содержимое представления (вызывается менеджером макета)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ObjectGeology itemList = listItems.get(i);
        viewHolder.twName.setText(itemList.getName());
        if (itemList.getDescription().trim().isEmpty()) {
            viewHolder.twDescription.setVisibility(View.GONE);
        } else {
            viewHolder.twDescription.setVisibility(View.VISIBLE);
            viewHolder.twDescription.setText(itemList.getDescription());
        }

        // получаем список выработок обьекта
        List<Excavation> list = QueryProcessing.getListExcavation(context, itemList.getName());
        // наполняем массивы
        if (list.size() > 0) {
            String[] arrayDateStart = new String[list.size()];
            String[] arrayDateEnd = new String[list.size()];
            for (int j = 0; j < list.size(); j++) {
                arrayDateStart[j] = list.get(j).getDateStart();
                arrayDateEnd[j] = list.get(j).getDateEnd();
            }

            // сортируем по дате
            String tempDateStart, tempDateEnd;
            for (int m = 0; m < arrayDateStart.length; m++) {
                for (int k = m + 1; k < arrayDateStart.length; k++) {
                    try {
                        if (!MyDateUtils.datesComparing(MyDateUtils.convertStringToDate(arrayDateStart[m]), MyDateUtils.convertStringToDate(arrayDateStart[k]))) {
                            tempDateStart = arrayDateStart[m];
                            arrayDateStart[m] = arrayDateStart[k];
                            arrayDateStart[k] = tempDateStart;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            for (int m = 0; m < arrayDateEnd.length; m++) {
                for (int k = m + 1; k < arrayDateEnd.length; k++) {
                    try {
                        if (MyDateUtils.datesComparing(MyDateUtils.convertStringToDate(arrayDateEnd[m]), MyDateUtils.convertStringToDate(arrayDateEnd[k]))) {
                            tempDateEnd = arrayDateEnd[m];
                            arrayDateEnd[m] = arrayDateEnd[k];
                            arrayDateEnd[k] = tempDateEnd;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            viewHolder.llDateObject.setVisibility(View.VISIBLE);
            viewHolder.twDateStart.setText(arrayDateStart[0] + "-");
            viewHolder.twDateEnd.setText(arrayDateEnd[0]);
        } else {
            viewHolder.llDateObject.setVisibility(View.GONE);
        }
    }

    // возвращает размер вашего набора данных (вызывается менеджером макета)
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, View view);
    }

    public interface OnLongClickListener {
        void onItemClick(View itemView, int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnLongClickListener(RecyclerExcavationAdapter.OnLongClickListener listenerLong) {
        this.listenerLong = listenerLong;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView twName;
        public TextView twDescription;
        public TextView twDateStart;
        public TextView twDateEnd;
        public LinearLayout recyclerObjectLinearLayout;
        public LinearLayout llDateObject;

        public ViewHolder(final View itemView) {
            super(itemView);
            twName = (TextView) itemView.findViewById(R.id.tvName);
            twDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            twDateStart = (TextView) itemView.findViewById(R.id.tvDateStart);
            twDateEnd = (TextView) itemView.findViewById(R.id.tvDateEnd);
            recyclerObjectLinearLayout = (LinearLayout) itemView.findViewById(R.id.recyclerObjectLinearLayout);
            llDateObject = (LinearLayout) itemView.findViewById(R.id.llDateObject);
            recyclerObjectLinearLayout.setOnClickListener(this);
            recyclerObjectLinearLayout.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (RecyclerObjectsAdapter.listener != null) {
                RecyclerObjectsAdapter.listener.onItemClick(itemView, getLayoutPosition(), view);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (RecyclerObjectsAdapter.listenerLong != null) {
                RecyclerObjectsAdapter.listenerLong.onItemClick(itemView, getLayoutPosition(), view);
            }
            return true;
        }
    }
}
