package com.example.julio.photogo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Image_Home_Adapter extends RecyclerView.Adapter<Image_Home_Adapter.ImageViewHolder> implements View.OnClickListener{

    private View.OnClickListener listener;
    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<ImageData> imageList;

    //getting the context and product list with constructor
    public Image_Home_Adapter(Context mCtx, List<ImageData> imageList) {
        this.mCtx = mCtx;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public Image_Home_Adapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_home,parent,false);
        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        view.setOnClickListener(this);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Image_Home_Adapter.ImageViewHolder holder, int position) {
        holder.textView.setText(imageList.get(position).getLocalname().toString());
        if(imageList.get(position).getRouteImg()!=null){
            uploadImgfromWeb(imageList.get(position).getRouteImg(),holder);
        }

    }
    private void uploadImgfromWeb(String routeImg, final ImageViewHolder holder) {
        ConnectionHTTP con = new ConnectionHTTP();
        String ip = con.getIp();
        String url = ip + routeImg;

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

        TextView textView;
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.nametxt);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}