package com.example.sherifawad.hyperencryption.Password;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Create Custom Shape
 */
public class Cell extends View {
    /**
     * Custom layout view with custom background
     * @param context
     * @param width
     * @param height
     */
    public Cell(final Context context, int width, int height, int parentLayoutWidth) {
        super(context);
        // Layout parameters (width * height)
        LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(width, height);
        // Assign the parameters to the layout
        setLayoutParams(cellParams);
        // Set custom background to the layout
        setBackground(getGradientDrawable(parentLayoutWidth));
    }

    /**
     * Create custom shape
     * @return
     * @param parentLayoutWidth
     */
    public GradientDrawable getGradientDrawable(int parentLayoutWidth) {
        GradientDrawable shape = new GradientDrawable();
        // Rectangle shape
        shape.setShape(GradientDrawable.RECTANGLE);
        // Set the shape color to transparent color
        shape.setColor(Color.TRANSPARENT);
        // Set shape borders with border width and color
        shape.setStroke((parentLayoutWidth/300), Color.BLACK);
        return shape;
    }

    /**
     * Custom shape with custom color
     * @param color color to assign to the shape background
     * @return
     */
    public GradientDrawable getGradientDrawable(int color, int parentLayoutWidth) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(color);
        shape.setStroke((parentLayoutWidth/300), Color.BLACK);
        return shape;
    }

}
