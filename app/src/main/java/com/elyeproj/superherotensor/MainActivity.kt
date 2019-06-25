package com.elyeproj.superherotensor

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.elyeproj.superherotensor.tensorflow.Classifier
import com.elyeproj.superherotensor.tensorflow.TensorFlowImageClassifier
import com.wonderkiln.camerakit.CameraKitImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val INPUT_WIDTH = 300
        private const val INPUT_HEIGHT = 300
        private const val IMAGE_MEAN = 128
        private const val IMAGE_STD = 128f
        private const val INPUT_NAME = "Mul"
        private const val OUTPUT_NAME = "final_result"
        private const val MODEL_FILE = "file:///android_asset/hero_stripped_graph.pb"
        private const val LABEL_FILE = "file:///android_asset/hero_labels.txt"
        val student_map = mutableMapOf<String,Int>()
    }

    private var classifier: Classifier? = null
    private var initializeJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        student_map["ankit"]=0
        student_map["kartik"]=0
        student_map["diwakar"]=0
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeTensorClassifier()
        buttonRecognize.setOnClickListener {
            setVisibilityOnCaptured(false)
            cameraView.captureImage {
                onImageCaptured(it)
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, display_data::class.java)
                intent.putExtra("my_map", student_map as Serializable)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



            private fun onImageCaptured(it: CameraKitImage) {
        val bitmap = Bitmap.createScaledBitmap(it.bitmap, INPUT_WIDTH, INPUT_HEIGHT, false)
        showCapturedImage(bitmap)

        classifier?.let {
            try {
                showRecognizedResult(it.recognizeImage(bitmap))
            } catch (e: java.lang.RuntimeException) {
                Log.e(TAG, "Crashing due to classification.closed() before the recognizer finishes!")
            }
        }
    }

    private fun showRecognizedResult(results: MutableList<Classifier.Recognition>) {
        runOnUiThread {
            setVisibilityOnCaptured(true)
            if (results.isEmpty()) {
                textResult.text = getString(R.string.result_no_hero_found)
                Log.d("name  "," not hero")
            } else {
                val listIterator = results.iterator()
                for (person in listIterator){
                    val hero = person.title
                    val confidence = person.confidence
                    textResult.text = when {
                        confidence > 0.50 -> getString(R.string.result_confident_hero_found, hero)
                        confidence > 0.20 -> getString(R.string.result_think_hero_found, hero)
                        else -> getString(R.string.result_maybe_hero_found, hero)
                    }
                    Log.d("tst","out of loop")
                    if(confidence>0.50){
                        student_map[hero]= student_map[hero]!! + 1

                    }
                }
            }
            for(enter in student_map)
                Log.d("from amp", enter.value.toString())
        }
    }

    private fun showCapturedImage(bitmap: Bitmap?) {
        runOnUiThread {
            imageCaptured.visibility = View.VISIBLE
            imageCaptured.setImageBitmap(bitmap)
        }
    }

    private fun setVisibilityOnCaptured(isDone: Boolean) {
        buttonRecognize.isEnabled = isDone
        if (isDone) {
            imageCaptured.visibility = View.VISIBLE
            textResult.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        } else {
            imageCaptured.visibility = View.GONE
            textResult.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun initializeTensorClassifier() {
        initializeJob = launch {
            try {
                classifier = TensorFlowImageClassifier.create(
                        assets, MODEL_FILE, LABEL_FILE, INPUT_WIDTH, INPUT_HEIGHT,
                        IMAGE_MEAN, IMAGE_STD, INPUT_NAME, OUTPUT_NAME)

                runOnUiThread {
                    buttonRecognize.isEnabled = true
                }
            } catch (e: Exception) {
                throw RuntimeException("Error initializing TensorFlow!", e)
            }
        }
    }

    private fun clearTensorClassifier() {
        initializeJob?.cancel()
        classifier?.close()
    }

    override fun onResume() {
        super.onResume()
        cameraView.start()
    }

    override fun onPause() {
        super.onPause()
        cameraView.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearTensorClassifier()
    }
}
/*
val hero = results[0].title
                    val confidence = results[0].confidence
                    textResult.text = when {
                        confidence > 0.50 -> getString(R.string.result_confident_hero_found, hero)
                        confidence > 0.20 -> getString(R.string.result_think_hero_found, hero)
                        else -> getString(R.string.result_maybe_hero_found, hero)
                    }
                    Log.d("tst","out of loop")
                    if(confidence>0.50){
                        student_map[hero]= student_map[hero]!! + 1

                    }
 */