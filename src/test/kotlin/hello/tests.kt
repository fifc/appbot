package hello 

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

class TestSource {
  @Test
  fun f() {
    println("running test: workload(n = 1) ...")
    val df = async(CommonPool) {
        workload(n = 1)
    }

    runBlocking {
      df.await()
    }
  }
}
