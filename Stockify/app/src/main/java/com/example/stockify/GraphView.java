package com.example.stockify;import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {
    private Paint paint;
    private float[] points;  // Holds Y values for the graph to fluctuate up and down
    private float progress = 0;  // Tracks how far we've drawn on the X axis
    private int maxSteps;   // Maximum steps on the X-axis

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.GREEN);  // Line color for the stock graph
        paint.setStrokeWidth(8f);     // Line thickness

        // Initialize Y-axis points for the up-down movement
        points = new float[] {
                0, 100, 200, 50, 300, 150, 400, 100, 500, 250, 600
        };

        maxSteps = points.length - 1;  // Number of segments (X steps) to be drawn
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Define the width and height of the view
        int width = getWidth();
        int height = getHeight();

        // Scale X to cover the entire width of the screen
        float stepX = width / (float) maxSteps;

        // Draw the diagonal fluctuating line
        for (int i = 0; i < points.length - 1 && i <= progress; i++) {
            canvas.drawLine(
                    stepX * i, height - points[i],           // Start point (X, Y), adjusted Y
                    stepX * (i + 1), height - points[i + 1], // End point (X, Y), adjusted Y
                    paint
            );
        }

        // Increment the progress to draw the next segment of the graph
        progress += 0.5f;  // Controls the speed of the animation
        if (progress < maxSteps) {
            postInvalidateDelayed(50);  // Redraw the graph every 50 milliseconds
        }
    }
}
