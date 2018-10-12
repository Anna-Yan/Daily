package com.example.anna.daily.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anna.daily.DBhelper;
import com.example.anna.daily.ItemLongClickListener;
import com.example.anna.daily.R;
import com.example.anna.daily.model.Deal;
import com.example.anna.daily.model.Task;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.RECEIVER_VISIBLE_TO_INSTANT_APPS;
import static com.example.anna.daily.MainActivity.base64Image;
import static com.example.anna.daily.MainActivity.isInserting;

public class DealNameRecyclerViewAdapter extends RecyclerView.Adapter<DealNameRecyclerViewAdapter.ViewHolder> {


    private List<Deal> dealList;
    private Context context;

    private RecyclerView dealRecyclerView;
    private static LinearLayout bottomBlueLinLayout;
    private LinearLayout bottomWhiteLinLayout;

    private EditText dealNameEditText;
    private ImageButton pathButton;
    private ImageButton photoButton;

    private Button addDealBttn;
    private Button saveDealBttn;
    private Button updateDealBttn;
    private Button addTaskBttn;

    private ImageButton editButton;
    private ImageButton plusButton;
    private ImageButton deleteButton;

    private static int row_index = -1;
    public static DBhelper dBhelper;

    public DealNameRecyclerViewAdapter (Context mContext, List<Deal> dealList) {

        this.context = mContext;
        this.dealList = dealList;
        this.dBhelper = new DBhelper(context);

    }


