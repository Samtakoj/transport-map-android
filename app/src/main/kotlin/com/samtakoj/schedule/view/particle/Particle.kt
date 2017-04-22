package com.samtakoj.schedule.view.particle

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.View
import java.util.ArrayList

/**
 * Created by artsiom.chuiko on 23/04/2017.
 */
data class Particle(val x: Float, val y: Float, val radius: Float)

class LineEvaluator: TypeEvaluator<Particle> {

    override fun evaluate(fraction: Float, startValue: Particle, endValue: Particle): Particle {
        return Particle(startValue.x + (endValue.x - startValue.x) * fraction,
                startValue.y + (endValue.y - startValue.y) * fraction,
                startValue.radius + (endValue.radius - startValue.radius) * fraction)
    }
}

class ParticleView(context: Context) : View(context) {

    private val STATUS_MOTIONLESS = 0
    private val STATUS_PARTICLE_GATHER = 1
    private val STATUS_TEXT_MOVING = 2

    private val ROW_NUM = 10
    private val COLUMN_NUM = 10

    private val DEFAULT_MAX_TEXT_SIZE = sp2px(80f)
    private val DEFAULT_MIN_TEXT_SIZE = sp2px(30f)

    val DEFAULT_TEXT_ANIM_TIME = 1000
    val DEFAULT_SPREAD_ANIM_TIME = 200
    val DEFAULT_HOST_TEXT_ANIM_TIME = 900

    private var hostTextPaint: Paint = Paint()
    private var particleTextPaint: Paint = Paint()
    private var circlePaint: Paint = Paint()
    private var hostBgPaint: Paint = Paint()
    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private val particles = Array(ROW_NUM) { arrayOfNulls<Particle>(COLUMN_NUM) }
    private val minParticles = Array(ROW_NUM) { arrayOfNulls<Particle>(COLUMN_NUM) }

    var bgColor: Int = 0
    var particleColor: Int = 0
    var particleTextSize = DEFAULT_MIN_TEXT_SIZE

    private var mStatus = STATUS_MOTIONLESS

    private lateinit var particleAnimListener: () -> Unit

    var particleText: String = ""
    var hostText: String = ""
    private var spreadWidth: Float = 0.toFloat()
    private var hostRectWidth: Float = 0.toFloat()
    private var particleTextX: Float = 0.toFloat()
    private var hostTextX: Float = 0.toFloat()

    //Text anim time in milliseconds
    private var textAnimTime: Int = 0
    //Spread anim time in milliseconds
    private var spreadAnimTime: Int = 0
    //HostText anim time in milliseconds
    private var hostTextAnimTime: Int = 0

    private var startMaxP: PointF? = null
    private var endMaxP: PointF? = null
    private var startMinP: PointF? = null
    private var endMinP: PointF? = null

