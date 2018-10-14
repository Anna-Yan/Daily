package com.example.anna.daily.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.TextView;
import android.widget.Toast;
import com.example.anna.daily.ItemClickListener;
import com.example.anna.daily.R;
import com.example.anna.daily.model.Deal;
import com.example.anna.daily.model.Task;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import java.util.List;
import static com.example.anna.daily.adapter.DealNameRecyclerViewAdapter.dBhelper;
import static com.example.anna.daily.MainActivity.mAdapter;
import static com.example.anna.daily.adapter.DealNameRecyclerViewAdapter.deal_row_index;


public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {


    private Context context;
    private static LinearLayout bottomBlueLinLayout;
    private LinearLayout bottomWhiteLinLayout;
    private ExpandableLinearLayout dealExpandableLayout;

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
        dealExpandableLayout = parent.getRootView().findViewById(R.id.dealExpLayout);
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

                List<Deal> list = dBhelper.getAllDeals();

                int dealPosition = (list.get(list.size()-1).getId())-taskList.get(task_row_index).getDeal_id();

                deal_row_index = dealPosition;

                Log.i("hreshtak", "task row_index="+task_row_index);

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

                        animateBlueLayout(true);
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

                            String edittedText = dealNameEditText.getText().toString();
                            holder.taskName.setText(edittedText);

                            updateTask(edittedText, task_row_index, taskList.get(task_row_index).getDisabled());

                        }
                        task_row_index = -1;
                        deal_row_index = -1;
                        notifyDataSetChanged();
                        bottomWhiteLinLayout.setVisibility(View.GONE);
                    }
                });

                editTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        animateBlueLayout(true);
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

                        task_row_index = -1;
                        notifyDataSetChanged();

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



            @Override
            public void onClick(View view, int position) {

                final Task task = taskList.get(position);

                if (view.getId() == R.id.whiteCircleButton ) {

                    holder.taskName.setTextColor(context.getResources().getColor(R.color.taskItemDisabledColor));
                    holder.whiteButton.setVisibility(View.GONE);


                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_left);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {

                            remove(task.getDeal_id(),task.getTask_number());

                            /// insert task
                            task.setDisabled(1);
                            if (dBhelper.insertTask(task)) {

                                taskList = dBhelper.getAllTasksByDealID(task.getDeal_id());

                                //updateData
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

                }
                if (view.getId() == R.id.blueCircleButton) {

                    holder.taskName.setTextColor(context.getResources().getColor(R.color.taskItemColor));
                    holder.whiteButton.setVisibility(View.VISIBLE);

                    if(dBhelper.updateTask(task.getTaskName(),task.getDeal_id(),task.getTask_number(),0)){

                        task_row_index = -1;
                        taskList = dBhelper.getAllTasksByDealID(task.getDeal_id());
                        dBhelper.closeDB();
                    }
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
            dBhelper.closeDB();

            dealNameEditText.setText("");
            Toast.makeText(context, "Task changed", Toast.LENGTH_SHORT).show();
        }
    }

   // Remove a RecyclerView item containing a specified Deal object
   public void remove(int deal_id, int task_number) {

       if( dBhelper.deleteTask(deal_id, task_number)){

           taskList = dBhelper.getAllTasksByDealID(deal_id);
           notifyItemRemoved(task_number);
   }
        else{
           Toast.makeText(context, "Can not delete task", Toast.LENGTH_SHORT).show();
       }
           dBhelper.closeDB();
   }

    @Override
    public int getItemCount() {
        return taskList.size();
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

    public void animateTask(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.slide_left);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        LinearLayout taskLayout;
        TextView taskName;
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
            blueButton = itemView.findViewById(R.id.blueCircleButton);
            taskName = itemView.findViewById(R.id.taskNameTV);

            whiteButton.setOnClickListener(this);
            blueButton.setOnClickListener(this);
            taskLayout.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public void onBind(Task task) {

            if(task.getDisabled() == 1){

                taskName.setTextColor(whiteButton.getContext().getResources().getColor(R.color.taskItemDisabledColor));
                whiteButton.setVisibility(View.GONE);

            }else{
                taskName.setTextColor(whiteButton.getContext().getResources().getColor(R.color.taskItemColor));
                whiteButton.setVisibility(View.VISIBLE);

            }
             taskName.setText(task.getTaskName());
             Log.i("hreshtak","onTaskRecView onBind, adapter pos="+getAdapterPosition()+", deal_id="+task.getDeal_id());
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
