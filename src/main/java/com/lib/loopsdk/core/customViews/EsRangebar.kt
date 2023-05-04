package com.lib.loopsdk.core.customViews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.google.gson.reflect.TypeToken
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.EsRangebarLayoutBinding


/**
 * TODO: document your custom view class.
 */
class EsRangebar : LinearLayout {

    private var startRange = 0
    private var endRange = 1000
    var leftSeekBar: com.jaygoo.widget.SeekBar? = null
    var rightSeekBar: com.jaygoo.widget.SeekBar? = null
    private var indicatorView : View? = null
    private var isRightDrag = false
    private var minValue = 0f
    private var maxValue = 1000f
    private var padding = 0
    private var indicatorMinValue = minValue - minValue
    private var indicatorMaxValue = maxValue - minValue
    private var uMin = indicatorMinValue
    private var uMax = indicatorMaxValue
    private var isRewards = false
    private lateinit var binding: EsRangebarLayoutBinding
    var listener: OnRangeUpdatedListener? = null

    interface OnRangeUpdatedListener {
        fun onRangeUpdated(minValue: Int, maxValue: Int)
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = EsRangebarLayoutBinding.inflate(inflater, this, true)
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
    }


    fun doTheMagicIn(context: Context, originalMinValue: Int, originalMaxValue: Int, uMin: Int, uMax: Int, indicatorLayoutStart : Int = R.layout.indicator_start, indicatorLayoutEnd : Int = R.layout.indicator_end){
        this.uMin = (uMin-originalMinValue).toFloat()
        this.uMax = (uMax-originalMinValue).toFloat()
        binding.startBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (isRightDrag) {
//                    indicatorView = LayoutInflater.from(context).inflate(indicatorLayoutEnd, null, false)
//                    endRange = i
//                    seekBar.thumb = getThumb(i, indicatorView!!)
//                    if (endRange in (indicatorMinValue.toInt() + 1)..indicatorMaxValue.toInt()) {
//                        binding.rangeBar.setValue(startRange.toFloat(), endRange.toFloat())
//                    }
                } else {
//                    indicatorView = LayoutInflater.from(context).inflate(indicatorLayoutStart, null, false)
//                    startRange = i
//                    seekBar.thumb = getThumb(i, indicatorView!!)
//                    if (startRange in (indicatorMinValue.toInt() + 1)..indicatorMaxValue.toInt()) {
//                        binding.rangeBar.setValue(startRange.toFloat(), endRange.toFloat())
//                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
        binding.rangeBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                if (isRightDrag || rightValue.toInt() != endRange) {
                    if (rightValue.toInt() > leftValue) {
//                        binding.startBar.progress = rightValue.toInt()
                        //this@EsRangebar.uMax = rightValue
                        val paddingFactor = if (rightValue.toInt().toString().length >= 4) {
                            1.7
                        } else {
                            1.5
                        }
//                        binding.startBar.post {
//                            if (rightValue.toInt() - indicatorMinValue > (indicatorMinValue + indicatorMaxValue)/4) {
//                                binding.startBar.setPadding(0, 0, (padding * paddingFactor).toInt(), 0)
//                            } else {
//                                binding.startBar.setPadding((padding * 1.5).toInt(), 0, (padding * paddingFactor).toInt(), 0)
//                            }
//                        }
                        listener?.onRangeUpdated((leftValue+minValue).toInt(), (rightValue+minValue).toInt())
                    }
                } else {
                    if (rightValue.toInt() > leftValue) {
//                        binding.startBar.progress = leftValue.toInt()
                        //this@EsRangebar.uMin = leftValue
                        val paddingFactor = if (leftValue.toInt().toString().length >= 4) {
                            1.7
                        } else {
                            1.5
                        }
                        binding.startBar.post {
                            if (indicatorMaxValue - leftValue.toInt() > (indicatorMinValue + indicatorMaxValue)/4) {
//                                binding.startBar.setPadding(padding, 0, 0, 0)
                            } else {
//                                binding.startBar.setPadding(padding, 0, (padding * paddingFactor).toInt(), 0)
                            }
                        }
                        listener?.onRangeUpdated((leftValue+minValue).toInt(), (rightValue+minValue).toInt())
                    }
                }
                binding.startBar.visibility = View.GONE
            }

            override fun onStartTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
                isRightDrag = !isLeft
            }
            override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
                isRightDrag = !isLeft
                binding.startBar.visibility = View.INVISIBLE
            }
        })

        binding.rangeBar.seekBarMode = RangeSeekBar.SEEKBAR_MODE_RANGE


        leftSeekBar = binding.rangeBar.leftSeekBar
        rightSeekBar = binding.rangeBar.rightSeekBar

        leftSeekBar!!.setIndicatorTextStringFormat("$ %s")
        leftSeekBar!!.setIndicatorTextDecimalFormat("0")
        rightSeekBar!!.setIndicatorTextStringFormat("$ %s")
        rightSeekBar!!.setIndicatorTextDecimalFormat("0")

        isRightDrag = false
