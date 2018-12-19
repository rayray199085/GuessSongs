package com.project.stephencao.guesssongs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.BaseAdapter;
import android.widget.Button;
import com.project.stephencao.guesssongs.R;
import com.project.stephencao.guesssongs.animation.WordsDisplayAnimation;
import com.project.stephencao.guesssongs.bean.GridViewButtonItems;
import com.project.stephencao.guesssongs.view.MyGridView;

import java.util.List;

public class MyGridViewAdapter extends BaseAdapter {
    private List<GridViewButtonItems> gridViewButtonItemsList;
    private Context context;
    private LayoutInflater inflater;
    private MyGridView myGridView;

    public MyGridViewAdapter(List<GridViewButtonItems> gridViewButtonItemsList, Context context, MyGridView myGridView) {
        this.myGridView = myGridView;
        this.gridViewButtonItemsList = gridViewButtonItemsList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return gridViewButtonItemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return gridViewButtonItemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.view_word_button,null);
            viewHolder.wordButton = convertView.findViewById(R.id.btn_grid_view_item);
            viewHolder.wordButton.setClickable(false);
            viewHolder.wordButton.setFocusable(false);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        GridViewButtonItems gridViewButtonItems = gridViewButtonItemsList.get(position);
        gridViewButtonItemsList.get(position).setButton(viewHolder.wordButton);
        viewHolder.wordButton.setText(gridViewButtonItems.getmContent());
        AnimationSet animationSet = WordsDisplayAnimation.addAnimation();
        animationSet.setStartOffset(position * 100);
        viewHolder.wordButton.startAnimation(animationSet);

        return convertView;
    }
    class ViewHolder{
        public Button wordButton;
    }
}
