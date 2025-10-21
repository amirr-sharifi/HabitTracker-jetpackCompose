package af.amir.mytasky.presentation.timer

import android.graphics.*
fun createBitmapCircle(
    progress : Int ,
    size : Int ,//in pixels
    strokeWidth : Float,
    foregroundColor : Int ,
    backgroundColor : Int
) : Bitmap{
    val bitmap = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        isAntiAlias=true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        this.strokeWidth = strokeWidth
    }

    val rect = RectF(strokeWidth/2,strokeWidth/2,size- strokeWidth/2,size- strokeWidth/2)
    paint.color  = backgroundColor
    canvas.drawArc(rect,0f,360f,false,paint)

    paint.color = foregroundColor
    val sweepAngle = progress * 3.6f
    canvas.drawArc(rect,-90f,sweepAngle,false,paint)

    return bitmap
}