package com.example.anna.daily.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.anna.daily.ItemClickListener;
import com.example.anna.daily.R;
import com.example.anna.daily.model.Deal;
import com.example.anna.daily.model.Task;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.example.anna.daily.MainActivity.base64Image;
import static com.example.anna.daily.MainActivity.isInserting;
import static com.example.anna.daily.adapter.TaskRecyclerViewAdapter.taskDealID;
import static com.example.anna.daily.adapter.TaskRecyclerViewAdapter.task_row_index;
public class DealNameRecyclerViewAdapter extends RecyclerView.Adapter<DealNameRecyclerViewAdapter.ViewHolder> {


    private List<Deal> dealList;
    private Context context;

    private RecyclerView dealRecyclerView;
    private  LinearLayout bottomBlueLinLayout;
    private LinearLayout bottomWhiteLinLayout;

    private Animation slideUpAnimation, slideDownAnimation;

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

    public static int deal_row_index = -5;
    public static DBhelper dBhelper;
    public static boolean taskCRUD = false;



    public DealNameRecyclerViewAdapter (Context mContext, List<Deal> dealList) {

        this.context = mContext;
        this.dealList = dealList;
        dBhelper = new DBhelper(context);
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

        slideUpAnimation = AnimationUtils.loadAnimation(context,
                R.anim.slide_up);

        slideDownAnimation = AnimationUtils.loadAnimation(context,
                R.anim.slide_down);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DealNameRecyclerViewAdapter.ViewHolder holder, final int position) {

        holder.onBind(dealList.get(position),context);

        holder.setIsRecyclable(false);

        //Always close all tasks and show 1 task when entered first time
        if(deal_row_index == -5) {

            holder.dealName.setTextAppearance(context, R.style.normalDealStyle);
            if (dBhelper.getTasksCountByDealId(dealList.get(position).getId()) > 1) {
                holder.expandTaskLayout(position, false);
                Log.i("hreshtak", "close Task layout =" + position);

            }
        }
        //Always show 1 task
        if(deal_row_index != position) {

            List<Deal> reversedList = dBhelper.getAllDeals();
            Collections.reverse(reversedList);
            Deal deal = reversedList.get(position);
            int tasksCount = dBhelper.getTasksCountByDealId(dealList.get(position).getId());

            holder.dealName.setTextAppearance(context, R.style.normalDealStyle);

            //if tasks are opened
            if(deal.getExpanded() == 1){
                if (tasksCount > 1) {
                    holder.expandTaskLayout(position, true);
                }
            //if tasks are closed
            }else if(tasksCount > 1){
                holder.expandTaskLayout(position, false);
            }

     }else if (deal_row_index == position){

            List<Deal> reversedList = dBhelper.getAllDeals();
            Collections.reverse(reversedList);
            Deal deal = reversedList.get(position);
            int tasksCount = dBhelper.getTasksCountByDealId(dealList.get(position).getId());

            if(taskCRUD){
                holder.dealName.setTextAppearance(context, R.style.normalDealStyle);
            }else
            holder.dealName.setTypeface(ResourcesCompat.getFont(context, R.font.verdanab));

            //if tasks are opened
            if(deal.getExpanded() == 1){
                if (tasksCount > 1) {
                    holder.expandTaskLayout(position, true);
                }
            //if tasks are closed
            }else if(tasksCount > 1){
                holder.expandTaskLayout(position, false);
            }
        }


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onLongClick(View view, int position) {

                holder.dealName.setTypeface(ResourcesCompat.getFont(context, R.font.verdanab));

                deal_row_index = position;
                taskCRUD = false;

                notifyDataSetChanged(); //make effect to reyclerView

                //open blue layout, and make visible update button by hiding add and save buttons
                animateBlueLayout(false);
                addDealBttn.setVisibility(View.INVISIBLE);
                saveDealBttn.setVisibility(View.INVISIBLE);
                updateDealBttn.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                pathButton.setVisibility(View.VISIBLE);
                photoButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClick(View view, int position) {

                List<Deal> reversedList = dBhelper.getAllDeals();
                Collections.reverse(reversedList);

                Deal deal = reversedList.get(position);
                int deal_id = deal.getId();
                int tasksCount = dBhelper.getTasksCountByDealId(dealList.get(position).getId());


                if(tasksCount > 1){

                if (deal.getExpanded() == 1) {
                    //Always show 1 task

                    holder.expandTaskLayout(position,false);
                    dBhelper.changeDealExpanded(deal_id,0);
                }
                else {
                    //Expand all tasks

                    holder.expandTaskLayout(position,true);
                    dBhelper.changeDealExpanded(deal_id,1);
                }
              }
            }
        });


