package com.sweteam5.ladybugadmin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {
    Context context;                        // Caching the context of an activity that uses the Adapter
    ArrayList<NoticeInfo> noticeArrayList;  // Notice information array list
    private AppCompatActivity activity;     // Caching an activity that uses the Adapter

    public NoticeAdapter(Context context, ArrayList<NoticeInfo> noticeArrayList, AppCompatActivity activity){
        this.context = context;
        this.noticeArrayList = noticeArrayList;
        this.activity = activity;
    }

    // when noticeViewHolder called first
    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notice_group_layout, parent, false);
        return new NoticeViewHolder(v);
    }

    // Viewholder of the recycler view
    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        // set text the list of noitice
        NoticeInfo notice = noticeArrayList.get(position);
        holder.title.setText(notice.title);
        if(notice.date.length() > 14)
            holder.date.setText(notice.date.substring(0, notice.date.length() - 3));
        else
            holder.date.setText(notice.date);

        // when title was clicked go to modify the notice
        holder.title.setOnClickListener(new View.OnClickListener() {//click the title to modify the notice
            @Override
            public void onClick(View view) {
                NoticeMngActivity.dm.findModifyNotice(activity, context, notice.title);
            }
        });

        // Set delete button listener
        holder.delete_button.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setTitle("공지 삭제").setMessage("이 공지를 삭제하겠습니까?")
            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Delete the notice from servers
                    NoticeMngActivity.dm.deleteNotice(activity, notice.title);
                }
            })
            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) { }
            });

            // Show the dialogs
            AlertDialog dialog = builder.create();
            dialog.show();

        });
    }


    // Get size of notice list
    @Override
    public int getItemCount() {
        return noticeArrayList.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder{
        TextView title;             // Title of notice TextView
        TextView date;              // Datetime of notice TextView
        ImageButton delete_button;  // Delete of notice button

        // Caching Views from layout
        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            title =  itemView.findViewById(R.id.noticeTitleTextView);
            date = itemView.findViewById(R.id.noticeDateTimeTextView);
            delete_button = itemView.findViewById(R.id.deleteNoticeButton);
        }
    }
}
