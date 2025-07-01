package com.hellodoc.healthcaresystem.skeleton

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch
import kotlin.random.Random

fun Modifier.discordClick(onClick: () -> Unit): Modifier = composed {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f), label = "scale"
    )

    var size by remember { mutableStateOf(IntSize.Zero) }
    val particles = remember { mutableStateListOf<Particle>() }
    val scope = rememberCoroutineScope()

    this
        .onGloballyPositioned { size = it.size }
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    pressed = true
                    scope.launch {
                        generateParticles(particles, size)
                    }
                    tryAwaitRelease()
                    pressed = false
                    onClick()
                }
            )
        }
        .then(
            Modifier.drawParticles(particles)
        )
}
data class Particle(
    val angle: Float,
    var radius: Float = 0f,
    val color: Color = Color(0xFF00BCD4),
    val alpha: Float = 1f
)

@Composable
fun Modifier.drawParticles(particles: List<Particle>): Modifier = this.then(
    Modifier.drawWithContent {
        drawContent()

        particles.forEach {
            val x = size.width / 2 + it.radius * kotlin.math.cos(it.angle)
            val y = size.height / 2 + it.radius * kotlin.math.sin(it.angle)
            drawCircle(
                color = it.color.copy(alpha = it.alpha),
                radius = 6f,
                center = Offset(x, y)
            )
        }
    }
)

suspend fun generateParticles(particles: MutableList<Particle>, size: IntSize) {
    particles.clear()
    val count = 10
    repeat(count) {
        particles.add(
            Particle(
                angle = Random.nextFloat() * (2 * Math.PI.toFloat()),
                color = Color(0xFF00BCD4) // bạn có thể random hoặc chỉnh theo theme
            )
        )
    }

    val duration = 300
    val steps = 30
    val delay = duration / steps
    val minDim = minOf(size.width, size.height)

    repeat(steps) { step ->
        particles.forEach {
            it.radius = (step / steps.toFloat()) * (minDim / 3f)
        }
        kotlinx.coroutines.delay(delay.toLong())
    }

    particles.clear()
}

