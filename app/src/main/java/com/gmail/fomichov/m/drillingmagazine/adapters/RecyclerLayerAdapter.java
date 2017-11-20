package com.gmail.fomichov.m.drillingmagazine.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.geology.Layer;

import java.util.List;
import java.util.Locale;

public class RecyclerLayerAdapter extends RecyclerView.Adapter<RecyclerLayerAdapter.ViewHolder> {
    private List<Layer> listItems;
    private static OnItemClickListener listener;
    private Context context;
    private double absoluteElevation;

    public RecyclerLayerAdapter(List<Layer> listItems, Context context, double absoluteElevation) {
        this.listItems = listItems;
        this.context = context;
        this.absoluteElevation = absoluteElevation;
    }

    public void setListItems(List<Layer> listItems) {
        this.listItems = listItems;
    }

    // создаем новые представления (вызывается менеджером макета)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layer, parent, false); // создаем новый вид
        return new ViewHolder(v);
    }

    // заменяет содержимое представления (вызывается менеджером макета)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int count = i + 1;
        Layer itemList = listItems.get(i);
        viewHolder.tvCountLayer.setText(String.valueOf(count));
        viewHolder.tvStart.setText(String.format(Locale.ENGLISH, "%(.2f", itemList.getStartDepth()));
        viewHolder.tvEnd.setText("-" + String.format(Locale.ENGLISH, "%(.2f", itemList.getEndDepth()) + "м");
        viewHolder.tvPower.setText("мощ. " + String.format(Locale.ENGLISH, "%(.2f", (itemList.getLayerPower())) + "м");
        if (absoluteElevation != 0) {
            viewHolder.tvAbsoluteElevation.setText("отм. " + String.format(Locale.ENGLISH, "%(.2f", (absoluteElevation - itemList.getEndDepth())) + "м");
        }
        if (itemList.getDescription() == null) {
            viewHolder.tvDescription.setVisibility(View.GONE);
        } else {
            viewHolder.tvDescription.setText(itemList.getDescription());
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
        private TextView tvCountLayer;
        private TextView tvStart;
        private TextView tvEnd;
        private TextView tvPower;
        private TextView tvAbsoluteElevation;
        private TextView tvDescription;
        private ImageView ivEdit;
        private ImageView ivDelete;

        private ViewHolder(final View itemView) {
            super(itemView);
            tvCountLayer = (TextView) itemView.findViewById(R.id.tvCountLayer);
            tvStart = (TextView) itemView.findViewById(R.id.tvStart);
            tvEnd = (TextView) itemView.findViewById(R.id.tvEnd);
            tvPower = (TextView) itemView.findViewById(R.id.tvPower);
            tvAbsoluteElevation = (TextView) itemView.findViewById(R.id.tvAbsoluteElevation);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
            ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            ivEdit.setOnClickListener(this);
            ivDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (RecyclerLayerAdapter.listener != null) {
                RecyclerLayerAdapter.listener.onItemClick(itemView, getLayoutPosition(), view);
            }
        }
    }
}
