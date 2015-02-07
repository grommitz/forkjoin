package forkjoin;

import java.util.concurrent.RecursiveAction;

/**
 * computes the fibonacci sequence up to max.
 * 
 * @author Martin Charlesworth
 *
 */
@SuppressWarnings("serial")
public class Fib extends RecursiveAction {

	final long max;
	long n = 0;
	
	public Fib(long max) {
		this.max = max;
	}
	
	// iterative version
	private void fib(long a, long b) {
		while(n < max) {
			//System.out.print(n + " ");
			n = a + b;
			b = a;
			a = n;
		}
	}

	// recursive version
	private void fibR(long a, long b) {
		if (a > max) return;
		
		//System.out.print(n + " ");
		n = a + b;
		b = a;
		a = n;
		fib(a, b);
	}

	@Override
	protected void compute() {
		fibR(0,1);
	}
	
}
