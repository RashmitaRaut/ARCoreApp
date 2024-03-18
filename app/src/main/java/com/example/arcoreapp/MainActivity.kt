package com.example.arcoreapp
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.node.VideoNode

class MainActivity : AppCompatActivity() {

    // An instance of ArSceneView, which is a view for rendering Augmented Reality scenes.
    private lateinit var sceneView: ArSceneView
    // An instance of ExtendedFloatingActionButton, used for placing the model in the AR scene.
    lateinit var placeButton: ExtendedFloatingActionButton
    // An instance of ArModelNode, which represents a 3D model in the AR scene.
    private lateinit var modelNode: ArModelNode
    // An instance of MediaPlayer, used for playing video content in the AR scene.
    private lateinit var videoNode: VideoNode
    private lateinit var mediaPlayer:MediaPlayer



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        placeButton = findViewById(R.id.place)


        sceneView = findViewById<ArSceneView?>(R.id.sceneView).apply {
            this.lightEstimationMode = Config.LightEstimationMode.DISABLED
        }

        mediaPlayer = MediaPlayer.create(this,R.raw.ad)

        placeButton.setOnClickListener {
            placeModel()
        }



        videoNode = VideoNode(sceneView.engine, scaleToUnits = 0.7f, centerOrigin = Position(y=-4f), glbFileLocation = "models/plane.glb", player = mediaPlayer, onLoaded = {_,_ ->
            mediaPlayer.start()
        })

        modelNode = ArModelNode(sceneView.engine,PlacementMode.INSTANT).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/leather_sofa.glb",
                scaleToUnits = 1f,
                centerOrigin = Position(-0.5f)

            )
            {
                sceneView.planeRenderer.isVisible = true
                val materialInstance = it.materialInstances[0]
            }
            onAnchorChanged = {
                placeButton.isGone = it != null
            }

        }
        sceneView.addChild(modelNode)
        modelNode.addChild(videoNode)

    }

    private fun placeModel(){
        modelNode.anchor()

        sceneView.planeRenderer.isVisible = false

    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }


}

