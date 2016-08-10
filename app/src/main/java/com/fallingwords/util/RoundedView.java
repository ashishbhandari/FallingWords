package com.fallingwords.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by b_ashish on 10-Aug-16.
 */

public class RoundedView extends View {

    public RoundedView(Context context) {
        super(context);
    }

    public RoundedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onDraw(android.graphics.Canvas canvas) {
        Paint paint = new Paint();

        paint.setAlpha(255);
        canvas.translate(0, 30);
        paint.setColor(Color.BLUE);
        Path mPath = new Path();
        mPath.addRoundRect(new RectF(0, 0, 100, 100), 20, 20, Path.Direction.CCW);
        canvas.clipPath(mPath, Region.Op.INTERSECT);
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        canvas.drawRect(0, 0, 120, 120, paint);

    }
}
