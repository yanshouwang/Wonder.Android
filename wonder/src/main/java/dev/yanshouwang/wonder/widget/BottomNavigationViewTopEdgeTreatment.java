package dev.yanshouwang.wonder.widget;

import androidx.annotation.NonNull;

import com.google.android.material.shape.EdgeTreatment;
import com.google.android.material.shape.ShapePath;

public class BottomNavigationViewTopEdgeTreatment extends EdgeTreatment {

    private static final int ANGLE_DOWN = 90;
    private static final int ARC_HALF = 180;

    @Override
    public void getEdgePath(float length, float center, float interpolation, @NonNull ShapePath shapePath) {
        super.getEdgePath(length, center, interpolation, shapePath);
        // 准备数据
        float horizontalOffset;
        float verticalOffset;
        float center1, center2, center3;
        float radius, ri;
        float startAngle1, startAngle2, startAngle3;
        float sweepAngle1, sweepAngle2, sweepAngle3;

        if (interpolation == 0) {
            shapePath.lineTo(length, 0);
            return;
        }

        radius = length / 15;
        horizontalOffset = 0;
        verticalOffset = radius / 2;
        float value = (float) Math.sqrt(Math.pow(radius + radius, 2) - Math.pow(radius + radius - verticalOffset, 2));
        center1 = horizontalOffset;
        center2 = center1 + value;
        center3 = center2 + value;

        float vi = radius - verticalOffset * interpolation;
        ri = (float) (Math.pow(radius, 2) - Math.pow(vi, 2) - Math.pow(value, 2)) / (2 * (vi - radius));


        float degrees = (float) Math.toDegrees(Math.atan2(value, ri + vi));
        sweepAngle1 = sweepAngle3 = -degrees;
        sweepAngle2 = 2 * degrees;
        startAngle1 = ANGLE_DOWN;
        startAngle2 = startAngle1 + sweepAngle1 + ARC_HALF;
        startAngle3 = startAngle2 + sweepAngle2 - ARC_HALF;
        // 画形状
        shapePath.lineTo(center1, 0);
        shapePath.addArc(center1 - ri, -2 * ri, center1 + ri, 0, startAngle1, sweepAngle1);
        shapePath.addArc(center2 - radius, -verticalOffset * interpolation, center2 + radius, -verticalOffset * interpolation + 2 * radius, startAngle2, sweepAngle2);
        shapePath.addArc(center3 - ri, -2 * ri, center3 + ri, 0, startAngle3, sweepAngle3);
        shapePath.lineTo(length, 0);
    }
}
