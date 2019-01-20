# Demonstrate file locking

This illustrates the use of `FileChannel`'s lock mechanism

There is a Writer and a Reader program. Writer writes a set of integers to a file repeatedly and 
Reader reads thems repeatedly.

Writer maintains a count and at each iteration it increments the count. At the each iteration the
writer writes a set of integers in the file - all the intergers in the set are of the same value - 
the count at that particular iteration.

Reader reads off the set of integers from the file. Reader also maintains a count and increments the
count at each iteration of read. But at each iteration, it sleeps for a second.

## When running Writer first and then running the Reader

As soon as the Writer starts it starts writing the file. The count will increament pretty fast.

But as soon as the Reader starts, the writer will slow down. This is because Reader and Writer 
competes for the file lock. When the Reader acquires it, the Writer would have to wait - since the 
writer acquires the lock exclusively. 

As the Reader waits a seconds after reading the set of integers, the Writer would therefore wait 
about a second between releasing and re-acquiring the lock.

If the Reader is stopped (while the Writer still continues), writer will speed up again. This is 
because the Writer is no longer competing with the Reader for the file channel lock.

 
## When running Reader first and then running the Writer

As soon as the Reader starts it reads off the set of intergers from the file - the last set written
by the Writer. (Note that this version of Reader fails if the File does not exists or does not have
a set of integers to begin with)

Until the Writer starts it reads the same set at each iteration. But as soon as the Writer starts,
the writer starts writing new set of integers, starting from count 0. Reader will thereafter read 
off the new set.




