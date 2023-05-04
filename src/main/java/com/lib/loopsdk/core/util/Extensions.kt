package com.lib.loopsdk.core.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.Spanned
import android.text.TextPaint
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.setMargins
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.LayoutViewOnlineBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.sufficientlysecure.htmltextview.HtmlFormatter
import org.sufficientlysecure.htmltextview.HtmlFormatterBuilder
import org.sufficientlysecure.htmltextview.HtmlResImageGetter
import timber.log.Timber
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.cos
import kotlin.math.sin


fun Context.showToast(message: String, time: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, time).show()
}

fun View.showView() {
    this.visibility = View.VISIBLE
}

fun View.hideView() {
    this.visibility = View.GONE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun View.invisibleView() {
    this.visibility = View.INVISIBLE
}

fun getGradientDrawable(
    colorStart: Int,
    colorEnd: Int,
    orientation: GradientDrawable.Orientation,
    radius: Int,
    myContext: Context,
): GradientDrawable {
    val gradientDrawable = GradientDrawable(orientation, intArrayOf(colorEnd, colorStart))
    gradientDrawable.cornerRadius = Utils.dpToPx(myContext, radius).toFloat()
    return gradientDrawable
}


fun View.setCustomDrawableWithColor(@ColorInt color: Int) {
    when (val tagBackground = this.background?.mutate()) {
        is ShapeDrawable -> {
            tagBackground.paint.color = color
        }
        is GradientDrawable -> {
            tagBackground.setColor(color)
        }
        is ColorDrawable -> {
            tagBackground.color = color
        }
    }
}

fun TextView.setDrawableTintColor(drawablePosition: Int, color: Int) {
    this.compoundDrawablesRelative.getOrNull(drawablePosition)?.mutate()?.setTint(color)
}

/**
 * In some cases {@link com.unifynd.lite.frt.core.views.utils.Extensions#setDrawableTintColor()}
 * does not work as 'compoundDrawablesRelative' returns empty array
 */
fun TextView.setDrawableTintColorAlt(drawablePosition: Int, color: Int) {
    this.compoundDrawables.getOrNull(drawablePosition)?.mutate()?.setTint(color)
}


fun getDateOfFormat(calendar: Calendar, pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
    return sdf.format(calendar.time)
}


fun getCalendarFromString(dateString: String, pattern: String): Calendar {
    val calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
    calendar.time = sdf.parse(dateString)!!
    return calendar
}

fun getCalendarFromStringWithCurrentDate(dateString: String, pattern: String): Calendar{
    val calendar = Calendar.getInstance()
    val tempCalendar = Calendar.getInstance()
    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
    tempCalendar.time = sdf.parse(dateString)!!
    calendar.set(calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DATE),
        tempCalendar.get(Calendar.HOUR_OF_DAY),
        tempCalendar.get(Calendar.MINUTE),
        tempCalendar.get(
            Calendar.SECOND))
    return calendar
}

fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager: InputMethodManager = activity.getSystemService(
        Activity.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    val view = activity.currentFocus
    if (view != null) {
        inputMethodManager.hideSoftInputFromWindow(
            view.windowToken, 0
        )
    }
}

fun openMallChangeBottomSheet(context: Context, activity: Activity){

}

fun Context.getPackageNameBasedOnVariant(isChennaiMall: Boolean = false): String {
    return if (isChennaiMall)
        "com.phoenix.nhance"
    else
        this.packageName
}


/**
 * Return true if this [Context] is available.
 * Availability is defined as the following:
 * + [Context] is not null
 * + [Context] is not destroyed (tested with [FragmentActivity.isDestroyed] or [Activity.isDestroyed])
 */
fun Context?.isAvailable(): Boolean {
    if (this == null) {
        return false
    } else if (this !is Application) {
        if (this is FragmentActivity) {
            return !this.isDestroyed
        } else if (this is Activity) {
            return !this.isDestroyed
        }
    }
    return true
}

@SuppressLint("SimpleDateFormat")
@Throws(IOException::class)
fun Context.createFile(isTemporary: Boolean = true, extension: String): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    var imageFileName = "FILE_$timeStamp"
    if (isTemporary) {
        imageFileName = "TMP_FILE"
    }
    val storageDir = cacheDir
    val image = File(storageDir, "$imageFileName.$extension")
    Log.i("********image", image.absolutePath)
    return image
}

fun Bitmap.toFile(context: Context, storeExternal: Boolean = false) : File? {
    val timeStamp = System.currentTimeMillis()/1000
    val filesDir: File
    var imageFile: File? = null
    val os: OutputStream
    try {
        filesDir = if (storeExternal) context.externalCacheDir ?: context.cacheDir else context.cacheDir
        imageFile = File(filesDir, "$timeStamp.jpg")
        os = FileOutputStream(imageFile)
        this.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.flush()
        os.close()
    } catch (e: Exception) {
        //Timber.e(e, "Error writing bitmap")
    }
    return imageFile
}


