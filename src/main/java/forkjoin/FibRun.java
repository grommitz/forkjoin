package forkjoin;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;


/**
 * Tests various scenarios using fork/join pool.
 * 
 * Using my dell studio with an i7 with 4 cores and 8 threads.
 * 
 * Fist F/J method runs every one of 2,000,000 runs in a separate thread. The overhead must kill it because it is way 
 * slower than single threaded.
 * 
 * Second F/J method decomposes it into a fixed number of threads running a portion of the jobs in each. Performance 
 * increases from 2 to ~ 12 threads reaching a max of about a 5x speedup over single threaded. This stays about the same
 * with thread count up to several tens of thousands, after that it starts tio tail off due to the overhead of so 
 * many threads.
 * 
 * @author Martin Charlesworth
 *
 */
@SuppressWarnings("serial")
public class FibRun {

	private static final int iterations = 2_000_000;
	private static final int parallelism = Runtime.getRuntime().availableProcessors();
	private final ForkJoinPool forkJoinPool = new ForkJoinPool(parallelism);
	private static final long max = (long) (Long.MAX_VALUE * 0.8);

	public static void main(String[] args) {
		
		for (int r=0; r<100; r++) {
			Stopwatch s = Stopwatch.createStarted();
		
			new FibRun().runSingleThreaded();
			final long t0 = s.elapsed(TimeUnit.MILLISECONDS);
			System.out.println("single threaded = " + t0 + " ms");
			
			new FibRun().runParallel(new FibTask1());
			final long t1 = s.elapsed(TimeUnit.MILLISECONDS) - t0;
			System.out.println("with fork/join(" + parallelism + " cpus), 1 thread per run = " + t1 + " ms");
	
			int threads = (r+1) * 10;
			new FibRun().runParallel(new FibTask2(threads));
			final long t2 = s.elapsed(TimeUnit.MILLISECONDS) - t0 - t1;
			System.out.println("with fork/join(" + parallelism + " cpus), " + threads + " threads = " + t2 + " ms");
		}
	}

	void runSingleThreaded() {
		for (int i=0; i<iterations; i++) {
			//System.out.print(i + ") ");
			new Fib(max).compute();
			//System.out.println();
		}
	}
	
	void runParallel(RecursiveAction action) {		
		forkJoinPool.invoke(action);
	}

	// every run is in a separate thread. the overhead kills us here. 
	static class FibTask1 extends RecursiveAction {
		@Override
		protected void compute() {
			List<RecursiveAction> forks = new LinkedList<>();
			for (int i=0; i<iterations; i++) {
				Fib task = new Fib(max);
				forks.add(task);
				task.fork();
			}
			for (RecursiveAction task : forks) {
				task.join();
			}
		}
	}
	
	// splitting into a more reasonable number of threads is much better, once JIT has
	// optimised everything, ie the first run is slower but then much better.
	static class FibTask2 extends RecursiveAction {
		private final int threads;
		public FibTask2(int threads) {
			this.threads = threads;
		}
		@Override
		protected void compute() {
			List<RecursiveAction> forks = new LinkedList<>();
			for (int i=0; i<threads; i++) {
				FibSubTask2 task = new FibSubTask2(iterations / threads);
				forks.add(task);
				task.fork();
			}
			for (RecursiveAction task : forks) {
				task.join();
			}
		}

		class FibSubTask2 extends RecursiveAction {
			private int runs;

			public FibSubTask2(int runs) {
				this.runs = runs;
			}

			@Override
			protected void compute() {
				for (int i=0; i<runs; i++) {
					new Fib(max).compute();
				}
			}
		}
	}
	
	
}
