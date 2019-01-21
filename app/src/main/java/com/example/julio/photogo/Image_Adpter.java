package com.example.julio.photogo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Image_Adpter extends RecyclerView.Adapter<Image_Adpter.ImageViewHolder> implements View.OnClickListener{

    private View.OnClickListener listener;
    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<ImageData> imageList;

    //getting the context and product list with constructor
    public Image_Adpter(Context mCtx, List<ImageData> imageList) {
        this.mCtx = mCtx;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public Image_Adpter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.images_layout,parent,false);
        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
       /* LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.images_layout, null);*/
        view.setOnClickListener(this);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Image_Adpter.ImageViewHolder holder, int position) {
        //holder.textView.setText(imageList.get(position).getLocalname().toString());
        if(imageList.get(position).getRouteImg()!=null){
            uploadImgfromWeb(imageList.get(position).getRouteImg(),holder);
        }

    }

    private void uploadImgfromWeb(String routeImg, final ImageViewHolder holder) {
        ConnectionHTTP con=new ConnectionHTTP();
        String ip=con.getIp();
        String url=ip+routeImg;
        Glide.with(mCtx)
                .load(url)
                .thumbnail(0.5f)
                .into(holder.imageView);

    }

    @Override
    public void onClick(View v) {
        if (listener!=null){
            listener.onClick(v);
        }
    }
    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {


        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