    init {
        particleTextSize = DEFAULT_MIN_TEXT_SIZE
        val hostTextSize = DEFAULT_MIN_TEXT_SIZE
        bgColor = 0xFF0867AB.toInt()
        particleColor = 0xFFCEF4FD.toInt()
        textAnimTime = DEFAULT_TEXT_ANIM_TIME
        spreadAnimTime = DEFAULT_SPREAD_ANIM_TIME
        hostTextAnimTime = DEFAULT_HOST_TEXT_ANIM_TIME

        hostTextPaint.isAntiAlias = true
        hostTextPaint.textSize = hostTextSize.toFloat()

        particleTextPaint.isAntiAlias = true
        circlePaint.isAntiAlias = true
        hostBgPaint.isAntiAlias = true
        hostBgPaint.textSize = hostTextSize.toFloat()

        particleTextPaint.textSize = particleTextSize.toFloat()
        circlePaint.textSize = particleTextSize.toFloat()

        particleTextPaint.color = bgColor
        hostTextPaint.color = bgColor
        circlePaint.color = particleColor
        hostBgPaint.color = particleColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h

        startMinP = PointF((mWidth / 2).toFloat() - getTextWidth(particleText, particleTextPaint) / 2f - dip2px(4f).toFloat(), mHeight / 2 + getTextHeight(hostText, hostTextPaint) / 2 - getTextHeight(particleText, particleTextPaint) / 0.7f)
        endMinP = PointF((mWidth / 2).toFloat() + getTextWidth(particleText, particleTextPaint) / 2f + dip2px(10f).toFloat(), mHeight / 2 + getTextHeight(hostText, hostTextPaint) / 2)

        for (i in 0..ROW_NUM - 1) {
            for (j in 0..COLUMN_NUM - 1) {
                minParticles[i][j] = Particle(startMinP!!.x + (endMinP!!.x - startMinP!!.x) / COLUMN_NUM * j, startMinP!!.y + (endMinP!!.y - startMinP!!.y) / ROW_NUM * i, dip2px(0.8f).toFloat())
            }
        }

        startMaxP = PointF((mWidth / 2 - DEFAULT_MAX_TEXT_SIZE).toFloat(), (mHeight / 2 - DEFAULT_MAX_TEXT_SIZE).toFloat())
        endMaxP = PointF((mWidth / 2 + DEFAULT_MAX_TEXT_SIZE).toFloat(), (mHeight / 2 + DEFAULT_MAX_TEXT_SIZE).toFloat())

        for (i in 0..ROW_NUM - 1) {
            for (j in 0..COLUMN_NUM - 1) {
                particles[i][j] = Particle(startMaxP!!.x + (endMaxP!!.x - startMaxP!!.x) / COLUMN_NUM * j, startMaxP!!.y + (endMaxP!!.y - startMaxP!!.y) / ROW_NUM * i, getTextWidth(hostText + particleText, particleTextPaint) / (COLUMN_NUM * 1.8f))
            }
        }

        val linearGradient = LinearGradient(mWidth / 2 - getTextWidth(particleText, circlePaint) / 2f,
                mHeight / 2 - getTextHeight(particleText, circlePaint) / 2,
                mWidth / 2 - getTextWidth(particleText, circlePaint) / 2,
                mHeight / 2 + getTextHeight(particleText, circlePaint) / 2,
                intArrayOf(particleColor, Color.argb(120, getR(particleColor), getG(particleColor), getB(particleColor))), null, Shader.TileMode.CLAMP)
        circlePaint.shader = linearGradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mStatus == STATUS_PARTICLE_GATHER) {
            for (i in 0..ROW_NUM - 1) {
                for (j in 0..COLUMN_NUM - 1) {
                    canvas.drawCircle(particles[i][j]!!.x, particles[i][j]!!.y, particles[i][j]!!.radius, circlePaint)
                }
            }
        }

        if (mStatus == STATUS_TEXT_MOVING) {
            canvas.drawText(hostText, hostTextX, mHeight / 2 + getTextHeight(hostText, hostBgPaint) / 2, hostBgPaint)
            canvas.drawRect(hostTextX + hostRectWidth, mHeight / 2 - getTextHeight(hostText, hostBgPaint) / 1.2f, hostTextX + getTextWidth(hostText, hostTextPaint), mHeight / 2 + getTextHeight(hostText, hostBgPaint) / 1.2f, hostTextPaint)
        }

