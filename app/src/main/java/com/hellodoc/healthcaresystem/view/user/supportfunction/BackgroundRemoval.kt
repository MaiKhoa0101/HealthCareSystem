package com.hellodoc.healthcaresystem.view.user.supportfunction

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.opengl.EGLConfig
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.view.Surface
import androidx.media3.exoplayer.ExoPlayer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.opengles.GL10

sealed class BackgroundRemovalMode {
    object None : BackgroundRemovalMode()
    // Xóa màu đơn sắc, colorRGB là float array [R, G, B] từ 0f đến 1f
    data class ChromaKey(
        //xanh dương là
        val colorRGB: FloatArray = floatArrayOf(1f, 1f, 1f), // mặc định trắng
        val threshold: Float = 0.35f
    ) : BackgroundRemovalMode()
    object Segmentation : BackgroundRemovalMode()
}

@SuppressLint("ViewConstructor")
class ChromaKeyGLSurfaceView(
    context: Context,
    private val exoPlayer: ExoPlayer,
    private val chromaColor: FloatArray,
    private val threshold: Float
) : GLSurfaceView(context), GLSurfaceView.Renderer {

    private var program = 0
    private var textureId = 0
    private val texMatrix = FloatArray(16)
    private lateinit var videoSurfaceTexture: SurfaceTexture
    private var frameAvailable = false
    private val lock = Object()

    private val vertexShader = """
        attribute vec4 aPosition;
        attribute vec4 aTexCoord;
        uniform mat4 uTexMatrix;
        varying vec2 vTexCoord;
        void main() {
            gl_Position = aPosition;
            vTexCoord = (uTexMatrix * aTexCoord).xy;
        }
    """.trimIndent()

    private val fragmentShader = """
        #extension GL_OES_EGL_image_external : require
        precision mediump float;
        uniform samplerExternalOES uTexture;
        uniform vec3 uChromaColor;
        uniform float uThreshold;
        varying vec2 vTexCoord;
        void main() {
            vec4 color = texture2D(uTexture, vTexCoord);
            float diff = distance(color.rgb, uChromaColor);
            if (diff < uThreshold) {
                gl_FragColor = vec4(0.0);
            } else {
                gl_FragColor = color;
            }
        }
    """.trimIndent()

    // Quad phủ full màn hình
    private val vertexBuffer = ByteBuffer.allocateDirect(32)
        .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(floatArrayOf(-1f,-1f, 1f,-1f, -1f,1f, 1f,1f))
            position(0)
        }
    private val texBuffer = ByteBuffer.allocateDirect(32)
        .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(floatArrayOf(0f,0f, 1f,0f, 0f,1f, 1f,1f))
            position(0)
        }

    init {
        setEGLContextClientVersion(2)
        setEGLConfigChooser(8, 8, 8, 8, 0, 0)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        setZOrderOnTop(true)
        setRenderer(this)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onSurfaceCreated(p0: GL10, p1: javax.microedition.khronos.egl.EGLConfig) {
        // Compile shaders
        val vs = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER).also {
            GLES20.glShaderSource(it, vertexShader)
            GLES20.glCompileShader(it)
        }
        val fs = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER).also {
            GLES20.glShaderSource(it, fragmentShader)
            GLES20.glCompileShader(it)
        }
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vs)
            GLES20.glAttachShader(it, fs)
            GLES20.glLinkProgram(it)
        }

        // Tạo texture cho video
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        textureId = textures[0]
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        videoSurfaceTexture = SurfaceTexture(textureId).apply {
            setOnFrameAvailableListener {
                synchronized(lock) { frameAvailable = true }
                requestRender()
            }
        }

        // ✅ Post về main thread trước khi gọi exoPlayer
        val surface = Surface(videoSurfaceTexture)
        Handler(Looper.getMainLooper()).post {
            exoPlayer.setVideoSurface(surface)
        }

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        synchronized(lock) {
            if (frameAvailable) {
                videoSurfaceTexture.updateTexImage()
                videoSurfaceTexture.getTransformMatrix(texMatrix)
                frameAvailable = false
            }
        }

        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(program)

        val aPos = GLES20.glGetAttribLocation(program, "aPosition")
        val aTex = GLES20.glGetAttribLocation(program, "aTexCoord")
        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glEnableVertexAttribArray(aTex)
        GLES20.glVertexAttribPointer(aPos, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, 0, texBuffer)

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, "uTexMatrix"), 1, false, texMatrix, 0)
        GLES20.glUniform3f(GLES20.glGetUniformLocation(program, "uChromaColor"), chromaColor[0], chromaColor[1], chromaColor[2])
        GLES20.glUniform1f(GLES20.glGetUniformLocation(program, "uThreshold"), threshold)

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    fun release() {
        videoSurfaceTexture.release()
    }
}