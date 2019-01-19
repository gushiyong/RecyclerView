package com.example.administrator.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnStartDragListener {

    SlideRecyclerView mRecyclerView;
    ItemTouchHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rv_listview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        final List<String> list = new ArrayList<>();
        list.add("12345611111");
        list.add("12345622222");
        list.add("12345614444444441111");
        list.add("5555555555555");
        final NormalAdapter mAdapter = new NormalAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter.setOnDeleteClickListener(new OnDeleteClickLister() {
            @Override
            public void onDeleteClick(View view, int position) {
                mAdapter.notifyItemRemoved(position);
                list.remove(position);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.closeMenu();
            }
        });
//        mHelper = new ItemTouchHelper(new SimpleItemTouchCallback(mAdapter, list));
//        mHelper.attachToRecyclerView(mRecyclerView);
    }

    public class NormalAdapter extends RecyclerView.Adapter<NormalAdapter.VH> {

        private OnDeleteClickLister mDeleteClickListener;

        public class VH extends RecyclerView.ViewHolder {
            public final TextView txtValue;
            TextView txtDelete;

            public VH(View itemView) {
                super(itemView);
                txtValue = itemView.findViewById(R.id.txt_value);
                txtDelete = itemView.findViewById(R.id.tv_delete);
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            return new VH(view);
        }

        private List<String> mList;

        public NormalAdapter(List<String> list) {
            this.mList = list;
        }

        @Override
        public void onBindViewHolder(final VH holder, final int position) {
            holder.txtValue.setText(mList.get(position));
            holder.txtValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            holder.txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDeleteClickListener.onDeleteClick(view, position);
                }
            });
//            holder.txtValue.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        startDrag(holder);
//                    }
//                    return false;
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setOnDeleteClickListener(OnDeleteClickLister listener) {
            this.mDeleteClickListener = listener;
        }

    }

    private class SimpleItemTouchCallback extends ItemTouchHelper.Callback {

        private NormalAdapter mAdapter;
        private List<String> mData;

        public SimpleItemTouchCallback(NormalAdapter adapter, List<String> data) {
            mAdapter = adapter;
            mData = data;
        }

        //设置支持的拖拽、滑动的方向
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN; // 上下拖拽
            int swipeFlag = ItemTouchHelper.START;// | ItemTouchHelper.END; //左->右和右->左滑动
            return makeMovementFlags(dragFlag, swipeFlag);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();
            Collections.swap(mData, from, to);
            mAdapter.notifyItemMoved(from, to);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();
            mData.remove(pos);
            mAdapter.notifyItemRemoved(pos);
        }

        //状态改变时回调
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                NormalAdapter.VH holder = (NormalAdapter.VH) viewHolder;
                holder.itemView.setBackgroundColor(0xffbcbcbc); //设置拖拽和侧滑时的背景色
            }
        }

        //拖拽或滑动完成之后调用，用来清除一些状态
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            NormalAdapter.VH holder = (NormalAdapter.VH) viewHolder;
            holder.itemView.setBackgroundColor(0xffeeeeee); //背景色还原
        }
    }

    @Override
    public void startDrag(RecyclerView.ViewHolder holder) {
        mHelper.startDrag(holder);
    }

}

interface OnStartDragListener {
    void startDrag(RecyclerView.ViewHolder holder);
}

interface OnDeleteClickLister {
    void onDeleteClick(View view, int position);
}
