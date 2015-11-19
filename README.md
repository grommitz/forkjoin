# forkjoin

Tests various scenarios using fork/join pool.

Using my dell studio with an i7 with 4 cores and 8 threads.

Fist F/J method runs every one of 2,000,000 runs in a separate thread. The overhead must kill it because it is way 
slower than single threaded.

Second F/J method decomposes the 2M jobs into a fixed number of threads running a portion of the jobs in each. Performance 
increases from 2 to approx. 12 threads reaching a max of about a 5x speedup over single threaded. This stays roughly
constant with thread count up to several tens of thousands, after that it starts to tail off due to the overhead of so 
many threads.

MC
