package com.project.jsh.androidprj;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jjh on 2017-12-03.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private Context context;
    private List<Item> items;
    private int item_layout;
    private Activity mActivity;

    public CardAdapter(Context context, List<Item> items, int item_layout, Activity activity) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
        this.mActivity = activity;
    }

    public CardAdapter(Context context, List<Item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(v);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(final CardAdapter.ViewHolder holder, final int position) {
        final Item item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), CardContent.class);
                intent.putExtra("information", item.getInformation());
                holder.itemView.getContext().startActivity(intent);
                mActivity.overridePendingTransition(R.anim.abc_slide_in_bottom ,R.anim.abc_slide_out_bottom);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            cardview = (CardView)itemView.findViewById(R.id.cv);
        }
    }
}
