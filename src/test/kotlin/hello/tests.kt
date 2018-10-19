package hello 

import kotlinx.coroutines.*
import org.junit.Test

class TestSource {
  @Test
  fun f() {
    println("running test: workload(n = 1) ...")
    val df = GlobalScope.async {
        workload(n = 1)
    }

    runBlocking {
      df.await()
    }
  }
}
