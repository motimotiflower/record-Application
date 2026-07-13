package com.example.sample3.Todo.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sample3.R;
import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.db.model.Todo;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    // チェック状態が変わったとき Activity / Fragment に通知するためのコールバック
    public interface OnTodoCheckedChangeListener {
        void onCheckedChanged(Todo todo, boolean isChecked);
    }

    public interface OnTodoClickListener {
        void onTodoClick(Todo todo);
    }

    //メンバ
    private List<Todo> todoList;
    private DatabaseHelper dbHelper;
    private OnTodoClickListener clickListener;


    public TodoAdapter(List<Todo> todoList, DatabaseHelper dbHelper, OnTodoClickListener clickListener) {
        this.todoList = todoList;
        this.dbHelper = dbHelper;
        this.clickListener = clickListener;
    }

    public TodoAdapter(List<Todo> todoList, DatabaseHelper dbHelper) {
        this.todoList = todoList;
        this.dbHelper = dbHelper;
    }


    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todoList.get(position);

        holder.titleTextView.setText(todo.getTitle());

        // リスナーを一旦解除
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(todo.isDone());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                // DBから削除
                int deletedRows = dbHelper.deleteTodoById(todo.getId());

                // Adapterのリストから削除して反映
                if (deletedRows > 0) {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        todoList.remove(pos);   //リストからposの位置のデータを削除
                        notifyItemRemoved(pos); //RecyclerViewに通知して更新かける
                    }
                }

            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onTodoClick(todo);
            }
        });

    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView titleTextView;

        public TodoViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_todo);
            titleTextView = itemView.findViewById(R.id.text_todo_title);
        }
    }

}