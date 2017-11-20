package com.gmail.fomichov.m.drillingmagazine.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.geology.Probe;

import java.util.List;
import java.util.Locale;

public class RecyclerProbeAdapter extends RecyclerView.Adapter<RecyclerProbeAdapter.ViewHolder> {
    private List<Probe> listItems;
    private static OnItemClickListener listener;
    private Context context;
    private double absoluteElevation;

    public RecyclerProbeAdapter(List<Probe> listItems, Context context, double absoluteElevation) {
        this.listItems = listItems;
        this.context = context;
        this.absoluteElevation = absoluteElevation;
    }

    public void setListItems(List<Probe> listItems) {
        this.listItems = listItems;
    }

    // создаем новые представления (вызывается менеджером макета)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_probe, parent, false); // создаем новый вид
        return new ViewHolder(v);
    }

    // заменяет содержимое представления (вызывается менеджером макета)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Probe itemList = listItems.get(i);
        viewHolder.tvDepth.setText("гл. " + String.format(Locale.ENGLISH, "%(.2f",(itemList.getDepth())) + "м");
        if (absoluteElevation != 0) {
            viewHolder.tvAbsoluteElevation.setText("отм. " + String.format(Locale.ENGLISH, "%(.2f",(absoluteElevation - itemList.getDepth())) + "м");
        }
        if (String.valueOf(itemList.getDescription()).trim().isEmpty()) {
            viewHolder.tvDescription.setVisibility(View.GONE);
        } else {
            viewHolder.tvDescription.setVisibility(View.VISIBLE);
            viewHolder.tvDescription.setText(itemList.getDescription());
        }
        if (itemList.getType().equals(context.getResources().getString(R.string.probe_destroyed))) {
            viewHolder.ivTypeProbe.setImageResource(R.drawable.ic_probe_destroyed_black_24dp);
        } else if (itemList.getType().equals(context.getResources().getString(R.string.probe_full))) {
            viewHolder.ivTypeProbe.setImageResource(R.drawable.ic_probe_full_black_24dp);
        } else if (itemList.getType().equals(context.getResources().getString(R.string.probe_water))) {
            viewHolder.ivTypeProbe.setImageResource(R.drawable.ic_probe_water_blue_24dp);
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvDepth;
        private TextView tvAbsoluteElevation;
        private TextView tvDescription;
        private ImageView ivEdit;
        private ImageView ivDelete;
        private ImageView ivTypeProbe;

        private ViewHolder(final View itemView) {
            super(itemView);
            tvDepth = (TextView) itemView.findViewById(R.id.tvDepth);
            tvAbsoluteElevation = (TextView) itemView.findViewById(R.id.tvAbsoluteElevation);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
            ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            ivTypeProbe = (ImageView) itemView.findViewById(R.id.ivTypeProbe);
            ivEdit.setOnClickListener(this);
            ivDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (RecyclerProbeAdapter.listener != null) {
                RecyclerProbeAdapter.listener.onItemClick(itemView, getLayoutPosition(), view);
            }
        }
    }
}
