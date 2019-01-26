package com.example.anna.daily.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.anna.daily.ItemClickListener;
import com.example.anna.daily.R;
import com.example.anna.daily.model.Deal;
import com.example.anna.daily.model.Task;
import java.util.ArrayList;
import java.util.List;
import static com.example.anna.daily.adapter.DealNameRecyclerViewAdapter.dBhelper;
import static com.example.anna.daily.MainActivity.mAdapter;
import static com.example.anna.daily.adapter.DealNameRecyclerViewAdapter.taskCRUD;


public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {


    private Context context;
    private  LinearLayout bottomBlueLinLayout;
    private LinearLayout bottomWhiteLinLayout;

    private Animation slideUpAnimation, slideDownAnimation;
    private EditText dealNameEditText;
    private ImageButton pathButton;
    private ImageButton photoButton;

    private ImageButton editDealButton;
    private ImageButton deleteDealButton;

    private Button addDealBttn;
    private Button saveDealBttn;
    private Button updateDealBttn;
    private Button updateTaskButton;
    private Button addTaskButton;

    private ImageButton editTaskButton;
    private ImageButton deleteTaskButton;

    public static int task_row_index = -1;
    public static int taskDealID = 0;
    private List<Task> taskList;


    public TaskRecyclerViewAdapter(Context mContext, List<Task> taskList) {

        this.context = mContext;
        this.taskList = taskList;
    }


    @NonNull
    @Override
    public TaskRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);

        bottomBlueLinLayout = parent.getRootView().findViewById(R.id.bottomBlueLinLayout);
        bottomWhiteLinLayout = parent.getRootView().findViewById(R.id.bottomWhiteLinLayout);
        dealNameEditText = parent.getRootView().findViewById(R.id.dealNameEditText);
        pathButton = parent.getRootView().findViewById(R.id.pathButton);
        photoButton = parent.getRootView().findViewById(R.id.photoButton);
        addDealBttn = parent.getRootView().findViewById(R.id.addDealButton);
        saveDealBttn = parent.getRootView().findViewById(R.id.saveDealButton);
        editDealButton = parent.getRootView().findViewById(R.id.editButton);
        deleteDealButton = parent.getRootView().findViewById(R.id.deleteButton);
        updateDealBttn = parent.getRootView().findViewById(R.id.updateDealButton);
        updateTaskButton = parent.getRootView().findViewById(R.id.updateTaskButton);
        addTaskButton = parent.getRootView().findViewById(R.id.addTaskButton);
        editTaskButton = parent.getRootView().findViewById(R.id.editTaskButton);
        deleteTaskButton = parent.getRootView().findViewById(R.id.deleteTaskButton);

        slideUpAnimation = AnimationUtils.loadAnimation(context,
                R.anim.slide_up);

        slideDownAnimation = AnimationUtils.loadAnimation(context,
                R.anim.slide_down);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final TaskRecyclerViewAdapter.ViewHolder holder, final int position) {

        holder.onBind(taskList.get(position));
        holder.setIsRecyclable(false);

        //make bold selected task
        if (task_row_index == position) {
            holder.taskName.setTextColor(Color.BLACK);
        }

        //Holder Long click
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onLongClick(View view, int position) {

                task_row_index = position;

                taskDealID = taskList.get(task_row_index).getDeal_id();

                notifyDataSetChanged(); //make effect to reyclerView
                animateBlueLayout(false);

                addDealBttn.setVisibility(View.INVISIBLE);
                addTaskButton.setVisibility(View.INVISIBLE);
                saveDealBttn.setVisibility(View.INVISIBLE);
                editDealButton.setVisibility(View.INVISIBLE);
                deleteDealButton.setVisibility(View.INVISIBLE);
                updateDealBttn.setVisibility(View.INVISIBLE);
                updateTaskButton.setVisibility(View.VISIBLE);
                editTaskButton.setVisibility(View.VISIBLE);
                pathButton.setVisibility(View.GONE);
                photoButton.setVisibility(View.GONE);


                //For updating task
                updateTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        hideKeyboard();
                        addDealBttn.setVisibility(View.VISIBLE);
                        editDealButton.setVisibility(View.VISIBLE);
                        saveDealBttn.setVisibility(View.VISIBLE);
                        addTaskButton.setVisibility(View.INVISIBLE); /////
                        updateTaskButton.setVisibility(View.INVISIBLE);


                        //Getting back old taskName color
                        if (holder.whiteButton.isShown()) {

                            holder.taskName.setTextColor(context.getResources().getColor(R.color.taskItemColor));
                        } else {
                            holder.taskName.setTextColor(context.getResources().getColor(R.color.taskItemDisabledColor));
                        }

                        if (bottomWhiteLinLayout.getVisibility() == View.VISIBLE) {

                            bottomBlueLinLayout.setVisibility(View.GONE);
                            String edittedText = dealNameEditText.getText().toString();
                            holder.taskName.setText(edittedText);

                            updateTask(edittedText, task_row_index, taskList.get(task_row_index).getDisabled());
                        }else{
                            animateBlueLayout(true);
                        }

                        task_row_index = -1;
                        taskCRUD = true;

                        mAdapter.notifyDataSetChanged();
                        notifyDataSetChanged();
                        bottomWhiteLinLayout.setVisibility(View.GONE);
                    }
                });

                editTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        bottomBlueLinLayout.setVisibility(View.GONE);
                        showKeyboard();
                        bottomWhiteLinLayout.setVisibility(View.VISIBLE);
                        addTaskButton.setVisibility(View.INVISIBLE);

                        String taskName = taskList.get(task_row_index).getTaskName();
                        dealNameEditText.setText(taskName);
                        dealNameEditText.setSelection(taskName.length());

                    }
                });

                deleteTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        animateBlueLayout(true);

                        Task task = taskList.get(task_row_index);
                        int deal_id = task.getDeal_id();
                        int tusk_number = task.getTask_number();

                        remove(deal_id, tusk_number);

                        taskCRUD = true;
                        mAdapter.notifyDataSetChanged(); //for notifying dealAdapter to change expandLayout size

                        addDealBttn.setVisibility(View.VISIBLE);
                        editDealButton.setVisibility(View.VISIBLE);
                        saveDealBttn.setVisibility(View.VISIBLE);
                        deleteDealButton.setVisibility(View.VISIBLE);
                        pathButton.setVisibility(View.VISIBLE);
                        photoButton.setVisibility(View.VISIBLE);

                        addTaskButton.setVisibility(View.INVISIBLE);
                        updateTaskButton.setVisibility(View.INVISIBLE);

                    }
                });
            }


            //Handle click on tasks buttons
            @Override
            public void onClick(View view, int position) {

                final Task task = taskList.get(position);

                switch (view.getId()){

                    case R.id.whiteCircleButton:
                    case R.id.whiteBttnLayout:

                        holder.taskName.setTextColor(context.getResources().getColor(R.color.taskItemDisabledColor));
                        holder.whiteBttnLayout.setVisibility(View.GONE);

                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_left);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {

                                remove(task.getDeal_id(),task.getTask_number());

                                task.setDisabled(1);

                                /// insert task
                                if (dBhelper.insertTask(task)) {

                                    taskList = dBhelper.getAllTasksByDealID(task.getDeal_id());
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

                                    //when deal is not expanded,notify for changing task size
                                    Deal deal = dBhelper.findDealByID(task.getDeal_id());
                                    if(deal.getExpanded() == 0){
                                        mAdapter.notifyDataSetChanged();
                                        Log.i("hreshtak", "---mAdapter notified");
                                    }

                                    // update adapter
                                    taskList = newTaskList;

                                    task_row_index = -1;
                                    notifyItemInserted(taskList.size());
                                    notifyDataSetChanged();
                                    dBhelper.closeDB();
                                }
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) { }
                        });

                        holder.itemView.startAnimation(anim);

                        break;


                    case R.id.blueCircleButton:
                    case R.id.blueBttnLayout:


                        holder.taskName.setTextColor(context.getResources().getColor(R.color.taskItemColor));
                        holder.whiteBttnLayout.setVisibility(View.VISIBLE);

                        Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.slide_left);
                        anim2.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {

                                remove(task.getDeal_id(),task.getTask_number());

                                /// insert task
                                task.setDisabled(0);

                                if (dBhelper.insertTask(task)) {

                                    taskList = dBhelper.getAllTasksByDealID(task.getDeal_id());
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

                                    //updateData
                                    taskList = newTaskList;

                                    task_row_index = -1;
                                    notifyItemInserted(0);
                                    notifyDataSetChanged();
                                    dBhelper.closeDB();
                                }
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) { }
                        });

                        holder.itemView.startAnimation(anim2);
                        break;
                }

            }
        });
    }


    public void updateTask(String edittedText, int row_index, int disable) {

        Task task = taskList.get(row_index);
        int deal_id = task.getDeal_id();
        int tusk_number = task.getTask_number();

        if(dBhelper.updateTask(edittedText,deal_id,tusk_number,disable)){

            taskList = dBhelper.getAllTasksByDealID(deal_id);
            dealNameEditText.setText("");
            dBhelper.closeDB();
        }
    }

   //Remove task
   public void remove(int deal_id, int task_number) {

       if( dBhelper.deleteTask(deal_id, task_number)){

           taskList = dBhelper.getAllTasksByDealID(deal_id);
           task_row_index = -1;
           notifyDataSetChanged();

       }else{
           Log.i("hreshtak", "not removed");
       }
           dBhelper.closeDB();
   }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void hideKeyboard(){

        InputMethodManager imm2 = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm2 != null) {
            imm2.hideSoftInputFromWindow(dealNameEditText.getWindowToken(), 0);
        }
    }

    public void showKeyboard(){

        dealNameEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(dealNameEditText, InputMethodManager.SHOW_IMPLICIT);
        }
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



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        LinearLayout taskLayout;
        TextView taskName;
        RelativeLayout whiteBttnLayout;
        RelativeLayout blueBttnLayout;
        ImageButton whiteButton;
        ImageButton blueButton;
        ItemClickListener itemClickListener;


        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        public ViewHolder(View itemView) {
            super(itemView);

            taskLayout = itemView.findViewById(R.id.taskLayout);
            whiteButton = itemView.findViewById(R.id.whiteCircleButton);
            whiteBttnLayout = itemView.findViewById(R.id.whiteBttnLayout);
            blueBttnLayout = itemView.findViewById(R.id.blueBttnLayout);
            blueButton = itemView.findViewById(R.id.blueCircleButton);
            taskName = itemView.findViewById(R.id.taskNameTV);

            whiteBttnLayout.setOnClickListener(this);
            blueBttnLayout.setOnClickListener(this);
            whiteButton.setOnClickListener(this);
            blueButton.setOnClickListener(this);
            taskLayout.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public void onBind(Task task) {

            if(task.getDisabled() == 1){

                taskName.setTextColor(whiteButton.getContext().getResources().getColor(R.color.taskItemDisabledColor));
                whiteBttnLayout.setVisibility(View.GONE);

            }else{
                taskName.setTextColor(whiteButton.getContext().getResources().getColor(R.color.taskItemColor));
                whiteBttnLayout.setVisibility(View.VISIBLE);

            }
             taskName.setText(task.getTaskName());
        }

        @Override
        public void onClick(View v) {

            itemClickListener.onClick(v,getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {

            itemClickListener.onLongClick(v,getAdapterPosition());
            return true;
        }
    }
}