        if (mStatus == STATUS_PARTICLE_GATHER) {
            canvas.drawRoundRect(RectF(mWidth / 2 - spreadWidth, startMinP!!.y, mWidth / 2 + spreadWidth, endMinP!!.y), dip2px(2f).toFloat(), dip2px(2f).toFloat(), hostBgPaint)
            canvas.drawText(particleText, mWidth / 2 - getTextWidth(particleText, particleTextPaint) / 2, startMinP!!.y + (endMinP!!.y - startMinP!!.y) / 2 + getTextHeight(particleText, particleTextPaint) / 2, particleTextPaint)
        } else if (mStatus == STATUS_TEXT_MOVING) {
            canvas.drawRoundRect(RectF(particleTextX - dip2px(4f), startMinP!!.y, particleTextX + getTextWidth(particleText, particleTextPaint) + dip2px(4f).toFloat(), endMinP!!.y), dip2px(2f).toFloat(), dip2px(2f).toFloat(), hostBgPaint!!)
            canvas.drawText(particleText, particleTextX, startMinP!!.y + (endMinP!!.y - startMinP!!.y) / 2 + getTextHeight(particleText, particleTextPaint) / 2, particleTextPaint)
        }

    }

    private fun startParticleAnim() {

        mStatus = STATUS_PARTICLE_GATHER

        val animList = ArrayList<Animator>()

        val textAnim = ValueAnimator.ofInt(DEFAULT_MAX_TEXT_SIZE, particleTextSize)
        textAnim.duration = (textAnimTime * 0.8f).toInt().toLong()
        textAnim.addUpdateListener { valueAnimator ->
            val textSize = valueAnimator.animatedValue as Int
            particleTextPaint.textSize = textSize.toFloat()
        }
        animList.add(textAnim)

        for (i in 0..ROW_NUM - 1) {
            for (j in 0..COLUMN_NUM - 1) {
                val tempI = i
                val tempJ = j
                val animator = ValueAnimator.ofObject(LineEvaluator(), particles[i][j], minParticles[i][j])
                animator.duration = (textAnimTime + (textAnimTime * 0.02f).toInt() * i + (textAnimTime * 0.03f).toInt() * j).toLong()
                animator.addUpdateListener { animation ->
                    particles[tempI][tempJ] = animation.animatedValue as Particle
                    if (tempI == ROW_NUM - 1 && tempJ == COLUMN_NUM - 1) {
                        invalidate()
                    }
                }
                animList.add(animator)
            }
        }

        val set = AnimatorSet()
        set.playTogether(animList)
        set.start()

        set.addListener(object : AnimListener() {
            override fun onAnimationEnd(animation: Animator) {
                startSpreadAnim()
            }
        })

    }

    private fun startSpreadAnim() {
        val animator = ValueAnimator.ofFloat(0F, getTextWidth(particleText, particleTextPaint) / 2 + dip2px(4f))
        animator.duration = spreadAnimTime.toLong()
        animator.addUpdateListener { animation ->
            spreadWidth = animation.animatedValue as Float
            invalidate()
        }
        animator.addListener(object : AnimListener() {
            override fun onAnimationEnd(animation: Animator) {
                startHostTextAnim()
            }
        })
        animator.start()
    }

    private fun startHostTextAnim() {
        mStatus = STATUS_TEXT_MOVING

        val animList = ArrayList<Animator>()

        val particleTextXAnim = ValueAnimator.ofFloat(startMinP!!.x + dip2px(4f), mWidth / 2 - (getTextWidth(hostText, hostTextPaint) + getTextWidth(particleText, particleTextPaint)) / 2 + getTextWidth(hostText, hostTextPaint))
        particleTextXAnim.addUpdateListener { animation -> particleTextX = animation.animatedValue as Float }
        animList.add(particleTextXAnim)

        val animator = ValueAnimator.ofFloat(0F, getTextWidth(hostText, hostTextPaint))
        animator.addUpdateListener { animation -> hostRectWidth = animation.animatedValue as Float }
        animList.add(animator)

        val hostTextXAnim = ValueAnimator.ofFloat(startMinP!!.x, mWidth / 2 - (getTextWidth(hostText, hostTextPaint) + getTextWidth(particleText, particleTextPaint) + dip2px(20f).toFloat()) / 2)
        hostTextXAnim.addUpdateListener { animation ->
            hostTextX = animation.animatedValue as Float
            invalidate()
        }
        animList.add(hostTextXAnim)

        val set = AnimatorSet()
        set.playTogether(animList)
        set.duration = hostTextAnimTime.toLong()
        set.addListener(object : AnimListener() {
            override fun onAnimationEnd(animation: Animator) {
                if (null != particleAnimListener) {
                    particleAnimListener.invoke()
                }
            }
        })
        set.start()

    }

    fun startAnim() {
        post { startParticleAnim() }
    }

    private abstract inner class AnimListener : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {

        }

        override fun onAnimationCancel(animation: Animator) {

        }

        override fun onAnimationRepeat(animation: Animator) {

        }
    }

    fun setOnParticleAnimListener(particleAnimListener: () -> Unit) {
        this.particleAnimListener = particleAnimListener
    }

    private fun dip2px(dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    private fun sp2px(spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    private fun getTextHeight(text: String, paint: Paint): Float {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height() / 1.1f
    }

    private fun getTextWidth(text: String, paint: Paint): Float {
        return paint.measureText(text)
    }

    private fun getR(color: Int): Int {
        val r = color shr 16 and 0xFF
        return r
    }

    private fun getG(color: Int): Int {
        val g = color shr 8 and 0xFF
        return g
    }

    private fun getB(color: Int): Int {
        val b = color and 0xFF
        return b
    }
}