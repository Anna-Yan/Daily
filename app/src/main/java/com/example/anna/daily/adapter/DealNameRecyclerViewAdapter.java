package com.example.anna.daily.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import java.util.Collections;
import java.util.List;
import static com.example.anna.daily.MainActivity.base64Image;
import static com.example.anna.daily.MainActivity.isInserting;
import static com.example.anna.daily.adapter.TaskRecyclerViewAdapter.task_row_index;

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

    public static int deal_row_index = -1;
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
    public void onBindViewHolder(@NonNull final DealNameRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.onBind(dealList.get(position),context);

        holder.setIsRecyclable(false);

        //Always show 1 task
        if(deal_row_index != position) {

            if(deal_row_index == -2){
                holder.expandTask(position,true);
                Log.i("hreshtak", "expand Task =" + position);


            }else{

            if (dBhelper.getTasksCountByDealId(dealList.get(position).getId()) > 1) {

                    holder.expandTaskLayout(position, false);
                    Log.i("hreshtak", "onBindViewHolder,pos=" + position);
            }
        }}


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onLongClick(View view, int position) {

                deal_row_index = position;
                notifyDataSetChanged(); //make effect to reyclerView

                //open blue layout, and make visible update button by hiding add and save buttons
                animateBlueLayout(false);
                addDealBttn.setVisibility(View.INVISIBLE);
                saveDealBttn.setVisibility(View.INVISIBLE);
                updateDealBttn.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);

            }

            @Override
            public void onClick(View view, int position) {

                List<Deal> reversedList = dBhelper.getAllDeals();
                Collections.reverse(reversedList);

                Deal deal = reversedList.get(position);
                int deal_id = deal.getId();

                if (holder.isExpanded) {

                    //Expand all tasks
                    holder.expandTaskLayout(position,true);
                    //Toast.makeText(context, "expand,"+position, Toast.LENGTH_SHORT).show();
                }
                else{
                    //Always show 1 task
                    if(dBhelper.getAllTasksByDealID(deal_id).size()>0){

                        holder.expandTaskLayout(position,false);
                       // Toast.makeText(context, "colapse,"+position, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //make bold selected deal
        if(deal_row_index == position){

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


                String dealText = dealList.get(deal_row_index).getName();

                dealNameEditText.setText(dealText);
                dealNameEditText.setSelection(dealText.length());
                dealNameEditText.setHint("Add deal");


            }
        });


        //For opening White Layout for adding taskName
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                animateBlueLayout(true);
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


                animateBlueLayout(true);
                hideKeyboard();

                String edittedText;
                //For checking if White Layout is opened or not, if not then do nothing
                if (bottomWhiteLinLayout.getVisibility() == View.VISIBLE) {

                    edittedText = dealNameEditText.getText().toString();

                    updateDeal(deal_row_index, edittedText);
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

                animateBlueLayout(true);
                hideKeyboard();

                    holder.bindTask(deal_row_index, dealNameEditText.getText().toString());
                    //expand task layout to the height of taskItem size

                    dealNameEditText.setText("");
                    addDealBttn.setVisibility(View.VISIBLE);
                    saveDealBttn.setVisibility(View.VISIBLE);
                    updateDealBttn.setVisibility(View.VISIBLE);

                    bottomWhiteLinLayout.setVisibility(View.GONE);
                    pathButton.setVisibility(View.VISIBLE);
                    photoButton.setVisibility(View.VISIBLE);

                    //For deselect bold text
                    deal_row_index = -2;
                    task_row_index = -1;
                    notifyDataSetChanged();

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


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView dealName;
        ExpandableLinearLayout dealExpandableLayout;

        //set up recycler view
        RecyclerView taskRecyclerView;
        RecyclerView.LayoutManager taskVerticallinLayoutManager;
        RecyclerView.LayoutManager taskHorizontalLinLayoutManager;
        public static TaskRecyclerViewAdapter taskAdapter;

        ItemClickListener itemClickListener;
        boolean isExpanded = false;

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

                //Get deal_id at row_index
                List<Deal> reversedList = dBhelper.getAllDeals();
                Collections.reverse(reversedList);

                Deal deal = reversedList.get(row_index);
                int deal_id = deal.getId();

                Task task = new Task();
                task.setDeal_id(deal_id);
                task.setTaskName(taskName);
                task.setTask_number(dBhelper.getTasksCountByDealId(deal_id));
                task.setDisabled(0);

                if (dBhelper.insertTask(task)) {

                    //updateData
                    taskAdapter = new TaskRecyclerViewAdapter(itemView.getContext(), dBhelper.getAllTasksByDealID(deal.getId()));
                    taskRecyclerView.setAdapter(taskAdapter);

                    taskAdapter.notifyItemInserted(dBhelper.getAllTasksByDealID(deal.getId()).size());
                    taskAdapter.notifyDataSetChanged();

                    Log.i("hreshtak", "success:bindTask,task count at " + row_index + "=" + dBhelper.getTasksCountByDealId(deal_id));
                }
        }


        public void expandTaskLayout(int position, boolean expand){

            List<Deal> reversedList = dBhelper.getAllDeals();
            Collections.reverse(reversedList);

            Deal deal = reversedList.get(position);
            int deal_id = deal.getId();

            if(expand){

                isExpanded = false;
                ViewGroup.LayoutParams params = dealExpandableLayout.getLayoutParams();
                params.height = dBhelper.getAllTasksByDealID(deal_id).size()*(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.task_item_size));
                dealExpandableLayout.setLayoutParams(params);

                dealExpandableLayout.move( params.height);
                Log.i("hreshtak", "task list size=="+ dBhelper.getAllTasksByDealID(deal_id).size());

            }
            else{

                isExpanded = true;
                ViewGroup.LayoutParams params = dealExpandableLayout.getLayoutParams();
                params.height = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.task_item_size);
                dealExpandableLayout.setLayoutParams(params);

                dealExpandableLayout.move(params.height);
            }
        }

        public void expandTask(int position, boolean expand) {


            List<Deal> reversedList = dBhelper.getAllDeals();
            Collections.reverse(reversedList);

            Deal deal = reversedList.get(position);
            int deal_id = deal.getId();

            if (expand) {

                ViewGroup.LayoutParams params = dealExpandableLayout.getLayoutParams();
                params.height = dBhelper.getAllTasksByDealID(deal_id).size()*(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.task_item_size));
                dealExpandableLayout.setLayoutParams(params);
                dealExpandableLayout.move( params.height);
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
