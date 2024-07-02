package com.example.cnpm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cnpm.R;
import com.example.cnpm.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {
    Context context;
    private List<HomeModel> list;

    OnPressed onPressed;
    public HomeAdapter(List<HomeModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent, false);
        return new HomeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        holder.userNameTv.setText(list.get(position).getUserName());
        holder.timeTv.setText(""+list.get(position).getTimestamp());
        List<String> likeList = list.get(position).getLikes();

        int count = likeList.size();

        if (count ==0){
            holder.likeCountTv.setText("0 Like");
        } else if (count==1) {
            holder.likeCountTv.setText(count + " Like");
        } else {
            holder.likeCountTv.setText(count + " Likes");
        }
        if(likeList.contains(user.getUid())){
            holder.likeCheckBox.setChecked(true);
        }
        else {
            holder.likeCheckBox.setChecked(false);
        }
        holder.descriptionTv.setText(list.get(position).getDescription());

        Random random = new Random();

        int color = Color.argb(255,random.nextInt(256),random.nextInt(256),random.nextInt(256));

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getProfileImage())
                .placeholder(R.drawable.ic_person)
                .timeout(6500)
                .into(holder.profileImage);

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getImageUrl())
                .placeholder(new ColorDrawable(color))
                .timeout(7000)
                .into(holder.imageView);
        holder.clickListener(position,
                list.get(position).getId(),
                list.get(position).getUserName(),
                list.get(position).getUid(),
                list.get(position).getLikes()
        );
    }
    public  interface  OnPressed{
        void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked);
        void onComment(int position, String id, String comment);
    }
    public  void  OnPressed(OnPressed onPressed){
        this.onPressed=onPressed;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    class HomeHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImage;
        private TextView userNameTv,timeTv, likeCountTv,descriptionTv;
        private ImageView imageView;
        private ImageButton commentBtn,shareBtn;
        private CheckBox likeCheckBox;
        public HomeHolder(View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            imageView = itemView.findViewById(R.id.imageview);
            userNameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            likeCountTv = itemView.findViewById(R.id.likeCountTv);
            likeCheckBox = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            descriptionTv = itemView.findViewById(R.id.descTv);

        }

        public void clickListener(int position, String id, String userName, String uid, List<String> likes) {
            likeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onPressed.onLiked(position,id,uid,likes, isChecked);
                }
            });
        }
    }

}
