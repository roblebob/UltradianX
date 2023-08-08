package com.roblebob.ultradianx.ui.extra;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.util.UtilKt;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpiralClock extends View {
    public static final String TAG = SpiralClock.class.getSimpleName();
    public SpiralClock(Context context) {
        super(context);
    }
    public SpiralClock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public SpiralClock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public SpiralClock(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    public static final int L = 2540;
    public static final int C = L / 2;

    public List<ShapeDrawable> mShapeDrawablesCoreList = new ArrayList<>();
    public List<ShapeDrawable> mShapeDrawablesExtraList = new ArrayList<>();


    public MyDataLists myDataLists;
    public MyPathStore myPathStore;
    public void setup() {
        myDataLists = new MyDataLists();
        myPathStore = new MyPathStore();
        mShapeDrawablesCoreList.clear();
        mShapeDrawablesCoreList.add(getHoursTrackShape());
        refresh();
    }
    public void refresh() {

        List<ShapeDrawable> shapeDrawablesList = new ArrayList<>(mShapeDrawablesCoreList);
        shapeDrawablesList.addAll(mShapeDrawablesExtraList);
        LayerDrawable layerDrawable = new LayerDrawable( shapeDrawablesList.toArray(new ShapeDrawable[0]));
        setBackground( layerDrawable );
    }


    public void submit(Instant start, Instant end) {
        Instant now = Instant.now();
        mShapeDrawablesExtraList.clear();

        //mShapeDrawablesExtraList.add( getRayShape(now) );
        //mShapeDrawablesExtraList.add( getRelevantTileShape(now) );
        mShapeDrawablesExtraList.add( getArmShape(now) );
        if (start != null && end != null) {
            mShapeDrawablesExtraList.add( getHighlights(start, end) );
        }

        refresh();
    }


    public ShapeDrawable getHighlights(Instant start, Instant end) {

        List<String> startPoints = solve(start);
        List<String> endPoints = solve(end);

        int startHour = getHour(start);
        int endHour = getHour(end);
        Log.e(TAG, "startHour: " + startHour  + "  endHour: " + endHour);


        List<String> outerPoints = new ArrayList<>();
        List<String> innerPoints = new ArrayList<>();

        outerPoints.add(startPoints.get(0));
        innerPoints.add(startPoints.get(1));


        if (startHour != endHour) {
            for (int i = startHour + 1; i <= endHour; i++) {

                outerPoints.add( myDataLists.mOuterHoursList.get(i));
                Log.e(TAG, "outerPoints: " + myDataLists.mOuterHoursList.get(i));
                innerPoints.add( myDataLists.mInnerHoursList.get(i));
            }
        }

        outerPoints.add(endPoints.get(0));
        innerPoints.add(endPoints.get(1));


        Collections.reverse(innerPoints);

        outerPoints.addAll(innerPoints);


        Path path = new Path();
        path.addPath( genPathFromList(outerPoints));
        path.setFillType(Path.FillType.EVEN_ODD);
        ShapeDrawable shapeHigh = new ShapeDrawable( new PathShape(path, L, L));
        shapeHigh.getPaint().setColor(Color.YELLOW);
        shapeHigh.setAlpha((int) (0.1 * 255));
        shapeHigh.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        shapeHigh.getPaint().setStrokeWidth(12.0f);
        shapeHigh.getPaint().setStrokeCap(Paint.Cap.ROUND);
        shapeHigh.getPaint().setStrokeJoin(Paint.Join.ROUND);
        return shapeHigh;
    }




    public Instant getRootInstant() {
        return Instant
                .now()
                .atZone(ZoneOffset.of("+02:00"))
                .withHour(6).withMinute(0).withSecond(0).withNano(0)
                .toInstant();
    }
    public int getHour(Instant instant) {
        Duration duration = Duration.between(getRootInstant(), instant);
        int hour = (int) duration.toHours();
        if (hour < 0) {
            hour = 24 + hour + 1;
        }
        return hour;
    }
    public double getAngle(Instant instant) {
        Duration duration = Duration.between(getRootInstant(), instant);
        double angle = duration.toMinutes() / 2.0;  // 1 degree  ==== 2 minutes
        angle = 360 - (angle % 360);      // counterclockwise
        angle = angle * (Math.PI / 180.0);  // convert from degree to radians
        return angle;
    }





    public List<String> solve(Instant instant) {

        int hour = getHour(instant);
        double angle = getAngle(instant);

        double z1_x = C;
        double z1_y = C;
        double z2_x = (int) (C + (C * Math.sin(angle)));
        double z2_y = (int) (C + (C * Math.cos(angle)));

        String[] arg;
        arg = myDataLists.mOuterHoursList.get(hour).split("[\\s]*,[\\s]*");
        double p1_x = Integer.parseInt(arg[0]);
        double p1_y = Integer.parseInt(arg[1]);
        arg = myDataLists.mOuterHoursList.get(hour + 1).split("[\\s]*,[\\s]*");
        double p2_x = Integer.parseInt(arg[0]);
        double p2_y = Integer.parseInt(arg[1]);

        arg = myDataLists.mInnerHoursList.get(hour).split("[\\s]*,[\\s]*");
        double q1_x = Integer.parseInt(arg[0]);
        double q1_y = Integer.parseInt(arg[1]);
        arg = myDataLists.mInnerHoursList.get(hour + 1).split("[\\s]*,[\\s]*");
        double q2_x = Integer.parseInt(arg[0]);
        double q2_y = Integer.parseInt(arg[1]);

        int x1 = (int) (((z2_x - z1_x) * (p2_x * p1_y - p1_x * p2_y) - (p2_x - p1_x) * (z2_x * z1_y - z1_x * z2_y)) / ((z2_y - z1_y) * (p2_x - p1_x) - (p2_y - p1_y) * (z2_x - z1_x)));
        int y1 = (int) (((z2_y - z1_y) * (p2_x * p1_y - p1_x * p2_y) - (p2_y - p1_y) * (z2_x * z1_y - z1_x * z2_y)) / ((z2_y - z1_y) * (p2_x - p1_x) - (p2_y - p1_y) * (z2_x - z1_x)));

        int x2 = (int) (((z2_x - z1_x) * (q2_x * q1_y - q1_x * q2_y) - (q2_x - q1_x) * (z2_x * z1_y - z1_x * z2_y)) / ((z2_y - z1_y) * (q2_x - q1_x) - (q2_y - q1_y) * (z2_x - z1_x)));
        int y2 = (int) (((z2_y - z1_y) * (q2_x * q1_y - q1_x * q2_y) - (q2_y - q1_y) * (z2_x * z1_y - z1_x * z2_y)) / ((z2_y - z1_y) * (q2_x - q1_x) - (q2_y - q1_y) * (z2_x - z1_x)));



        ArrayList<String> list = new ArrayList<>();
        list.add(x1 + "," + y1);
        list.add(x2 + "," + y2);
        return list;
    }




    public ShapeDrawable getArmShape(Instant instant) {
        // create a path basic path
        List<String> intersectionPoints = solve( instant);


        String s = "M " + intersectionPoints.get(0)  +  " L " + intersectionPoints.get(1);
        Path path = new Path();
        path.addPath(PathParser.createPathFromPathData(s));
        ShapeDrawable shapePath = new ShapeDrawable(new PathShape(path, L, L));
        shapePath.getPaint().setColor(Color.BLUE);
        shapePath.setAlpha((int) (1.0 * 255));
        shapePath.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        shapePath.getPaint().setStrokeWidth(20.0f);
        shapePath.getPaint().setStrokeCap(Paint.Cap.ROUND);
        shapePath.getPaint().setStrokeJoin(Paint.Join.ROUND);
        return shapePath;

    }



    public ShapeDrawable getRayShape(Instant instant) {

        double angle = getAngle( instant);


        int x = (int) (C + (C * Math.sin(angle)));
        int y = (int) (C + (C * Math.cos(angle)));

        String s = "M " + C + "," + C + " L " + x + "," + y;
        Log.e(TAG, "getArm: " + s);
        Path path = new Path();
        path.addPath( PathParser.createPathFromPathData(s));
        ShapeDrawable shapePath = new ShapeDrawable( new PathShape(path, L, L));
        shapePath.getPaint().setColor(Color.RED);
        shapePath.setAlpha((int) (1.0 * 255));
        shapePath.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        shapePath.getPaint().setStrokeWidth(12.0f);
        shapePath.getPaint().setStrokeCap(Paint.Cap.ROUND);
        shapePath.getPaint().setStrokeJoin(Paint.Join.ROUND);
        return shapePath;
    }




    public ShapeDrawable getHoursTrackShape() {
        // create a path basic path

        ShapeDrawable shapePath = new ShapeDrawable( new PathShape(myPathStore.mHoursTrack, L, L));
        shapePath.getPaint().setColor(Color.WHITE);
        shapePath.setAlpha((int) (0.1 * 255));
        shapePath.getPaint().setStyle(Paint.Style.FILL);
        shapePath.getPaint().setStrokeWidth(12.0f);
        shapePath.getPaint().setStrokeCap(Paint.Cap.ROUND);
        shapePath.getPaint().setStrokeJoin(Paint.Join.ROUND);
        return shapePath;
    }






    public ShapeDrawable getRelevantTileShape(Instant instant) {

        int hour = getHour(instant);

        Path path = new Path();
        path.addPath( PathParser.createPathFromPathData("M " + myDataLists.mOuterHoursList.get(hour) + " L " + myDataLists.mOuterHoursList.get(hour + 1)));
        path.addPath( PathParser.createPathFromPathData("M " + myDataLists.mInnerHoursList.get(hour) + " L " + myDataLists.mInnerHoursList.get(hour + 1)));
        //path.addPath( PathParser.createPathFromPathData("M " + myDataLists.mOuterHoursList.get(hour) + " L " + myDataLists.mInnerHoursList.get(hour)));
        //path.addPath( PathParser.createPathFromPathData("M " + myDataLists.mOuterHoursList.get(hour+ 1) + " L " + myDataLists.mInnerHoursList.get(hour + 1)));

        path.setFillType(Path.FillType.EVEN_ODD);

        ShapeDrawable shapePath = new ShapeDrawable( new PathShape(path, L, L));
        shapePath.getPaint().setColor(Color.RED);
        shapePath.setAlpha((int) (1.0 * 255));
        shapePath.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        shapePath.getPaint().setStrokeWidth(12.0f);
        shapePath.getPaint().setStrokeCap(Paint.Cap.ROUND);
        shapePath.getPaint().setStrokeJoin(Paint.Join.ROUND);
        return shapePath;
    }




















    class MyPathStore {
        public Path mHoursTrack;
        public MyPathStore() {
            List<String> track = new ArrayList<>();
            track.addAll( myDataLists.mOuterHoursList);
            track.addAll( myDataLists.mInnerHoursList);
            mHoursTrack = genPathFromList( track);
            mHoursTrack.setFillType(Path.FillType.EVEN_ODD);
        }
    }

    public Path genPathFromList(List<String> inputList) {
        List<String> list = new ArrayList<>( inputList);
        list.add(0, "M");
        list.add(2, "L");
        Path path = new Path();
        path.addPath( PathParser.createPathFromPathData( UtilKt.list2String(list)));
        return path;
    }

    public class MyDataLists {
        public List<String> mOuterHoursList = new ArrayList<>();
        public List<String> mInnerHoursList = new ArrayList<>();

        public MyDataLists() {
            Resources res = getResources();
            mOuterHoursList .addAll( Arrays.asList( res.getStringArray( R.array.outer_path_hours)));
            mInnerHoursList .addAll( Arrays.asList( res.getStringArray( R.array.inner_path_hours)));
        }
    }









}
