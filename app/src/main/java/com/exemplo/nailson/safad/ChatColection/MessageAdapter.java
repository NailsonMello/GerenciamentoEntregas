package com.exemplo.nailson.safad.ChatColection;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exemplo.nailson.safad.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();


    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mensagemlayout ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout, layout;
        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;


        public MessageViewHolder(View view) {
            super(view);
            layout = (RelativeLayout)view.findViewById(R.id.msgLayout);
            relativeLayout = (RelativeLayout)view.findViewById(R.id.msgCor);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        String userId = mUser.getUid();

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        if (from_user.equals(userId)){
            viewHolder.layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            viewHolder.relativeLayout.setBackgroundResource(R.drawable.messgeu);
            viewHolder.messageText.setTextColor(Color.BLACK);
            viewHolder.displayName.setTextColor(Color.BLACK);
        }else{
            viewHolder.layout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            viewHolder.relativeLayout.setBackgroundResource(R.drawable.mensagemshape);
            viewHolder.messageText.setTextColor(Color.WHITE);
            viewHolder.displayName.setTextColor(Color.WHITE);
        }
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("SAFAD").child("usuario").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("nome").getValue().toString();
                String image = dataSnapshot.child("imagem").getValue().toString();

                viewHolder.displayName.setText(name);

                Picasso.with(viewHolder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewHolder.messageText.setText(c.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }



}
