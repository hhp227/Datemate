package com.hhp227.datemate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hhp227.datemate.R;
import com.hhp227.datemate.dto.Comment;

import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mCommentIds;
    private List<Comment> mComments;

    public CommentAdapter(Context mContext, List<String> mCommentIds, List<Comment> mComments) {
        this.mContext = mContext;
        this.mCommentIds = mCommentIds;
        this.mComments = mComments;
    }

    @Override
    public int getCount() {
        return mComments.size();
    }

    @Override
    public Object getItem(int position) {
        return mComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mInflater == null)
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.item_comment, parent, false);

        Comment comment = mComments.get(position);

        TextView authorView = convertView.findViewById(R.id.comment_author);
        TextView bodyView = convertView.findViewById(R.id.comment_body);

        authorView.setText(comment.author);
        bodyView.setText(comment.text);

        return convertView;
    }
}