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

import java.util.List;
import java.util.Locale;

public class RecyclerExcavationAdapter extends RecyclerView.Adapter<RecyclerExcavationAdapter.ViewHolder> {
    private List<Excavation> listItems;
    private static OnItemClickListener listener;
    private static OnLongClickListener listenerLong;
    private Context context;

    public RecyclerExcavationAdapter(List<Excavation> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public void setListItems(List<Excavation> listItems) {
        this.listItems = listItems;
    }

    // создаем новые представления (вызывается менеджером макета)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_exc, parent, false); // создаем новый вид
        return new ViewHolder(v);
    }

    // заменяет содержимое представления (вызывается менеджером макета)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Excavation itemList = listItems.get(i);
        viewHolder.tvTypeExcavation.setText(itemList.getTypeExcavation());
        viewHolder.tvName.setText("-" + itemList.getNameExcavation());
        viewHolder.tvAbsoluteElevation.setText("  отм. " + String.format(Locale.ENGLISH, "%(.2f", itemList.getAbsoluteElevation()) + "м");
        viewHolder.tvDepthExcavation.setText("  гл. " + String.format(Locale.ENGLISH, "%(.2f", QueryProcessing.getMaxDepthExc(context, itemList.getKeyExc())) + "м");

        viewHolder.tvDateStart.setText(itemList.getDateStart() + " ");
        viewHolder.tvDateEnd.setText("- " + itemList.getDateEnd());

        viewHolder.tvStayWater.setText("уст. " + String.format(Locale.ENGLISH, "%(.2f", itemList.getWaterStay()) + "м");
        viewHolder.tvStayWaterElevation.setText("  отм. " + String.format(Locale.ENGLISH, "%(.2f", (itemList.getAbsoluteElevation() - itemList.getWaterStay())) + "м");
        viewHolder.tvDateStayWater.setText("  " + itemList.getDateWaterStay());

        if (itemList.getDescriptionExcavation().trim().isEmpty()) {
            viewHolder.tvDescription.setVisibility(View.GONE);
        } else {
            viewHolder.tvDescription.setVisibility(View.VISIBLE);
            viewHolder.tvDescription.setText(itemList.getDescriptionExcavation());
        }

        if (itemList.getWhoWorked().trim().isEmpty() && itemList.getEquipment().trim().isEmpty() && itemList.getPenetrationMethod().trim().isEmpty()) {
            viewHolder.llWhoWork.setVisibility(View.GONE);
        } else if (!itemList.getWhoWorked().trim().isEmpty() || !itemList.getEquipment().trim().isEmpty() || !itemList.getPenetrationMethod().trim().isEmpty()) {
            viewHolder.llWhoWork.setVisibility(View.VISIBLE);
            viewHolder.tvWhoWorked.setText(itemList.getWhoWorked());
            viewHolder.tvEquipment.setText("  " + itemList.getEquipment());
            viewHolder.tvPenetrationMethod.setText("  " + itemList.getPenetrationMethod());
        }

        if (itemList.getLatitude().trim().isEmpty() || itemList.getLongitude().trim().isEmpty()) {
            viewHolder.llCoordinate.setVisibility(View.GONE);
        } else {
            viewHolder.llCoordinate.setVisibility(View.VISIBLE);
            viewHolder.tvLatitude.setText(context.getResources().getString(R.string.tvLatitudeExc) + " " + itemList.getLatitude());
            viewHolder.tvLongitude.setText(context.getResources().getString(R.string.tvLongitudeExc) + " " + itemList.getLongitude());
        }

        if (itemList.getTypeExcavation().equals(context.getResources().getString(R.string.exc_well))) {
            viewHolder.ivTypeExc.setImageResource(R.drawable.ic_exc_well_black_24dp);
        } else if (itemList.getTypeExcavation().equals(context.getResources().getString(R.string.exc_stat_zond))) {
            viewHolder.ivTypeExc.setImageResource(R.drawable.ic_exc_zond_stat_black_24dp);
        } else if (itemList.getTypeExcavation().equals(context.getResources().getString(R.string.exc_pit_dudka))) {
            viewHolder.ivTypeExc.setImageResource(R.drawable.ic_exc_dudka_black_24dp);
        } else if (itemList.getTypeExcavation().equals(context.getResources().getString(R.string.exc_pit))) {
            viewHolder.ivTypeExc.setImageResource(R.drawable.ic_exc_pit_black_24dp);
        } else if (itemList.getTypeExcavation().equals(context.getResources().getString(R.string.exc_din_zond))) {
            viewHolder.ivTypeExc.setImageResource(R.drawable.ic_exc_zond_din_black_24dp);
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

    public void setOnLongClickListener(OnLongClickListener listenerLong) {
        this.listenerLong = listenerLong;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView tvName;
        private TextView tvDescription;
        private TextView tvDateStart;
        private TextView tvDateEnd;
        private TextView tvPenetrationMethod;
        private TextView tvWhoWorked;
        private TextView tvEquipment;
        private TextView tvLatitude;
        private TextView tvLongitude;
        private TextView tvTypeExcavation;
        private TextView tvAbsoluteElevation;
        private TextView tvStayWater;
        private TextView tvDateStayWater;
        private TextView tvDepthExcavation;
        private TextView tvStayWaterElevation;
        private ImageView ivTypeExc;
        private LinearLayout recyclerExcavationLinearLayout;
        private LinearLayout llWhoWork;
        private LinearLayout llCoordinate;

        private ViewHolder(final View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvDateStart = (TextView) itemView.findViewById(R.id.tvDateStart);
            tvDateEnd = (TextView) itemView.findViewById(R.id.tvDateEnd);
            tvPenetrationMethod = (TextView) itemView.findViewById(R.id.tvPenetrationMethod);
            tvWhoWorked = (TextView) itemView.findViewById(R.id.tvWhoWorked);
            tvEquipment = (TextView) itemView.findViewById(R.id.tvEquipment);
            tvLatitude = (TextView) itemView.findViewById(R.id.tvLatitude);
            tvLongitude = (TextView) itemView.findViewById(R.id.tvLongitude);
            tvTypeExcavation = (TextView) itemView.findViewById(R.id.tvTypeExcavation);
            tvStayWaterElevation = (TextView) itemView.findViewById(R.id.tvStayWaterElevation);
            tvAbsoluteElevation = (TextView) itemView.findViewById(R.id.tvAbsoluteElevation);
            tvDepthExcavation = (TextView) itemView.findViewById(R.id.tvDepthExcavation);
            tvStayWater = (TextView) itemView.findViewById(R.id.tvStayWater);
            tvDateStayWater = (TextView) itemView.findViewById(R.id.tvDateStayWater);
            ivTypeExc = (ImageView) itemView.findViewById(R.id.ivTypeExc);
            recyclerExcavationLinearLayout = (LinearLayout) itemView.findViewById(R.id.recyclerExcavationLinearLayout);
            llWhoWork = (LinearLayout) itemView.findViewById(R.id.llWhoWork);
            llCoordinate = (LinearLayout) itemView.findViewById(R.id.llCoordinate);
            recyclerExcavationLinearLayout.setOnClickListener(this);
            recyclerExcavationLinearLayout.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (RecyclerExcavationAdapter.listener != null) {
                RecyclerExcavationAdapter.listener.onItemClick(itemView, getLayoutPosition(), view);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (RecyclerExcavationAdapter.listenerLong != null) {
                RecyclerExcavationAdapter.listenerLong.onItemClick(itemView, getLayoutPosition(), view);
            }
            return true;
        }
    }
}
