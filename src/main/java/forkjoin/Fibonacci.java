package forkjoin;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

/**
 * computes the fibonacci sequence up to the Nth Fibonacci number with various methods.
 * 
 * @author Martin Charlesworth
 *
 */
public class Fibonacci {

	private static class FibIterative {

		private long N;

		private void fib(long a, long b) {
			long curr = 0;
			for(int iter = 0; iter <= N; ++iter) {
				System.out.print(curr + " ");
				curr = a + b;
				b = a;
				a = curr;
			}
		}
		
		public void run(long N) {
			this.N = N;
			fib(0,1);
		}
	}

	// first version - results in a huge number of recursive calls!
	private static class FibRecursive1 {
		private long calls = 0;
		
		private long fib(long n) {
			++calls;
			if (n <= 2) {
				return 1;
			} else {
				return fib(n-1) + fib(n-2);
			}
		}
		
		public void run(long n) {
			for (int i = 1; i <= n; ++i)
				System.out.print(fib(i) + " ");
			System.out.println("\ncalls=" + calls);
		}
	}

	// second version - much more efficient
	private static class FibRecursive2 {
		private long curr = 0;
		private long iter = 0;
		private long N;
		private long calls = 0;
		
		private void fib(long a, long b) {
			++calls;
			if (iter > N) return;
			System.out.print(curr + " ");
			curr = a + b;
			b = a;
			a = curr;
			++iter;
			fib(a, b);
		}

		public void run(long N) {
			this.N = N;
			fib(0,1);
			System.out.println("\ncalls=" + calls);
		}
	}
	
	public static void main(String[] args) {
		int N = 40;
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		System.out.println("\nIterative:");
		new FibIterative().run(N);
		System.out.println("\n" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");

		stopwatch = Stopwatch.createStarted();
		System.out.println("\nRecursive1:");
		new FibRecursive1().run(N);
		System.out.println("\n" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");

		stopwatch = Stopwatch.createStarted();
		System.out.println("\nRecursive2:");
		new FibRecursive2().run(N);
		System.out.println("\n" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");


	}
}