//        indicatorView = if (isRightDrag) {
//            LayoutInflater.from(context).inflate(indicatorLayoutEnd, null, false)
//        } else {
//            LayoutInflater.from(context).inflate(indicatorLayoutStart, null, false)
//        }
        initializeRanges(originalMinValue, originalMaxValue)
        // to invalidate the change of the thumb after the first initialization
//        startBar.progress = startBar.progress + 1
//        endBar.progress = endBar.progress + 1
    }

    private fun initializeRanges(originalMinValue: Int, originalMaxValue: Int) {
        maxValue = originalMaxValue.toFloat()
        minValue = originalMinValue.toFloat()

        indicatorMinValue = minValue - minValue
        indicatorMaxValue = maxValue - minValue
        startRange = uMin.toInt()
        endRange = uMax.toInt()
        if(indicatorMaxValue==0F){
            indicatorMaxValue = 1.0F
        }
        binding.startBar.max = indicatorMaxValue.toInt()
        binding.rangeBar.setRange(indicatorMinValue,indicatorMaxValue)
        binding.rangeBar.setValue(uMin, uMax)
        binding.startBar.progress = uMin.toInt()
        binding.rangeBar.postInvalidate()
        //chagneThumbColor()


//        val drawable: Drawable = context.getResources().getDrawable(R.drawable.rangebar_progress_thumb)
//        val primaryColor: Int= Color.parseColor(binding.brandThemeColors!!.PRIMARY_BRAND_COLOR)
//        drawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(primaryColor,
//            BlendModeCompat.SRC_IN)
//
//        val mDrawableName = "myimg"
//        val resID = resources.getIdentifier(mDrawableName, drawable.toString(), context.opPackageName)


//        val id = binding.rangeBar.rightSeekBar.thumbDrawableId
//        val drawable2 = resources.getDrawable(id)
//        drawable2.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))

//        view.getDrawable().mutate().setColorFilter(0xff777777, PorterDuff.Mode.MULTIPLY);
//
//        binding.ivCouponStatus.setColorFilter(ContextCompat.getColor(binding.root.context,R.color.info_blue), PorterDuff.Mode.SRC_IN)

//        DrawableCompat.setTint(context.getResources().getDrawable(R.drawable.rangebar_progress_thumb),ContextCompat.getColor(context,primaryColor))
 //       binding.rangeBar.rightSeekBar.thumbDrawableId=id
//        binding.rangeBar.leftSeekBar.thumbDrawableId=R.drawable.rangebar_progress_thumb
//        binding.rangeBar.leftSeekBar.thumbDrawableId=id


    }
    private fun chagneThumbColor(){
        val id = binding.rangeBar.rightSeekBar!!.thumbDrawableId
        val leftId = binding.rangeBar.leftSeekBar!!.thumbDrawableId
        val drawable2 = resources.getDrawable(id)
        val drawableLeft = resources.getDrawable(leftId)
        drawable2.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
        drawableLeft.setTint(Color.parseColor(Colors.PRIMARY_BRAND_COLOR))
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        val padding = indicatorView!!.width / 2
//        startBar.max = 1000
//        endBar.max = 1000
//        rangeBar.setRange(0f,1000f)
//        startBar.setPadding(padding / 2, 0, padding, 0)
//        endBar.setPadding(padding / 2, 0, padding, 0)
//        rangeBar.setPadding(padding / 2, 0, padding, 0)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
//        padding = indicatorView!!.width / 2
//        initializeRanges(minValue.toInt(), maxValue.toInt())
//        binding.rangeBar.setPadding(padding , 0, padding, 0)
//        binding.startBar.visibility = View.INVISIBLE
    }

    fun getThumb(progress: Int, indicator: View): Drawable {
        val trueProgress: Int = (progress+minValue).toInt()
        (indicator.findViewById<View>(R.id.progressBar) as TextView).text = "$trueProgress"
        if (isRewards) {
            (indicator.findViewById<View>(R.id.ivClose) as AppCompatImageView).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_points_filled))
        } else {
            (indicator.findViewById<View>(R.id.ivClose) as AppCompatImageView).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_points_filled))
        }

        indicator.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(indicator.measuredWidth, indicator.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        indicator.layout(0, 0, indicator.measuredWidth, indicator.measuredHeight)
        indicator.draw(canvas)

        return BitmapDrawable(resources, bitmap)
    }

}