fun Context.areNotificationsEnabled(): Boolean {
    return NotificationManagerCompat.from(this).areNotificationsEnabled()
}

fun View.scaleView(fromY: Float, toY: Float, fromX: Float, toX: Float) {
    val anim = ScaleAnimation(
        fromX, toX,  // Start and end values for the X axis scaling
        fromY, toY,  // Start and end values for the Y axis scaling
        Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
        Animation.RELATIVE_TO_SELF, 0.5f) // Pivot point of Y scaling
    anim.fillAfter = true // Needed to keep the result of the animation
    anim.duration = 300
    this.startAnimation(anim)
}

fun isHoli(): Boolean {
    val start = Calendar.getInstance()
    start.set(Calendar.DAY_OF_MONTH, 25)
    start.set(Calendar.MONTH, Calendar.MARCH)
    start.set(Calendar.YEAR, 2021)
    start.set(Calendar.HOUR_OF_DAY, 0)
    start.set(Calendar.MINUTE, 0)
    start.set(Calendar.SECOND, 0)
    val endDate = Calendar.getInstance()
    endDate.set(Calendar.DAY_OF_MONTH, 30)
    endDate.set(Calendar.MONTH, Calendar.MARCH)
    endDate.set(Calendar.YEAR, 2021)
    endDate.set(Calendar.HOUR_OF_DAY, 0)
    endDate.set(Calendar.MINUTE, 0)
    endDate.set(Calendar.SECOND, 0)
    return Calendar.getInstance().time.after(start.time) && Calendar.getInstance().time.before(endDate.time)
}


fun Number?.toBoolean(): Boolean {
    if (this == null) return false
    return this.toInt() >= 1
}

fun Boolean?.toInt(): Int {
    if (this == null) return 0
    return if(this) 1 else 0
}

fun RecyclerView?.getCurrentPosition() : Int {
    return (this?.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
}

inline fun <reified T> Flow<T>.observeOnLifecycle(
    lifecycleOwner: LifecycleOwner,
    noinline collector: suspend (T) -> Unit
) = FlowObserver(lifecycleOwner, this, collector)

inline fun <reified T> Flow<T>.observeInLifecycle(
    lifecycleOwner: LifecycleOwner
) = FlowObserver(lifecycleOwner, this, {})

class FlowObserver<T> (
    lifecycleOwner: LifecycleOwner,
    private val flow: Flow<T>,
    private val collector: suspend (T) -> Unit
) {

    private var job: Job? = null

    init {
        lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver {
                source: LifecycleOwner, event: Lifecycle.Event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    job = source.lifecycleScope.launch {
                        flow.collect {
                            collector(it) }
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    job?.cancel()
                    job = null
                }
                else -> { }
            }
        })
    }
}


fun getFormattedPoints(points : String): String? {
    val roundedBalance: Int = points.split(".")[0].toInt()
    val formatter = DecimalFormat("##,##,###")
    return formatter.format(roundedBalance)
}
fun getBitmapFromURL(src: String?): Bitmap? {
    return try {
        val url = URL(src)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.setDoInput(true)
        connection.connect()
        val input: InputStream = connection.getInputStream()
        BitmapFactory.decodeStream(input)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun Drawable?.applyGradient(colorType: ThemeColorUtils.ColorTypes): Bitmap? {
    if (this == null) return null
    return try {
        this.clearColorFilter()
        val colorIntList = ThemeColorUtils.convertColorListToColorIntList(colorType.defaultValues)
        val angle = colorType.angle
        val originalBitmap = this.mutate().toBitmap()
        val width: Int = originalBitmap.width
        val height: Int = originalBitmap.height
        val updatedBitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(updatedBitmap)
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)
        val paint = Paint()
        val angleInRadians = Math.toRadians(angle).toFloat()
        val endX = sin(angleInRadians) * width
        val endY = cos(angleInRadians) * height
        var floatArray: FloatArray? = colorType.positions
        if (floatArray == null) {
            floatArray = FloatArray(colorIntList.size)
            floatArray.forEachIndexed { index, fl ->
                floatArray[index] = (index.toFloat()) / colorIntList.size
            }
        }
        val shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),colorIntList,floatArray , Shader.TileMode.REPEAT)
        paint.shader = shader
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        updatedBitmap
    } catch (e: Exception) {
        Timber.e(e)
        null
    }
}
fun AppCompatTextView.setGradientColor(){
    val paint: TextPaint = paint
    val width: Float = paint.measureText(text.toString())

    val textShader: Shader = LinearGradient(
        0f, 0f, width, paint.textSize, intArrayOf(
            Color.parseColor("#C5C5C5"),
            Color.parseColor("#94B2B2B2"),
        ), null, Shader.TileMode.CLAMP
    )
    setTextColor(resources.getColor(R.color.white))
    paint.shader = textShader
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }

}

