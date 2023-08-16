package com.roblebob.ultradianx.ui.extra

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.PathShape
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.PathParser
import com.roblebob.ultradianx.R
import com.roblebob.ultradianx.ui.extra.SpiralClock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.Arrays
import java.util.Collections
import kotlin.math.cos
import kotlin.math.sin

class SpiralClock : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private val mOuterHoursList = mutableListOf(*resources.getStringArray(R.array.outer_path_hours))
    private val mInnerHoursList = mutableListOf(*resources.getStringArray(R.array.inner_path_hours))

    private val mShapeDrawablesCoreList = mutableListOf<ShapeDrawable>()
    private val mShapeDrawablesExtraList = mutableListOf<ShapeDrawable>()
    private val myPathStore  = MyPathStore()
    fun setup() {
        mShapeDrawablesCoreList.clear()
        mShapeDrawablesCoreList.add(hoursTrackShape)
        refresh()
    }

    private fun refresh() {
        val shapeDrawablesList: MutableList<ShapeDrawable> = ArrayList(mShapeDrawablesCoreList)
        shapeDrawablesList.addAll(mShapeDrawablesExtraList)
        val layerDrawable = LayerDrawable(shapeDrawablesList.toTypedArray())
        background = layerDrawable
    }

    fun submit(start: Instant?, end: Instant?) {
        val now = Instant.now()
        mShapeDrawablesExtraList.clear()

        //mShapeDrawablesExtraList.add( getRayShape(now) );
        //mShapeDrawablesExtraList.add( getRelevantTileShape(now) );
        mShapeDrawablesExtraList.add(getArmShape(now))
        if (start != null && end != null) {
            mShapeDrawablesExtraList.add(getHighlights(start, end))
        }
        refresh()
    }


    /**
     * wakeup time, assuming you woke up at 6:00 am. :)
     */

    private val rootInstant: Instant
        get() = Instant
            .now()
            .atZone(ZoneOffset.of("+02:00"))
            .withHour(6).withMinute(0).withSecond(0).withNano(0)
            .toInstant()


    /**
     * Given an instant, this method returns the hour of the day.
     * The hour is used as the index of the list of points that define the spiral.
     * @param instant : Instant
     * @return int : hour of the day
     **/
    private fun getHour(instant: Instant): Int {
        val duration = Duration.between(rootInstant, instant)
        var hour = duration.toHours().toInt()
        if (hour < 0) {
            hour += 24 + 1
        }
        return hour
    }

    /**
     * Assuming the spiral is a clock,
     * this method returns the angle of the instant's arm,
     * and that of rootInstant's
     * @param instant : Instant
     * @return double : angle in radians
     */
    private fun getAngle(instant: Instant): Double {
        val duration = Duration.between(rootInstant, instant)
        var angle = duration.toMinutes() / 2.0 // 1 degree  ==== 2 minutes
        angle = 360 - angle % 360 // counterclockwise
        angle *= (Math.PI / 180.0) // convert from degree to radians
        return angle
    }


    /**
     * Given an instant, this method returns the intersection points of the ray and the spiral
     * @param instant : Instant
     * @return List<String> : list of intersection points
     **/
    private fun solve(instant: Instant): List<String> {
        val hour = getHour(instant)
        val angle = getAngle(instant)
        val z1_x = C.toDouble()
        val z1_y = C.toDouble()
        val z2_x = (C + C * sin(angle)).toInt()
            .toDouble()
        val z2_y = (C + C * cos(angle)).toInt()
            .toDouble()

        var arg = mOuterHoursList[hour].split("\\s*,\\s*".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val p1_x = arg[0].toDouble()
        val p1_y = arg[1].toDouble()

        arg = mOuterHoursList[hour + 1].split("\\s*,\\s*".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val p2_x = arg[0].toDouble()
        val p2_y = arg[1].toDouble()

        arg = mInnerHoursList[hour].split("\\s*,\\s*".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val q1_x = arg[0].toDouble()
        val q1_y = arg[1].toDouble()

        arg = mInnerHoursList[hour + 1].split("\\s*,\\s*".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val q2_x = arg[0].toDouble()
        val q2_y = arg[1].toDouble()
        val x1 =
            (((z2_x - z1_x) * (p2_x * p1_y - p1_x * p2_y) - (p2_x - p1_x) * (z2_x * z1_y - z1_x * z2_y)) / ((z2_y - z1_y) * (p2_x - p1_x) - (p2_y - p1_y) * (z2_x - z1_x))).toInt()
        val y1 =
            (((z2_y - z1_y) * (p2_x * p1_y - p1_x * p2_y) - (p2_y - p1_y) * (z2_x * z1_y - z1_x * z2_y)) / ((z2_y - z1_y) * (p2_x - p1_x) - (p2_y - p1_y) * (z2_x - z1_x))).toInt()
        val x2 =
            (((z2_x - z1_x) * (q2_x * q1_y - q1_x * q2_y) - (q2_x - q1_x) * (z2_x * z1_y - z1_x * z2_y)) / ((z2_y - z1_y) * (q2_x - q1_x) - (q2_y - q1_y) * (z2_x - z1_x))).toInt()
        val y2 =
            (((z2_y - z1_y) * (q2_x * q1_y - q1_x * q2_y) - (q2_y - q1_y) * (z2_x * z1_y - z1_x * z2_y)) / ((z2_y - z1_y) * (q2_x - q1_x) - (q2_y - q1_y) * (z2_x - z1_x))).toInt()
        val list = ArrayList<String>()
        list.add("$x1,$y1")
        list.add("$x2,$y2")
        return list
    }












    private fun getHighlights(start: Instant, end: Instant): ShapeDrawable {
        val startPoints = solve(start)
        val endPoints = solve(end)
        val startHour = getHour(start)
        val endHour = getHour(end)
        Log.e(TAG, "startHour: $startHour  endHour: $endHour")

        val outerPoints = mutableListOf<String>()
        val innerPoints = mutableListOf<String>()
        outerPoints.add(startPoints[0])
        innerPoints.add(startPoints[1])
        if (startHour != endHour) {
            for (i in startHour + 1..endHour) {
                outerPoints.add( mOuterHoursList[i])
                Log.e(TAG, "outerPoints: " + mOuterHoursList[i])
                innerPoints.add(mInnerHoursList[i])
            }
        }
        outerPoints.add(endPoints[0])
        innerPoints.add(endPoints[1])
        innerPoints.reverse()
        outerPoints.addAll(innerPoints)
        val path = Path()
        path.addPath(genPathFromList(outerPoints))
        path.fillType = Path.FillType.EVEN_ODD
        val shapeHigh = ShapeDrawable(PathShape(path, L.toFloat(), L.toFloat()))
        shapeHigh.paint.color = Color.YELLOW
        shapeHigh.alpha = (0.1 * 255).toInt()
        shapeHigh.paint.style = Paint.Style.FILL_AND_STROKE
        shapeHigh.paint.strokeWidth = 12.0f
        shapeHigh.paint.strokeCap = Paint.Cap.ROUND
        shapeHigh.paint.strokeJoin = Paint.Join.ROUND
        return shapeHigh
    }

    fun getArmShape(instant: Instant): ShapeDrawable {
        // create a path basic path
        val intersectionPoints = solve(instant)
        val s = "M " + intersectionPoints[0] + " L " + intersectionPoints[1]
        val path = Path()
        path.addPath(PathParser.createPathFromPathData(s))
        val shapePath = ShapeDrawable(PathShape(path, L.toFloat(), L.toFloat()))
        shapePath.paint.color = Color.BLUE
        shapePath.alpha = (1.0 * 255).toInt()
        shapePath.paint.style = Paint.Style.FILL_AND_STROKE
        shapePath.paint.strokeWidth = 20.0f
        shapePath.paint.strokeCap = Paint.Cap.ROUND
        shapePath.paint.strokeJoin = Paint.Join.ROUND
        return shapePath
    }

    fun getRayShape(instant: Instant): ShapeDrawable {
        val angle = getAngle(instant)
        val x = (C + C * Math.sin(angle)).toInt()
        val y = (C + C * Math.cos(angle)).toInt()
        val s = "M $C,$C L $x,$y"
        Log.e(TAG, "getArm: $s")
        val path = Path()
        path.addPath(PathParser.createPathFromPathData(s))
        val shapePath = ShapeDrawable(PathShape(path, L.toFloat(), L.toFloat()))
        shapePath.paint.color = Color.RED
        shapePath.alpha = (1.0 * 255).toInt()
        shapePath.paint.style = Paint.Style.FILL_AND_STROKE
        shapePath.paint.strokeWidth = 12.0f
        shapePath.paint.strokeCap = Paint.Cap.ROUND
        shapePath.paint.strokeJoin = Paint.Join.ROUND
        return shapePath
    }

    private val hoursTrackShape: ShapeDrawable
        get() {
            // create a path basic path
            val shapePath =
                ShapeDrawable(PathShape(myPathStore.mHoursTrack, L.toFloat(), L.toFloat()))
            shapePath.paint.color = Color.WHITE
            shapePath.alpha = (0.1 * 255).toInt()
            shapePath.paint.style = Paint.Style.FILL
            shapePath.paint.strokeWidth = 12.0f
            shapePath.paint.strokeCap = Paint.Cap.ROUND
            shapePath.paint.strokeJoin = Paint.Join.ROUND
            return shapePath
        }

    fun getRelevantTileShape(instant: Instant): ShapeDrawable {
        val hour = getHour(instant)
        val path = Path()
        path.addPath(PathParser.createPathFromPathData("M " + mOuterHoursList[hour] + " L " + mOuterHoursList[hour + 1]))
        path.addPath(PathParser.createPathFromPathData("M " + mInnerHoursList[hour] + " L " + mInnerHoursList[hour + 1]))
        //path.addPath( PathParser.createPathFromPathData("M " + myDataLists.mOuterHoursList.get(hour) + " L " + myDataLists.mInnerHoursList.get(hour)));
        //path.addPath( PathParser.createPathFromPathData("M " + myDataLists.mOuterHoursList.get(hour+ 1) + " L " + myDataLists.mInnerHoursList.get(hour + 1)));
        path.fillType = Path.FillType.EVEN_ODD
        val shapePath = ShapeDrawable(PathShape(path, L.toFloat(), L.toFloat()))
        shapePath.paint.color = Color.RED
        shapePath.alpha = (1.0 * 255).toInt()
        shapePath.paint.style = Paint.Style.FILL_AND_STROKE
        shapePath.paint.strokeWidth = 12.0f
        shapePath.paint.strokeCap = Paint.Cap.ROUND
        shapePath.paint.strokeJoin = Paint.Join.ROUND
        return shapePath
    }

    inner class MyPathStore {
        var mHoursTrack: Path

        init {
            val track =  mutableListOf<String>()
            track.addAll(mOuterHoursList)
            track.addAll(mInnerHoursList)
            mHoursTrack = genPathFromList(track)
            mHoursTrack.fillType = Path.FillType.EVEN_ODD
        }
    }

    fun genPathFromList(inputList: List<String>): Path {
        val list = mutableListOf<String>()
        list.addAll( inputList)
        list.add(0, "M")
        list.add(2, "L")
        return PathParser.createPathFromPathData(list.joinToString(" "))
    }



    companion object {
        val TAG = SpiralClock::class.java.simpleName
        const val L = 2540
        const val C = L / 2
    }
}