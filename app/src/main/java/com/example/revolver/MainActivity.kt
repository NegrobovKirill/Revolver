package com.example.revolver

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.*
import android.view.MotionEvent
import android.view.View
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.example.revolver.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    var step:Int = 0
    var baraban_step:Int = 0

    lateinit var bindingClass: ActivityMainBinding

    var floatRotation: Float = 0F

    var array: IntArray = intArrayOf(0,0,0,0,0,0)
    var arrayBoolean: BooleanArray = booleanArrayOf(true,true,true,true,true,true)

    private lateinit var soundPool: SoundPool

    private var mShoot:Int = 0
    private var mEmpty:Int = 0
    private var mReload:Int = 0
    private var mRoll:Int = 0

    private var barabanRotation:Int = 0

    private lateinit var mHandler: Handler
    private lateinit var reloadHandler: Handler

    val  effect:VibrationEffect? = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)

        setContentView(bindingClass.root)

        array[0] = bindingClass.iBullet0.id
        array[1] = bindingClass.iBullet1.id
        array[2] = bindingClass.iBullet2.id
        array[3] = bindingClass.iBullet3.id
        array[4] = bindingClass.iBullet4.id
        array[5] = bindingClass.iBullet5.id

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder().setMaxStreams(3)
            .setAudioAttributes(attributes)
            .build()

        mShoot = soundPool!!.load(this, R.raw.shoot, 1)
        mEmpty = soundPool!!.load(this, R.raw.empty_shoot, 1)
        mReload = soundPool!!.load(this, R.raw.reload, 1)
        mRoll = soundPool!!.load(this, R.raw.baraban_roll, 1)

        bindingClass.bRoll.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mHandler = Handler(Looper.myLooper()!!)
                        mHandler.postDelayed(runnable, 0)
                    }
                    MotionEvent.ACTION_UP -> {

                        mHandler.removeCallbacks(runnable);
                    }
                }

                return v?.onTouchEvent(event) ?: false
            }
        })

    }

    fun onClickShoot(view: View){

        floatRotation = floatRotation+Constants.ValConstantRotation

        if(arrayBoolean[nowBullet(step)] == true){
            //Выстрел
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            if(bindingClass.sVibration.isChecked == true){
                vibrator.vibrate(effect)
            }

            soundPool.play(mShoot,0.3F,0.3F,1,0,1F)
            soundPool.play(mRoll,0.3F,0.3F,2,0,1F)

            arrayBoolean[nowBullet(step)] = false
            when(array[nowBullet(step)]){
                bindingClass.iBullet0.id -> bindingClass.iBullet0.imageAlpha = 0
                bindingClass.iBullet1.id -> bindingClass.iBullet1.imageAlpha = 0
                bindingClass.iBullet2.id -> bindingClass.iBullet2.imageAlpha = 0
                bindingClass.iBullet3.id -> bindingClass.iBullet3.imageAlpha = 0
                bindingClass.iBullet4.id -> bindingClass.iBullet4.imageAlpha = 0
                bindingClass.iBullet5.id -> bindingClass.iBullet5.imageAlpha = 0
            }
        }
        else
        {
            //Звук щелчка
            soundPool.play(mEmpty,0.3F,0.3F,1,0,1F)
            soundPool.play(mRoll,0.3F,0.3F,2,0,1F)
        }
        step++
        if(step >=6){
            step = nowBullet(step)
        }

        baraban_roll(baraban_step)

        baraban_step++
        if(baraban_step >=2){
            baraban_step = nowBullet(baraban_step)
        }

        bindingClass.lRevolver.animate().rotation(floatRotation)

        val animation_kurok = RotateAnimation(0F, 30F, 1,0.4F,1, 0.8F)
        animation_kurok.duration = 100
        bindingClass.kurok.startAnimation(animation_kurok)

        val animation_udarnik = RotateAnimation(0F,-25F,1,0.5F,1,0.8F)
        animation_udarnik.duration = 100
        bindingClass.udarnik.startAnimation(animation_udarnik)

    }

    fun onClickReload(view: View) {

        soundPool.play(mReload, 1F, 1F, 0, 0, 1F)

        for (index in 0..5){
            arrayBoolean[index] = true
        }
        bindingClass.iBullet0.imageAlpha = 255
        bindingClass.iBullet1.imageAlpha = 255
        bindingClass.iBullet2.imageAlpha = 255
        bindingClass.iBullet3.imageAlpha = 255
        bindingClass.iBullet4.imageAlpha = 255
        bindingClass.iBullet5.imageAlpha = 255

    }

    fun onClickiBullet(view: View){
        var index = 0
        while(index<6){
            if(array[index] == view.id)
            {
                break
            }
            index++
        }
        if (arrayBoolean[index] == false){
            //Заряжаем

            soundPool.play(mReload,1F,1F,0,0,1F)
            arrayBoolean[index] = true
            when(view.id){
                bindingClass.iBullet0.id -> bindingClass.iBullet0.imageAlpha = 255
                bindingClass.iBullet1.id -> bindingClass.iBullet1.imageAlpha = 255
                bindingClass.iBullet2.id -> bindingClass.iBullet2.imageAlpha = 255
                bindingClass.iBullet3.id -> bindingClass.iBullet3.imageAlpha = 255
                bindingClass.iBullet4.id -> bindingClass.iBullet4.imageAlpha = 255
                bindingClass.iBullet5.id -> bindingClass.iBullet5.imageAlpha = 255
            }
        }
    }

    var runnable: Runnable = object : Runnable {
        override fun run() {

            soundPool.play(mRoll,1F,1F,0,0,1F)
            floatRotation = floatRotation + Constants.ValConstantRotation

            baraban_roll(baraban_step)

            baraban_step++
            if (baraban_step >= 6) {
                baraban_step = nowRotation(baraban_step)
            }

            step++
            if (step >= 6) {
                step = nowBullet(step)
            }
            bindingClass.lRevolver.animate().setDuration(100).rotation(floatRotation)

            mHandler.postDelayed(this, 100)

        }
    }

    fun baraban_roll(int: Int){
        if (nowRotation(int) == 0){
            bindingClass.baraban.imageAlpha = 0
        }else
            bindingClass.baraban.imageAlpha = 255
    }

}

object Constants{
    const val ValConstantRotation = 60F
}

fun nowBullet(int: Int) : Int{
    return (int % 6)
}

fun nowRotation(int: Int): Int{
    return (int%2)
}

