package dev.chrisstone.pushyalarms;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

public class AlarmListItemClickListener implements RecyclerView.OnItemTouchListener {

    private final GestureDetector gestureDetector;
    protected OnItemClickListener listener;
    @Nullable
    private View childView;

    private int childViewPosition;

    public AlarmListItemClickListener(Context context, OnItemClickListener listener) {
        this.gestureDetector = new GestureDetector(context, new GestureListener());
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
        childView = view.findChildViewUnder(event.getX(), event.getY());
        if (childView != null) {
            childViewPosition = view.getChildLayoutPosition(childView);
        }

        return childView != null && gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent event) {
        // Not needed.
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    /**
     * A click listener for items.
     */
    public interface OnItemClickListener {

        /**
         * Called when an item is clicked.
         *
         * @param childView View of the item that was clicked.
         * @param position  Position of the item that was clicked.
         */
        void onItemClick(View childView, int position);

        /**
         * Called when an item is long pressed.
         *
         * @param childView View of the item that was long pressed.
         * @param position  Position of the item that was long pressed.
         */
        void onItemLongPress(View childView, int position);

    }

    protected class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            if (childView != null) {
                listener.onItemClick(childView, childViewPosition);
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            if (childView != null) {
                listener.onItemLongPress(childView, childViewPosition);
            }
        }

        @Override
        public boolean onDown(MotionEvent event) {
            // Best practice to always return true here.
            // http://developer.android.com/training/gestures/detector.html#detect
            return true;
        }

    }

}