package dev.yanshouwang.wonder.material.shape;

import androidx.annotation.NonNull;

import com.google.android.material.shape.EdgeTreatment;
import com.google.android.material.shape.ShapePath;

public class CurveEdgeTreatment extends EdgeTreatment {
    private static final int ANGLE_DOWN = 90;
    private static final int ANGLE_UP = -90;
    private static final int ARC_HALF = 180;

    private float _radius;
    private float _cornerRadius;
    private float _horizontalOffset;
    private float _verticalOffset;
    private boolean _inside;
    private float _minimum;
    private float _maximum;

    public CurveEdgeTreatment(float radius, float cornerRadius, float horizontalOffset, float verticalOffset, boolean inside) {
        super();

        this._radius = radius;
        this._cornerRadius = cornerRadius;
        this._horizontalOffset = horizontalOffset;
        this._verticalOffset = verticalOffset;
        this._inside = inside;

        if (this._radius < 0) {
            this._radius = 0;
        }
        if (this._cornerRadius < 0) {
            this._cornerRadius = 0;
        }

        this.calculateBoundary();
    }

    private void calculateBoundary() {
        this._minimum = 0;
        this._maximum = (float) Math.sqrt(Math.pow(this._radius, 2) + 2 * this._radius * this._cornerRadius) + this._radius + this._cornerRadius;
    }

    @Override
    public void getEdgePath(float length, float center, float interpolation, @NonNull ShapePath shapePath) {
        super.getEdgePath(length, center, interpolation, shapePath);

        if (_inside) {
            this.getInsideEdgePath(length, interpolation, shapePath);
        } else {
            this.getOutsideEdgePath(length, interpolation, shapePath);
        }
    }

    private void getInsideEdgePath(float length, float interpolation, ShapePath shapePath) {
        float v1 = this._verticalOffset * interpolation;

        if (v1 <= this._minimum || v1 > this._maximum) {
            shapePath.lineTo(length, 0);
            return;
        }

        // 勾股定理
        float v2 = this._radius + this._cornerRadius;
        float v3 = this._cornerRadius + this._radius - v1;
        float distance = (float) Math.sqrt(Math.pow(v2, 2) - Math.pow(v3, 2));

        // 准备数据
        float left1, left2, left3;
        float top1, top2, top3;
        float right1, right2, right3;
        float bottom1, bottom2, bottom3;
        float startAngle1, startAngle2, startAngle3;
        float sweepAngle1, sweepAngle2, sweepAngle3;

        float center2 = this._horizontalOffset;
        float center1 = center2 - distance;
        float center3 = center2 + distance;

        left1 = center1 - this._cornerRadius;
        left2 = center2 - this._radius;
        left3 = center3 - this._cornerRadius;
        top1 = top3 = 0;
        top2 = v1 - 2 * this._radius;
        right1 = left1 + 2 * this._cornerRadius;
        right2 = left2 + 2 * this._radius;
        right3 = left3 + 2 * this._cornerRadius;
        bottom1 = bottom3 = 2 * this._cornerRadius;
        bottom2 = top2 + 2 * this._radius;
        float v4 = (float) Math.toDegrees(Math.acos(v3 / v2));
        sweepAngle1 = sweepAngle3 = v4;
        sweepAngle2 = -2 * v4;
        startAngle1 = ANGLE_UP;
        startAngle2 = startAngle1 + sweepAngle1 + ARC_HALF;
        startAngle3 = startAngle2 + sweepAngle2 - ARC_HALF;

        // 绘制
        shapePath.lineTo(center1, 0);
        shapePath.addArc(left1, top1, right1, bottom1, startAngle1, sweepAngle1);
        shapePath.addArc(left2, top2, right2, bottom2, startAngle2, sweepAngle2);
        shapePath.addArc(left3, top3, right3, bottom3, startAngle3, sweepAngle3);
        shapePath.lineTo(length, 0);
    }

    private void getOutsideEdgePath(float length, float interpolation, ShapePath shapePath) {
        float v1 = this._verticalOffset * interpolation;

        if (v1 <= this._minimum || v1 > this._maximum) {
            shapePath.lineTo(length, 0);
            return;
        }

        // 勾股定理
        float v2 = this._radius + this._cornerRadius;
        float v3 = this._cornerRadius + this._radius - v1;
        float distance = (float) Math.sqrt(Math.pow(v2, 2) - Math.pow(v3, 2));

        // 准备数据
        float left1, left2, left3;
        float top1, top2, top3;
        float right1, right2, right3;
        float bottom1, bottom2, bottom3;
        float startAngle1, startAngle2, startAngle3;
        float sweepAngle1, sweepAngle2, sweepAngle3;

        float center2 = this._horizontalOffset + this._radius;
        float center1 = center2 - distance;
        float center3 = center2 + distance;

        left1 = center1 - this._cornerRadius;
        left2 = center2 - this._radius;
        left3 = center3 - this._cornerRadius;
        top1 = top3 = -2 * this._cornerRadius;
        top2 = -v1;
        right1 = left1 + 2 * this._cornerRadius;
        right2 = left2 + 2 * this._radius;
        right3 = left3 + 2 * this._cornerRadius;
        bottom1 = bottom3 = 0;
        bottom2 = top2 + 2 * this._radius;
        float v4 = (float) Math.toDegrees(Math.acos(v3 / v2));
        sweepAngle1 = sweepAngle3 = -v4;
        sweepAngle2 = 2 * v4;
        startAngle1 = ANGLE_DOWN;
        startAngle2 = startAngle1 + sweepAngle1 + ARC_HALF;
        startAngle3 = startAngle2 + sweepAngle2 - ARC_HALF;

        // 绘制
        shapePath.lineTo(center1, 0);
        shapePath.addArc(left1, top1, right1, bottom1, startAngle1, sweepAngle1);
        shapePath.addArc(left2, top2, right2, bottom2, startAngle2, sweepAngle2);
        shapePath.addArc(left3, top3, right3, bottom3, startAngle3, sweepAngle3);
        shapePath.lineTo(length, 0);
    }

    public float getHorizontalOffset() {
        return this._horizontalOffset;
    }

    public void setHorizontalOffset(float horizontalOffset) {
        this._horizontalOffset = horizontalOffset;
    }

    public float getVerticalOffset() {
        return this._verticalOffset;
    }
}