    @NonNull
    @Override
    public DealNameRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deal_name_item, parent, false);
        bottomBlueLinLayout = parent.getRootView().findViewById(R.id.bottomBlueLinLayout);
        bottomWhiteLinLayout = parent.getRootView().findViewById(R.id.bottomWhiteLinLayout);
        dealNameEditText = parent.getRootView().findViewById(R.id.dealNameEditText);
        pathButton = parent.getRootView().findViewById(R.id.pathButton);
        photoButton = parent.getRootView().findViewById(R.id.photoButton);
        addDealBttn = parent.getRootView().findViewById(R.id.addDealButton);
        saveDealBttn = parent.getRootView().findViewById(R.id.saveDealButton);
        updateDealBttn = parent.getRootView().findViewById(R.id.updateDealButton);
        addTaskBttn = parent.getRootView().findViewById(R.id.addTaskButton);
        editButton = parent.getRootView().findViewById(R.id.editButton);
        plusButton = parent.getRootView().findViewById(R.id.plusButton);
        deleteButton = parent.getRootView().findViewById(R.id.deleteButton);
        dealRecyclerView = parent.getRootView().findViewById(R.id.dealRecyclerView);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DealNameRecyclerViewAdapter.ViewHolder holder, final int position) {

        holder.onBind(dealList.get(position),context);

        holder.setIsRecyclable(false);

        //always expand first task
        ViewGroup.LayoutParams params = holder.dealExpandableLayout.getLayoutParams();
        params.height = context.getResources().getDimensionPixelSize(R.dimen.task_item_size);
        holder.dealExpandableLayout.setLayoutParams(params);
        holder.dealExpandableLayout.expand();


        holder.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public void onLongClick(View view, int position) {

                row_index = position;
                notifyDataSetChanged(); //make effect to reyclerView

                //open blue layout, and make visible update button by hiding add and save buttons
                animateBlueLayout(false);
                addDealBttn.setVisibility(View.INVISIBLE);
                saveDealBttn.setVisibility(View.INVISIBLE);
            }
        });

        //make bold selected deal
        if(row_index == position){

            holder.dealName.setTypeface(ResourcesCompat.getFont(context, R.font.verdanab));
        }
        else{
            holder.dealName.setTextAppearance(context, R.style.normalDealStyle);
            //holder.dealExpandableLayout.collapse();
        }


         //For showing selected DealName in editText
         editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInserting = false;

                animateBlueLayout(true);
                bottomWhiteLinLayout.setVisibility(View.VISIBLE);
                showKeyboard();


                String dealText = dealList.get(row_index).getName();

                dealNameEditText.setText(dealText);
                dealNameEditText.setSelection(dealText.length());
                dealNameEditText.setHint("Deal name");


            }
        });


        //For opening White Layout for adding taskName
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animateBlueLayout(true);
                showKeyboard();

                //This is for making visible addTaskButton
                pathButton.setVisibility(View.INVISIBLE);
                photoButton.setVisibility(View.INVISIBLE);
                updateDealBttn.setVisibility(View.INVISIBLE);
                bottomWhiteLinLayout.setVisibility(View.VISIBLE);

                dealNameEditText.setHint("Task name");
            }
        });

        //For deleting deal
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteDeal(row_index);

                row_index = -1; //uncheck bold check
                notifyDataSetChanged();

                animateBlueLayout(true);
                addDealBttn.setVisibility(View.VISIBLE);
                saveDealBttn.setVisibility(View.VISIBLE);

            }
        });


        //For updating deal
        updateDealBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                animateBlueLayout(true);
                hideKeyboard();

                String edittedText;
                //For checking if White Layout is opened or not, if not then do nothing
                if (bottomWhiteLinLayout.getVisibility() == View.VISIBLE) {

                    edittedText = dealNameEditText.getText().toString();

                    updateDeal(row_index, edittedText);
                }

                row_index = -1; //uncheck bold
                notifyDataSetChanged();

                addDealBttn.setVisibility(View.VISIBLE);
                saveDealBttn.setVisibility(View.VISIBLE);
                bottomWhiteLinLayout.setVisibility(View.GONE);

            }
        });


        //For adding new Task
        addTaskBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animateBlueLayout(true);

               // if (!dealNameEditText.getText().toString().matches("")) {

                    holder.bindTask(row_index, dealNameEditText.getText().toString());

                    //expand task layout to the height of taskItem size
                    // holder.expandTaskLayout(true,context);

                    hideKeyboard();
                    dealNameEditText.setText("");
                    addDealBttn.setVisibility(View.VISIBLE);
                    saveDealBttn.setVisibility(View.VISIBLE);
                    updateDealBttn.setVisibility(View.VISIBLE);
                    bottomWhiteLinLayout.setVisibility(View.GONE);
                    pathButton.setVisibility(View.VISIBLE);
                    photoButton.setVisibility(View.VISIBLE);

                    //For deselect bold text
                    row_index = -1;
                    notifyDataSetChanged();


                // } else {
               //     Toast.makeText(context, "Please fill the task field", Toast.LENGTH_SHORT).show();
               // }
            }
        });

    }


    public void hideKeyboard(){

        InputMethodManager imm2 = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm2.hideSoftInputFromWindow(dealNameEditText.getWindowToken(), 0);


    }

    public void showKeyboard(){

        dealNameEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(dealNameEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private static Bitmap base64ToImage(String imageString) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes = baos.toByteArray();
        imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;

    }

    private static void animateBlueLayout(boolean hide){

        if(hide){

            bottomBlueLinLayout.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);

                            bottomBlueLinLayout.setVisibility(View.GONE);
                        }
                    });
        }
        else{

            bottomBlueLinLayout.animate()
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);

                            bottomBlueLinLayout.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    public void animateBounce(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.bounce_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    @Override
    public int getItemCount() {
        return dealList.size();
    }

    // petq chi
    public void insert(int position, Deal deal) {
        dealList.add(position, deal);
        notifyItemInserted(position);
    }

    public void deleteDeal(int position) {

        Deal deal = dealList.get(position);
        int id = deal.getId();

        if( dBhelper.deleteDeal(id)){

            dealList = dBhelper.getAllDeals();
            Collections.reverse(dealList);

            notifyItemRemoved(position);
            dBhelper.closeDB();
        }
        else
            Toast.makeText(context, "Can not delete deal", Toast.LENGTH_SHORT).show();
    }


    public void updateDeal(int position, String edittedText) {

        Deal deal = dealList.get(position);
        int id = deal.getId();

        if(dBhelper.updateDeal(id, edittedText, base64Image)){

            dealNameEditText.setText("");

            if(!base64Image.matches("")){
                base64Image = "";
            }

            dealList = dBhelper.getAllDeals();
            Collections.reverse(dealList);

            dBhelper.closeDB();
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener, View.OnLongClickListener {

        List<Task> taskList;
        ImageView imageView;
        TextView dealName;
        ExpandableLayout dealExpandableLayout;

        //set up recycler view
        RecyclerView taskRecyclerView;
        RecyclerView.LayoutManager taskVerticallinLayoutManager;
        RecyclerView.LayoutManager taskHorizontalLinLayoutManager;
        TaskRecyclerViewAdapter taskAdapter;

        ItemLongClickListener itemLongClickListener;
        boolean isExpanded;


        public void setItemLongClickListener(ItemLongClickListener itemLongClickListener){
            this.itemLongClickListener = itemLongClickListener;
        }

        public ViewHolder(View itemView) {
            super(itemView);


            imageView = itemView.findViewById(R.id.imageView);
            dealName = itemView.findViewById(R.id.dealNameTV);
            taskRecyclerView = itemView.findViewById(R.id.taskRecyclerView);
            dealExpandableLayout = itemView.findViewById(R.id.dealExpLayout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);


            taskVerticallinLayoutManager = new LinearLayoutManager(bottomBlueLinLayout.getContext(), LinearLayoutManager.VERTICAL,false);
            taskHorizontalLinLayoutManager = new LinearLayoutManager(bottomBlueLinLayout.getContext(), LinearLayoutManager.HORIZONTAL, false);
            taskRecyclerView.setLayoutManager(taskVerticallinLayoutManager);

            //set Animation to recyclerview item
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(1000);
            itemAnimator.setRemoveDuration(1000);
            taskRecyclerView.setItemAnimator(itemAnimator);


            }

        public void onBind(Deal deal, Context context) {

            dealName.setText(deal.getName());
            int deal_id = deal.getId();

            if(!deal.getImagePath().matches("")){
                imageView.setImageBitmap(base64ToImage(deal.getImagePath()));
           }

            taskAdapter = new TaskRecyclerViewAdapter(context,dBhelper.getAllTasksByDealID(deal_id));
            taskRecyclerView.setAdapter(taskAdapter);

            Log.i("hreshtak","onDealRecView onBind, pos="+getAdapterPosition()+", all tasks "+dBhelper.getAllTasksByDealID(deal.getId()));
        }

        public void bindTask(int row_index, String taskName) {

            //Check which recyclerView adapter will insert task(row_index=longClickedDealPosition)
           //  if(dBhelper.getTasksCountByDealId(row_index) < 3 ) {

                //Get deal_id at row_index
                List<Deal> reversedList = dBhelper.getAllDeals();
                Collections.reverse(reversedList);

                Deal deal = reversedList.get(row_index);     //dBhelper.getAllDeals().get(row_index);
                int deal_id = deal.getId();

                Task task = new Task();
                task.setDeal_id(deal_id);
                task.setTaskName(taskName);
                task.setTask_number(dBhelper.getTasksCountByDealId(deal_id));

                if (dBhelper.insertTask(task)) {

                    //updateData
                    taskAdapter = new TaskRecyclerViewAdapter(itemView.getContext(), dBhelper.getAllTasksByDealID(deal.getId()));
                    taskRecyclerView.setAdapter(taskAdapter);

                    taskAdapter.notifyItemInserted(dBhelper.getAllTasksByDealID(deal.getId()).size());
                    taskAdapter.notifyDataSetChanged();

                   // Toast.makeText(itemView.getContext(), "Task added, dealName =" + deal.getName() + ", task count=" + dBhelper.getTasksCountByDealId(deal_id), Toast.LENGTH_SHORT).show();
                    Log.i("hreshtak", "success:bindTask,task count at " + row_index + "=" + dBhelper.getTasksCountByDealId(deal_id));


                    //Expand all tasks
                    ViewGroup.LayoutParams params = dealExpandableLayout.getLayoutParams();
                    params.height = dBhelper.getAllTasksByDealID(deal_id).size()*(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.task_item_size));
                    dealExpandableLayout.setLayoutParams(params);
                    dealExpandableLayout.expand();
                }

        }


        public void expandTaskLayout(boolean expand, Context context){

            if(expand){

                isExpanded = true;
                ViewGroup.LayoutParams params = dealExpandableLayout.getLayoutParams();
                params.height = taskList.size()*(context.getResources().getDimensionPixelSize(R.dimen.task_item_size));
                dealExpandableLayout.setLayoutParams(params);

               // dealExpandableLayout.move(taskList.size()*(context.getResources().getDimensionPixelSize(R.dimen.task_item_size)));
                dealExpandableLayout.expand();
            }
            else{

                isExpanded = false;
                ViewGroup.LayoutParams params = dealExpandableLayout.getLayoutParams();
                params.height = bottomBlueLinLayout.getContext().getResources().getDimensionPixelSize(R.dimen.task_item_size);
                dealExpandableLayout.setLayoutParams(params);

               // dealExpandableLayout.move(bottomBlueLinLayout.getContext().getResources().getDimensionPixelSize(R.dimen.task_item_size));
                dealExpandableLayout.collapse();
            }
        }

        @Override
        public void onClick(View v) {


            //Get deal_id at row_index
            List<Deal> reversedList = dBhelper.getAllDeals();
            Collections.reverse(reversedList);

            Deal deal = reversedList.get(getAdapterPosition());     //dBhelper.getAllDeals().get(row_index);
            int deal_id = deal.getId();



            if (dealExpandableLayout.getLayoutParams().height == itemView.getContext().getResources().getDimensionPixelSize(R.dimen.task_item_size)) {

                // holder.expandTaskLayout(false, context);

                //Expand all tasks
                ViewGroup.LayoutParams params = dealExpandableLayout.getLayoutParams();
                params.height = dBhelper.getAllTasksByDealID(deal_id).size() * (itemView.getContext().getResources().getDimensionPixelSize(R.dimen.task_item_size));
                dealExpandableLayout.setLayoutParams(params);
                dealExpandableLayout.expand();

            }
            else{
                //Always show 1 task
                ViewGroup.LayoutParams params = dealExpandableLayout.getLayoutParams();
                params.height = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.task_item_size);
                dealExpandableLayout.setLayoutParams(params);
                dealExpandableLayout.expand();

                // holder.expandTaskLayout(true, context);
            }


        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            Log.d("ExpandableLayout", "State: " + state);

            if (state == ExpandableLayout.State.EXPANDING) {
                taskRecyclerView.smoothScrollToPosition(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {

            itemLongClickListener.onLongClick(view,getAdapterPosition());
            return true;
        }
    }
}
