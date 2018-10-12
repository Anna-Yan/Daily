package com.example.anna.daily.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anna.daily.DBhelper;
import com.example.anna.daily.ItemLongClickListener;
import com.example.anna.daily.R;
import com.example.anna.daily.model.Deal;
import com.example.anna.daily.model.Task;
import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.anna.daily.MainActivity.base64Image;
import static com.example.anna.daily.adapter.DealNameRecyclerViewAdapter.dBhelper;


public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {


    private Context context;

    private static LinearLayout bottomBlueLinLayout;
    private LinearLayout bottomWhiteLinLayout;
    private ExpandableLayout dealExpandableLayout;

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

    private int row_index = -1;
    private  List<Task> taskList = new ArrayList<>();
    //private DBhelper dBhelper;


    public TaskRecyclerViewAdapter (Context mContext, List<Task> taskList) {

        this.context = mContext;
        this.taskList = taskList;
       // this.dBhelper = new DBhelper(context);
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

        Log.i("hreshtak","onCreateViewHolder");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskRecyclerViewAdapter.ViewHolder holder, final int position) {


        holder.onBind(taskList.get(position));
        holder.setIsRecyclable(false);

        //make bold selected task
        if(row_index == position){
            holder.taskName.setTextColor(Color.BLACK);
        }
        else{
            if(holder.whiteButton.isShown()){

                holder.taskName.setTextColor(context.getResources().getColor(R.color.taskItemDisabledColor));
            }else{
                holder.taskName.setTextColor(context.getResources().getColor(R.color.taskItemColor));
            }
        }

        holder.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public void onLongClick(View view, int position) {

                row_index = position;
                notifyDataSetChanged(); //make effect to reyclerView
                animateBlueLayout(false);

                addDealBttn.setVisibility(View.INVISIBLE);
                saveDealBttn.setVisibility(View.INVISIBLE);
                editDealButton.setVisibility(View.INVISIBLE);
                deleteDealButton.setVisibility(View.INVISIBLE);
                updateDealBttn.setVisibility(View.INVISIBLE);
                updateTaskButton.setVisibility(View.VISIBLE);
                editTaskButton.setVisibility(View.VISIBLE);
                pathButton.setVisibility(View.GONE);
                photoButton.setVisibility(View.GONE);
 //stex

                    //For updating task
                    updateTaskButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            animateBlueLayout(true);
                            hideKeyboard();
                            addDealBttn.setVisibility(View.VISIBLE);
                            editDealButton.setVisibility(View.VISIBLE);

                            //Getting back old taskName color
                            if(holder.whiteButton.isShown()){

                                holder.taskName.setTextColor(context.getResources().getColor(R.color.taskItemColor));
                            }else{
                                holder.taskName.setTextColor(context.getResources().getColor(R.color.taskItemDisabledColor));
                            }

                            if(bottomWhiteLinLayout.getVisibility() == View.VISIBLE ) {

                                String edittedText = dealNameEditText.getText().toString();
                                holder.taskName.setText(edittedText);

                                updateTask(edittedText, row_index);

                            }
                            row_index = -1;
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

                            String taskName = taskList.get(row_index).getTaskName();
                            dealNameEditText.setText(taskName);
                            dealNameEditText.setSelection(taskName.length());

                        }
                    });

                    deleteTaskButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            animateBlueLayout(true);

                            remove(taskList.get(row_index).getDeal_id(),row_index);

                            row_index = -1; //uncheck bold check
                            notifyDataSetChanged();

                            deleteDealButton.setVisibility(View.VISIBLE);
                            addDealBttn.setVisibility(View.VISIBLE);
                            pathButton.setVisibility(View.VISIBLE);
                            photoButton.setVisibility(View.VISIBLE);

                            dealExpandableLayout.collapse();

                            //es 2-@ piti heto lini dealList size, kanchel DB-ic
                           // for (int i = 0; i < 2; i++) {
                                //find the expandable layout at position and collapse
                                //ExpandableLinearLayout dealExpLayout = deallinLayoutManager.findViewByPosition(position).findViewById(R.id.dealExpLayout);
                                //collapseExpandLayout(dealExpandableLayout, context);
                           // }

                        }
                    });

            }}); //stex
    }

    public void updateTask(String edittedText, int row_index) {

        Task task = taskList.get(row_index);
        int deal_id = task.getDeal_id();
        int tusk_number = task.getTask_number();

        if(dBhelper.updateTask(edittedText,deal_id,tusk_number)){

            taskList = dBhelper.getAllTasksByDealID(deal_id);
            dBhelper.closeDB();

            dealNameEditText.setText("");
            Toast.makeText(context, "Task changed", Toast.LENGTH_SHORT).show();
        }
    }

   /* private void collapseExpandLayout(ExpandableLinearLayout dealExpandableLayout, Context context){

        ViewGroup.LayoutParams params = dealExpandableLayout.getLayoutParams();
        params.height = taskList.size()*(context.getResources().getDimensionPixelSize(R.dimen.task_item_size));
        dealExpandableLayout.setLayoutParams(params);
        dealExpandableLayout.move(taskList.size()*(context.getResources().getDimensionPixelSize(R.dimen.task_item_size)));

    } */
   // Remove a RecyclerView item containing a specified Deal object
   public void remove(int deal_id, int task_number) {

       if( dBhelper.deleteTask(deal_id, task_number)){

           taskList = dBhelper.getAllTasksByDealID(deal_id);
           notifyItemRemoved(task_number);
           dBhelper.closeDB();
       }
        else
                Toast.makeText(context, "Can not delete task", Toast.LENGTH_SHORT).show();

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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        LinearLayout taskLayout;
        TextView taskName;
        ImageButton whiteButton;
        ImageButton blueButton;

        ItemLongClickListener itemLongClickListener;

        public void setItemLongClickListener(ItemLongClickListener itemLongClickListener){
            this.itemLongClickListener = itemLongClickListener;
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

          // if(getAdapterPosition() == task.getDeal_id()){

             taskName.setText(task.getTaskName());
             Log.i("hreshtak","onTaskRecView onBind, adapter pos="+getAdapterPosition()+", deal_id="+task.getDeal_id());

        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.whiteCircleButton:

                    taskName.setTextColor(whiteButton.getContext().getResources().getColor(R.color.taskItemDisabledColor));
                    whiteButton.setVisibility(View.GONE);

                    break;

                case R.id.blueCircleButton:

                    taskName.setTextColor(whiteButton.getContext().getResources().getColor(R.color.taskItemColor));
                    whiteButton.setVisibility(View.VISIBLE);

                    break;
            }

        }

        @Override
        public boolean onLongClick(View v) {

            itemLongClickListener.onLongClick(v,getAdapterPosition());
            return true;
        }
    }
}