         //For showing selected DealName in editText
         editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInserting = false;

                bottomBlueLinLayout.setVisibility(View.GONE);
                bottomWhiteLinLayout.setVisibility(View.VISIBLE);
                showKeyboard();

                String dealText = dealList.get(deal_row_index).getName();

                dealNameEditText.setText(dealText);
                dealNameEditText.setSelection(dealText.length());

            }
        });


        //For opening White Layout for adding taskName
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomBlueLinLayout.setVisibility(View.GONE);
                showKeyboard();

                //This is for making visible addTaskButton
                pathButton.setVisibility(View.GONE);
                photoButton.setVisibility(View.GONE);
                updateDealBttn.setVisibility(View.INVISIBLE);
                addTaskBttn.setVisibility(View.VISIBLE);
                bottomWhiteLinLayout.setVisibility(View.VISIBLE);

                dealNameEditText.setHint("Add task");
            }
        });

        //For deleting deal
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteDeal(deal_row_index);

                deal_row_index = -1; //uncheck bold check
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

                hideKeyboard();

                String edittedText;

                //For checking if White Layout is opened or not, if not then do nothing
                if (bottomWhiteLinLayout.getVisibility() == View.VISIBLE) {

                    bottomBlueLinLayout.setVisibility(View.GONE);
                    edittedText = dealNameEditText.getText().toString();
                    updateDeal(deal_row_index, edittedText);

                }else{
                    animateBlueLayout(true);
                }

                deal_row_index = -1; //uncheck bold
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

                    hideKeyboard();

                    //Get deal_id at row_index
                    List<Deal> reversedList = dBhelper.getAllDeals();
                    Collections.reverse(reversedList);

                    if(deal_row_index >= 0) {

                        Deal deal = reversedList.get(deal_row_index);
                        int deal_id = deal.getId();

                        holder.bindTask(deal_id, dealNameEditText.getText().toString());

                        //make expanded true
                        dBhelper.changeDealExpanded(deal_id,1);
                        Log.i("stugum", "deal_row_index >= 0");

                    }else{

                        holder.bindTask(taskDealID, dealNameEditText.getText().toString());
                        Log.i("stugum", "deal_row_index < 0");

                        //make expanded true
                        dBhelper.changeDealExpanded(taskDealID,1);
                    }

                    dealNameEditText.setText("");
                    addDealBttn.setVisibility(View.VISIBLE);
                    saveDealBttn.setVisibility(View.VISIBLE);
                    updateDealBttn.setVisibility(View.VISIBLE);

                    bottomWhiteLinLayout.setVisibility(View.GONE);
                    pathButton.setVisibility(View.VISIBLE);
                    photoButton.setVisibility(View.VISIBLE);

                    //For deselect bold text
                    notifyDataSetChanged();
                    task_row_index = -1;
                    taskCRUD = true;
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

    private void animateBlueLayout(boolean hide){

        if(hide){

            bottomBlueLinLayout.startAnimation(slideDownAnimation);
            bottomBlueLinLayout.setVisibility(View.GONE);
        }
        else{

            bottomBlueLinLayout.startAnimation(slideUpAnimation);
            bottomBlueLinLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dealList.size();
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
            Log.i("hreshtak","Can not delete deal");
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


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView dealName;
        ExpandableLinearLayout dealExpandableLayout;

        //set up recycler view
        RecyclerView taskRecyclerView;
        RecyclerView.LayoutManager taskVerticallinLayoutManager;
        RecyclerView.LayoutManager taskHorizontalLinLayoutManager;
        public static TaskRecyclerViewAdapter taskAdapter;

        List<Task> taskList;
        List<Task> newTaskList;

        ItemClickListener itemClickListener;


        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            dealName = itemView.findViewById(R.id.dealNameTV);
            taskRecyclerView = itemView.findViewById(R.id.taskRecyclerView);
            dealExpandableLayout = itemView.findViewById(R.id.dealExpLayout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            taskVerticallinLayoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL,false);
            taskHorizontalLinLayoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            taskRecyclerView.setLayoutManager(taskVerticallinLayoutManager);

            //set Animation to recyclerview item
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(7000);
            itemAnimator.setRemoveDuration(3000);
            taskRecyclerView.setItemAnimator(itemAnimator);


            }

        public void onBind(Deal deal, Context context) {

            dealName.setText(deal.getName());
            int deal_id = deal.getId();

            if(!deal.getImagePath().matches("")){
                imageView.setImageBitmap(base64ToImage(deal.getImagePath()));
           }

            taskList = dBhelper.getAllTasksByDealID(deal.getId());
            newTaskList = new ArrayList<>();

            for (int i = 0; i < taskList.size(); i++) {

                if(taskList.get(i).getDisabled() == 0){

                    newTaskList.add(taskList.get(i));
                }
            }

            for (int i = 0; i < taskList.size(); i++) {
                if (taskList.get(i).getDisabled() == 1) {

                    newTaskList.add(taskList.get(i));
                }
            }

            taskAdapter = new TaskRecyclerViewAdapter(context,newTaskList);
            taskRecyclerView.setAdapter(taskAdapter);


        }

        public void bindTask(int deal_id, String taskName) {

                //Check which recyclerView adapter will insert task
                List<Task> taskList = dBhelper.getAllTasksByDealID(deal_id);

                Task task = new Task();
                task.setDeal_id(deal_id);
                task.setTaskName(taskName);

                if(taskList.size()>0 ){
                    task.setTask_number(taskList.get(taskList.size()-1).getTask_number()+1);
                }else{
                    task.setTask_number(dBhelper.getTasksCountByDealId(deal_id));
                }
                task.setDisabled(0);


                if (dBhelper.insertTask(task)) {

                    //updateData
                    taskList = dBhelper.getAllTasksByDealID(deal_id);
                    taskAdapter = new TaskRecyclerViewAdapter(itemView.getContext(), taskList);
                    taskRecyclerView.setAdapter(taskAdapter);

                    taskAdapter.notifyItemInserted(0);
                    taskAdapter.notifyDataSetChanged();

                    Log.i("hreshtak", "success:bindTask");
                }
        }


        public void expandTaskLayout(int position, boolean expand){

            int height = 0;
            List<Deal> reversedList = dBhelper.getAllDeals();
            Collections.reverse(reversedList);
            Deal deal = reversedList.get(position);
            int deal_id = deal.getId();

            List<Task> taskList = dBhelper.getAllTasksByDealID(deal.getId());
            List<Task> newTaskList = new ArrayList<>();

            for (int i = 0; i < taskList.size(); i++) {

                if(taskList.get(i).getDisabled() == 0){

                    newTaskList.add(taskList.get(i));
                }
            }

            for (int i = 0; i < taskList.size(); i++) {
                if (taskList.get(i).getDisabled() == 1) {

                    newTaskList.add(taskList.get(i));
                }
            }

            if(expand){

                dBhelper.changeDealExpanded(deal_id,1);

                ViewGroup.LayoutParams params = dealExpandableLayout.getLayoutParams();

                  for (int i = 0; i < taskList.size(); i++) {

                    int size = newTaskList.get(i).getTaskName().length() / 30;
                    if ( newTaskList.get(i).getTaskName().length()>30) {
                        height += (itemView.getContext().getResources().getDimensionPixelSize(R.dimen.task_change_size)*size);
                    }
              }

                params.height = newTaskList.size() * itemView.getContext().getResources().getDimensionPixelSize(R.dimen.task_item_size) + height;


                dealExpandableLayout.setLayoutParams(params);

                dealExpandableLayout.move(params.height);
            }
            else{

                dBhelper.changeDealExpanded(deal_id,0);

                ViewGroup.LayoutParams params = dealExpandableLayout.getLayoutParams();
                params.height = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.task_item_size);

                if(taskList.size() != 0){

                 int size = newTaskList.get(0).getTaskName().length() / 30;
                    if ( newTaskList.get(0).getTaskName().length()>30) {
                        params.height = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.task_item_size) + (itemView.getContext().getResources().getDimensionPixelSize(R.dimen.task_change_size)*size);

                    }
                }

                dealExpandableLayout.setLayoutParams(params);
                dealExpandableLayout.move(params.height);
            }
        }


     @Override
        public void onClick(View view) {

            itemClickListener.onClick(view,getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {

            itemClickListener.onLongClick(view,getAdapterPosition());
            return true;
        }


    }
}
