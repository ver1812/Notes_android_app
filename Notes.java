package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Notes extends AppCompatActivity {
    FloatingActionButton mcreatenotesfab;
    private FirebaseAuth firebaseAuth;

    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter <firebasemodel,NoteViewHolder> noteAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);


        mcreatenotesfab = findViewById(R.id.createnotesfab);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview = findViewById(R.id.recyclerview);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);



        getSupportActionBar().setTitle("All Notes");

        mcreatenotesfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Notes.this,createnote.class));

            }
        });


        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("mynotes").orderBy("title",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<firebasemodel> allusernotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull firebasemodel model) {

                ImageView popupbutton = holder.itemView.findViewById(R.id.menupopupbutton);

                int colourcode = getRandomColor();
                holder.mnote.setBackgroundColor(holder.itemView.getResources().getColor(colourcode,null));

                holder.notetitle.setText(model.getTitle());
                holder.notecontent.setText(model.getContent());

                String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //we have to open detail
                        Intent intent = new Intent(view.getContext(),notedetails.class);
                        intent.putExtra("title",model.getTitle());
                        intent.putExtra("content",model.getContent());
                        intent.putExtra("noteId",docId);

                        view.getContext().startActivity(intent);

                    }
                });

                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                                Intent intent = new Intent(view.getContext(),editnoteactivity.class);
                                intent.putExtra("title",model.getTitle());
                                intent.putExtra("content",model.getContent());
                                intent.putExtra("noteId",docId);
                                view.getContext().startActivity(intent);

                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                                //Toast.makeText(view.getContext(),"Note deleted successfully",Toast.LENGTH_SHORT).show();

                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("mynotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Notes.this,"Note deleted successfully",Toast.LENGTH_SHORT).show();


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Notes.this,"Failed to delete ",Toast.LENGTH_SHORT).show();

                                    }
                                });


                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Copy to clipboard").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                                // Copy note content to clipboard
                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clipData = ClipData.newPlainText("Note Content", model.getContent());
                                clipboardManager.setPrimaryClip(clipData);

                                // Show toast to indicate that the note content has been copied to clipboard
                                Toast.makeText(Notes.this, "Note content copied to clipboard", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                String shareBody = model.getTitle() + "\n\n" + model.getContent();
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                view.getContext().startActivity(Intent.createChooser(shareIntent, "Share note via"));
                                return false;
                            }
                        });





                        popupMenu.show();

                    }
                });


            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        mrecyclerview = findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);


    }

    private int getRandomColor() {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.blue_200);
        colorcode.add(R.color.blue_200);
        colorcode.add(R.color.blue_300);
        colorcode.add(R.color.blue_500);
        colorcode.add(R.color.blue_700);
        colorcode.add(R.color.blue_900);

        colorcode.add(R.color.green_300);
        colorcode.add(R.color.green_500);
        colorcode.add(R.color.green_700);
        colorcode.add(R.color.green_900);
        colorcode.add(R.color.purple_200);
        colorcode.add(R.color.purple_500);
        colorcode.add(R.color.purple_700);
        colorcode.add(R.color.teal_200);
        colorcode.add(R.color.teal_700);

        colorcode.add(R.color.white);

        colorcode.add(R.color.yellow_300);
        colorcode.add(R.color.yellow_400);
        colorcode.add(R.color.yellow_500);
        colorcode.add(R.color.yellow_600);
        colorcode.add(R.color.yellow_700);
        colorcode.add(R.color.yellow_800);
        colorcode.add(R.color.yellow_900);

        colorcode.add(R.color.green_300);
        colorcode.add(R.color.green_400);
        colorcode.add(R.color.green_500);
        colorcode.add(R.color.green_600);
        colorcode.add(R.color.green_700);
        colorcode.add(R.color.green_800);
        colorcode.add(R.color.green_900);

        colorcode.add(R.color.blue_200);
        colorcode.add(R.color.blue_300);
        colorcode.add(R.color.blue_400);
        colorcode.add(R.color.blue_500);
        colorcode.add(R.color.blue_600);
        colorcode.add(R.color.blue_700);
        colorcode.add(R.color.blue_800);
        colorcode.add(R.color.blue_900);

        colorcode.add(R.color.red_200);
        colorcode.add(R.color.red_300);
        colorcode.add(R.color.red_400);
        colorcode.add(R.color.red_500);
        colorcode.add(R.color.red_600);
        colorcode.add(R.color.red_700);
        colorcode.add(R.color.red_800);
        colorcode.add(R.color.red_900);

        Random random = new Random();
        int position = random.nextInt(colorcode.size());
        return colorcode.get(position);
    }


    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView notetitle;
        private TextView notecontent;
        LinearLayout mnote;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle = itemView.findViewById(R.id.notetitle);
            notecontent = itemView.findViewById(R.id.notecontent);
            mnote = itemView.findViewById(R.id.note);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(Notes.this,SignIn.class));




        }




        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!= null)
        {
            noteAdapter.stopListening();
        }

    }


}