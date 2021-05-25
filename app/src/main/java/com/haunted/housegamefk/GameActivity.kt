package com.haunted.housegamefk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.random.nextInt

class GameActivity : AppCompatActivity() {

/*lateinit var Courutine_Dice : CoroutineScope*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


        findViewById<ImageView>(R.id.dice).setOnClickListener {
            /*  if(Courutine_Dice.isActive)*/
            /* Courutine_Dice =*/ MainScope().launch {


            var rotateDirection = arrayListOf(-1, 1).random()//
            var random_Buffer_XY = Random.nextBoolean()
            var rotation_Time: Long = 1
            var NumberOfRotations_begin = Random.nextInt(1..2)
            var NumberOfRotations_end = Random.nextInt(5..7)
            var Dice_scale: Float = 0.005F
//            begin.text = NumberOfRotations_begin.toString()
//            end.text = NumberOfRotations_end.toString()


            for (i in NumberOfRotations_begin..NumberOfRotations_end) {

                if (random_Buffer_XY) {
                    for (i in 0..89) {
                        it.rotationX = it.rotationX + rotateDirection
                        it.scaleX = it.scaleX + Dice_scale
                        it.scaleY = it.scaleY + Dice_scale
                        delay(rotation_Time)

                    }
                } else {
                    for (i in 0..89) {
                        it.rotationY = it.rotationY + rotateDirection
                        it.scaleX = it.scaleX + Dice_scale
                        it.scaleY = it.scaleY + Dice_scale
                        delay(rotation_Time)
                    }
                }




                findViewById<ImageView>(R.id.dice).setImageResource(
                    when ((1..6).random()) {
                        1 -> R.drawable.ic1
                        2 -> R.drawable.ic2
                        3 -> R.drawable.ic3
                        4 -> R.drawable.ic4
                        5 -> R.drawable.ic5
                        6 -> R.drawable.ic6
                        else -> R.drawable.ic1
                    }
                )

                if (random_Buffer_XY) {
                    for (i in 90..179) {
                        it.rotationX = it.rotationX + rotateDirection
                        it.scaleX = it.scaleX - Dice_scale
                        it.scaleY = it.scaleY - Dice_scale
                        delay(rotation_Time)
                    }

                } else {
                    for (i in 90..179) {
                        it.rotationY = it.rotationY + rotateDirection
                        it.scaleX = it.scaleX - Dice_scale
                        it.scaleY = it.scaleY - Dice_scale
                        delay(rotation_Time)
                    }

                }
            }

        }
        }
    }
}