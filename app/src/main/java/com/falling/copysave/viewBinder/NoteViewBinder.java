package com.falling.copysave.viewBinder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.falling.copysave.R;
import com.falling.copysave.bean.NoteBean;
import com.falling.copysave.view.MyCardView;

import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by falling on 2017/7/19.
 */

public class NoteViewBinder  extends ItemViewBinder<NoteBean, NoteViewBinder.ViewHolder> {
    @NonNull
    @Override
    protected NoteViewBinder.ViewHolder onCreateViewHolder(
            @NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        MyCardView root = (MyCardView) inflater.inflate(R.layout.item_note, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull NoteBean noteBean) {
        holder.id = noteBean.getId();
        holder.copyContent.setText(noteBean.getCopyContent());
        holder.comment.setText(noteBean.getComment());
        holder.date.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss",noteBean.getDate()));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private long id;
        @NonNull private final TextView copyContent;
        @NonNull private final TextView comment;
        @NonNull private final TextView date;

        ViewHolder(MyCardView itemView) {
            super(itemView);
            copyContent = (TextView) itemView.findViewById(R.id.copyContent);
            comment = (TextView) itemView.findViewById(R.id.comment);
            date = (TextView) itemView.findViewById(R.id.date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),id + " clicked",Toast.LENGTH_SHORT).show();
                }
            });
            itemView.setRemoveListener(new MyCardView.RemoveListener() {
                @Override
                public void removeItem(MyCardView view, MyCardView.RemoveDirection direction) {
                    Toast.makeText(view.getContext(),id + " removed" + direction,Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
