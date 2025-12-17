package com.asdru.cardgame3.logic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BattleTimer(
  private val scope: CoroutineScope,
  private val onTick: (Int) -> Unit,
  private val onTimeout: () -> Unit
) {
  private var timerJob: Job? = null
  private var maxTimeSeconds: Int = 0
  private var currentTimeSeconds: Int = 0
  private var isPaused: Boolean = false

  fun init(seconds: Int) {
    maxTimeSeconds = seconds
    currentTimeSeconds = seconds
  }

  fun start(checkPauseConditions: () -> Boolean) {
    if (maxTimeSeconds <= 0) return
    timerJob?.cancel()
    currentTimeSeconds = maxTimeSeconds

    timerJob = scope.launch {
      while (isActive) {
        delay(1000)
        if (!checkPauseConditions()) {
          currentTimeSeconds--
          onTick(currentTimeSeconds)
          if (currentTimeSeconds <= 0) {
            onTimeout()
          }
        }
      }
    }
  }

  fun stop() {
    timerJob?.cancel()
  }

  fun reset() {
    if (maxTimeSeconds > 0) {
      currentTimeSeconds = maxTimeSeconds
      onTick(currentTimeSeconds)
    }
  }
}