fun Context.getHTMLTagsToSpannedString(source: String): Spanned {
    return HtmlFormatter.formatHtml(
        HtmlFormatterBuilder().setHtml(source).setImageGetter(
            HtmlResImageGetter(
                this)
        ))
}

fun String.isValidEmail(): Boolean {
    val regex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
    val pattern = Pattern.compile(regex)
    return !isNullOrEmpty() && pattern.matcher(this).matches()
}


fun String.getDateSuffix(fromFormat: String = Constants.API_DATE_FORMAT): String {
    val calendar = getCalendarFromString(this, fromFormat)
    val date = calendar.get(Calendar.DAY_OF_MONTH)
    var suffix = "th"
    if (date !in 11..13) {
        suffix = when(date % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
    return suffix
}

fun ProgressBar.setProgressTheme(
    backGroundColorType: Int,
    progressColorType: Int,
) {
    val progressBarDrawable: LayerDrawable = this.progressDrawable as LayerDrawable
    val backgroundDrawable = progressBarDrawable.getDrawable(0).mutate() as Drawable
    val progressDrawable  = progressBarDrawable.getDrawable(1).mutate() as Drawable
//    val intProgressColors = ThemeColorUtils.convertColorListToColorIntList(progressColorType.defaultValues)
//    val intBgColors = ThemeColorUtils.convertColorListToColorIntList(backGroundColorType.defaultValues)
    if (false) {
        //progressDrawable.colors = intProgressColors
    } else {
        progressDrawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(progressColorType, BlendModeCompat.SRC_IN)
    }
    if (false) {
        //backgroundDrawable.colors = intBgColors
    } else {
        backgroundDrawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(backGroundColorType,
            BlendModeCompat.SRC_IN)
    }

}

fun Toast.showCustomToast(myContext:Context, message: String, imageIcon: Int, activity: Activity): Toast {
    try {
        val layout = activity.layoutInflater.inflate (
            R.layout.custom_toast_survey,
            activity.findViewById(R.id.toast_container)
        )

        // set the text of the TextView of the message
        val textView = layout.findViewById<AppCompatTextView>(R.id.tvMessage)
        textView.text = message

        // set the image of the ImageView of the message
        val imageView = layout.findViewById<AppCompatImageView>(R.id.ivIcon)
        imageView.setImageDrawable(
            ContextCompat.getDrawable(
                myContext,
                imageIcon
            )
        )
        // use the application extension function
        this.setGravity(Gravity.BOTTOM, 0, 40)
        this.duration = Toast.LENGTH_SHORT
        this.view = layout
        return this
    }catch (e: Exception){
        Timber.d("Exception toast: ${e.message} ")
        return Toast(myContext)
    }


}

fun Context.startSharingTextIntent(message: String, imageUri: Uri? = null, subject: String = "") {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, message)
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    if (imageUri != null) {
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
        intent.type = "image"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    startActivity(Intent.createChooser(intent, message))
}

fun Activity.showSurveyToast(myContext:Context, view: View, message: String, imageIcon: Int) {
    val snackBar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
    val customSnackView = layoutInflater.inflate(R.layout.custom_toast_survey, null)
    val textView = customSnackView.findViewById<AppCompatTextView>(R.id.tvMessage)
    textView.text = message

    // set the image of the ImageView of the message
    val imageView = customSnackView.findViewById<AppCompatImageView>(R.id.ivIcon)
    imageView.setImageDrawable(
        ContextCompat.getDrawable(
            myContext,
            imageIcon
        )
    )
    snackBar.view.setBackgroundColor(Color.TRANSPARENT)
    val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout? ?: return
    snackBarLayout.setPadding(0, 0, 0, 0)
    snackBarLayout.addView(customSnackView, 0)
    snackBar.show()
}

@SuppressLint("WrongViewCast")
fun Activity.showNetworkOnlineSnackBar() {
    val snackbar = Snackbar.make(
        findViewById(android.R.id.content),
        "",
        Snackbar.LENGTH_LONG
    ).apply {
        view.translationY = (-20).toFloat()
    }
    val snackBarLayout: Snackbar.SnackbarLayout = snackbar.view as (Snackbar.SnackbarLayout)
    val textView = snackBarLayout.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    textView.visibility = View.INVISIBLE
    val snackViewBinding:LayoutViewOnlineBinding = DataBindingUtil.inflate(
        this.layoutInflater,
        R.layout.layout_view_online,
        null,
        true
    )
    snackViewBinding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
    snackViewBinding.brandThemeColors = Colors

    val params: ViewGroup.MarginLayoutParams = snackbar.view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(Utils.dpToPx(this, 20), 0, Utils.dpToPx(this, 20), 0)
    snackbar.view.layoutParams = params
    snackBarLayout.setPadding(0, 0, 0, 0)
    snackBarLayout.addView(snackViewBinding.root, 0)
    ViewCompat.setElevation(snackbar.view, 0f)
    snackbar.show()
}
